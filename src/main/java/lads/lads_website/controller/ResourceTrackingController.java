package lads.lads_website.controller;

import lads.lads_website.domain.Player;
import lads.lads_website.domain.ResourceTracking;
import lads.lads_website.domain.ResourceTrackingValues;
import lads.lads_website.domain.projections.ResourceTypeOnly;
import lads.lads_website.forms.RTForm;
import lads.lads_website.forms.RTPrepForm;
import lads.lads_website.forms.RTValuesForm;
import lads.lads_website.forms.list_wrappers.ResourceTrackingList;
import lads.lads_website.service.PlayerService;
import lads.lads_website.service.ResourceTrackingService;
import lads.lads_website.service.ResourceTrackingValuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.apache.commons.text.WordUtils.capitalizeFully;
import static org.apache.commons.text.WordUtils.uncapitalize;

@Controller
public class ResourceTrackingController {

    private final ResourceTrackingService resourceTrackingService;
    private final ResourceTrackingValuesService resourceTrackingValuesService;
    private final PlayerService playerService;

    @Autowired
    public ResourceTrackingController(ResourceTrackingService resourceTrackingService, ResourceTrackingValuesService resourceTrackingValuesService, PlayerService playerService) {
        this.resourceTrackingService = resourceTrackingService;
        this.resourceTrackingValuesService = resourceTrackingValuesService;
        this.playerService = playerService;
    }

    @GetMapping("/resourceTracking/addPrep")
    public String routeToResourceTrackingAdd(Model model) {
        return "/resourceTracking/resourceTrackingFormPrep";
    }

    @PostMapping("/resourceTracking/addPrep")
    public RedirectView routeToForm(RTPrepForm prepareForm, RedirectAttributes redirectAttributes, Principal principal) {
        RedirectView rv = new RedirectView("/resourceTracking/add");
        rv.setExposeModelAttributes(false);
        if (prepareForm.getSelectPreviousDay()) {
            Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
            Optional<ResourceTracking> rtOpt = resourceTrackingService.getMostRecentResourceTracking(player.getId());
            rtOpt.ifPresent(resourceTracking -> redirectAttributes.addFlashAttribute("lastRT", resourceTracking));
            if (rtOpt.isPresent()) {
                ResourceTracking rt = rtOpt.get();
                redirectAttributes.addFlashAttribute("lastRT", rt);
                List<ResourceTrackingValues> rtVals = rt.getResourceTrackingValues();
                Map<String, ResourceTrackingValues> rtValMap = new HashMap<>();
                rtVals.forEach(resourceTrackingValues -> rtValMap.put(capitalizeFully(resourceTrackingValues.getResourceType()),resourceTrackingValues));
                redirectAttributes.addFlashAttribute("lastRTVals", rtValMap);
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "No previous resources recorded, default values loaded instead.");
            }
        }
        return rv;
    }

    @GetMapping("/resourceTracking/add")
    public String getResourceTrackingForm(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        List<ResourceTypeOnly> resourceTypeOnlyList = resourceTrackingValuesService.getAllResourceTypes();
        List<String> resourceTypes = new ArrayList<>();

        resourceTypeOnlyList.forEach(resourceTypeOnly -> resourceTypes.add(capitalizeFully(resourceTypeOnly.getResourceType())));

        model.addAttribute("player", player);
        model.addAttribute("resourceTypes", resourceTypes);

        return "/resourceTracking/add";
    }

    @PostMapping("/resourceTracking/add")
    public String saveResourceTrackingFromForm(RTForm rtForm, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        ResourceTracking rt = new ResourceTracking();

        rt.setAddDate(LocalDateTime.now());
        rt.setDiamonds(rtForm.getDiamonds());
        rt.setPurpleDiamonds(rtForm.getPurpleDiamonds());
        rt.setDeepspaceWish(rtForm.getDeepspaceWish());
        rt.setEmpyreanWish(rtForm.getEmpyreanWish());
        rt.setGold(rtForm.getGold());
        rt.setAwakeningHeartSr(rtForm.getAwakeningHeartSr());
        rt.setAwakeningHeartSsr(rtForm.getAwakeningHeartSsr());
        rt.setPlayer(player);

        rt = resourceTrackingService.addNewResourceTracking(rt);

        for (RTValuesForm rtValsForm : rtForm.getRtValuesForms()) {
            ResourceTrackingValues rtVals = new ResourceTrackingValues();
            rtVals.setResourceTracking(rt);
            rtVals.setResourceType(uncapitalize(rtValsForm.getResourceTypeName(), ' '));
            rtVals.setN(rtValsForm.getN());
            rtVals.setR(rtValsForm.getR());
            rtVals.setSr(rtValsForm.getSr());
            rtVals.setSsr(rtValsForm.getSsr());
            rtVals.setGeneral(rtValsForm.getGeneral());
            resourceTrackingValuesService.addNewResourceTrackingValue(rtVals);
        }
        return "redirect:/home";
    }

    // todo: refactor this to look cleaner, maybe create helper class as needed
    @GetMapping("resourceTracking/list")
    public String getResourceTrackingList(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        List<ResourceTrackingList> resourceTrackingLists = new ArrayList<>();
        List<ResourceTracking> resources = resourceTrackingService.getAllResourceTrackingByPlayerId(player.getId());

        int wishesGainedOverTime = 0;
        int wishesGainedByDayPositive = 0;
        int positiveDaysTracked = 0;
        int avgWishesGainedPerDay = 0;
        int previousWishTotal = 0;
        for (int i = 0; i < resources.size(); i++) {
            ResourceTracking resource = resources.get(i);
            ResourceTrackingList list = new ResourceTrackingList();
            list.setResourceTracking(resource);
            int currentWishes = resource.getDeepspaceWish() + (int)Math.floor(resource.getDiamonds()/150f);
            list.setTotalWishes(currentWishes);

            int wishesGainedSinceLastEntry = 0;
            if (i > 0) {
                ResourceTracking previous = resources.get(i - 1);
                wishesGainedSinceLastEntry = currentWishes - previousWishTotal;
                if (wishesGainedSinceLastEntry >= 0) {
                    positiveDaysTracked++;
                    long days = ChronoUnit.DAYS.between(previous.getAddDate().toLocalDate(), resource.getAddDate().toLocalDate());
                    int wishesGainedByDay = Math.round((float) wishesGainedSinceLastEntry / days);
                    wishesGainedByDayPositive += wishesGainedByDay;
                    avgWishesGainedPerDay = wishesGainedByDayPositive / positiveDaysTracked;
                }
            }

            wishesGainedOverTime += wishesGainedSinceLastEntry;

            list.setWishesGainedSinceLastEntry(wishesGainedSinceLastEntry);
            list.setWishesGainedOverTime(wishesGainedOverTime);
            list.setAverageWishesGainedPerDay(avgWishesGainedPerDay);

            previousWishTotal = currentWishes;
            resourceTrackingLists.add(list);
        }
        model.addAttribute("resources", resourceTrackingLists);

        return "/resourceTracking/resourceTrackingList";
    }

}
