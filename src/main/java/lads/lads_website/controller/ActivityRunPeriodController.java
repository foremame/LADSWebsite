package lads.lads_website.controller;

import lads.lads_website.domain.ActivityRunPeriod;
import lads.lads_website.domain.Banner;
import lads.lads_website.domain.Event;
import lads.lads_website.domain.Player;
import lads.lads_website.forms.ActivityForm;
import lads.lads_website.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class ActivityRunPeriodController {

    private final ActivityRunPeriodService activityRunPeriodService;
    private final EventService eventService;
    private final BannerService bannerService;
    private final PlayerService playerService;
    private final CardPullService cardPullService;

    @Autowired
    public ActivityRunPeriodController(ActivityRunPeriodService activityRunPeriodService, EventService eventService, BannerService bannerService, PlayerService playerService, CardPullService cardPullService) {
        this.activityRunPeriodService = activityRunPeriodService;
        this.eventService = eventService;
        this.bannerService = bannerService;
        this.playerService = playerService;
        this.cardPullService = cardPullService;
    }

    @GetMapping("/activityRunPeriod/add")
    public String getRunPeriodForm(Model model) {
        model.addAttribute("banners", bannerService.getAllBanners());
        model.addAttribute("events", eventService.getAllEvents());
        return "/activityRunPeriod/addNewRunPeriod";
    }

    @PostMapping("/activityRunPeriod/add")
    public String addNewActivityRunPeriod(ActivityForm activityForm) {
        ActivityRunPeriod activityRunPeriod = new ActivityRunPeriod();
        activityRunPeriod.setActivityRunType(activityForm.getActivityType());
        activityRunPeriod.setRerun(true);
        LocalDate startDate = LocalDate.parse(activityForm.getStartDate());
        LocalDate endDate = LocalDate.parse(activityForm.getEndDate());
        activityRunPeriod.setStartDate(startDate);
        activityRunPeriod.setEndDate(endDate);
        Optional<ActivityRunPeriod> activityRunPeriodOptional;
        if (activityForm.getActivityType().equals("Event")) {
            Event event = eventService.getEventById(activityForm.getEventId()).orElseThrow(()->new RuntimeException("No event found."));
            activityRunPeriod.setEvent(event);
            activityRunPeriodOptional = activityRunPeriodService.getMostRecentActivityRunPeriodForActivity("Event", activityForm.getEventId());
        }
        else {
            Banner banner = bannerService.getBannerById(activityForm.getBannerId()).orElseThrow(()->new RuntimeException("No banner found"));
            activityRunPeriod.setBanner(banner);
            activityRunPeriodOptional = activityRunPeriodService.getMostRecentActivityRunPeriodForActivity("Banner", activityForm.getBannerId());
        }
        if (activityRunPeriodOptional.isEmpty()) {
            throw new RuntimeException("This is a new run of an event/banner, no prior run can be found. If you wish to add a new event/banner, please use the correct form");
        }
        ActivityRunPeriod lastRun = activityRunPeriodOptional.get();
        activityRunPeriod.setRunNum(lastRun.getRunNum()+1);
        activityRunPeriodService.addNewActivityRunPeriod(activityRunPeriod);

        return "redirect:/home";
    }

    @GetMapping("/activityRunPeriod/listBanners")
    public String getActivityRunPeriodBannerList(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        List<ActivityRunPeriod> activityRunPeriods = activityRunPeriodService.getAllBannerRuns();
        Map<Long, Integer> cardPullsByActivityRunPeriodId = new HashMap<>();

        for (ActivityRunPeriod arp : activityRunPeriods) {
            int pulls = cardPullService.getNumberOfPullsForBanner(arp.getId(), player.getId());
            cardPullsByActivityRunPeriodId.put(arp.getId(), pulls);
        }

        model.addAttribute("activityRunPeriods", activityRunPeriods);
        model.addAttribute("cardPullMap", cardPullsByActivityRunPeriodId);
        return "/activityRunPeriod/activityRunPeriodBannerList";
    }

    @GetMapping("/activityRunPeriod/listEvents")
    public String getActivityRunPeriodEventList(Model model) {
        List<ActivityRunPeriod> activityRunPeriods = activityRunPeriodService.getAllEventRuns();

        model.addAttribute("activityRunPeriods", activityRunPeriods);
        return "/activityRunPeriod/activityRunPeriodEventList";
    }

    @RequestMapping(value="/activityRunPeriod/validateRunPeriodForm", method= RequestMethod.GET)
    @ResponseBody
    public List<String> validateRunPeriodForm(String startDate, String endDate, String activityType, Long activityId) {
        List<String> validationResults = new ArrayList<>();

        LocalDate startDateParsed = null;
        try {
            startDateParsed = LocalDate.parse(startDate);
        } catch (DateTimeParseException e) {
            // add error message to validation results
            validationResults.add("Start date provided: " + startDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        LocalDate endDateParsed = null;
        try {
            endDateParsed = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            validationResults.add("End date provided: " + endDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        if (startDateParsed != null && endDateParsed != null) {
            List<ActivityRunPeriod> runs = activityRunPeriodService.getAllRunsBetweenGivenStartAndDateForActivity(activityType, activityId, startDateParsed, endDateParsed);
            if (!runs.isEmpty()) {
                ActivityRunPeriod firstRun = runs.getFirst();
                // add error message to validation results
                String name = activityType.equals("Event") ? firstRun.getEvent().getName() : firstRun.getBanner().getName();
                validationResults.add("A run of the " + activityType + " " + name + " already exists within the given run period (" + startDate + "/" + endDate + ").");
            }
        }

        return validationResults;
    }
}
