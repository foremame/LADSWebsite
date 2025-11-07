package lads.lads_website.controller;

import lads.lads_website.backend_logic.LevelingPlanning.CardLevelingCost;
import lads.lads_website.backend_logic.LevelingPlanning.StaminaCost;
import lads.lads_website.domain.*;
import lads.lads_website.forms.LevelingPlanningForm;
import lads.lads_website.forms.list_wrappers.PlayerCardList;
import lads.lads_website.forms.subforms.LevelingPlanningSubForm;
import lads.lads_website.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.*;

@Controller
public class LevelingPlanningController {

    private final AscensionCostService ascensionCostService;
    private final LevelingCostService levelingCostService;
    private final PlayerBountyService playerBountyService;
    private final PlayerService playerService;
    private final PlayerCardService playerCardService;
    private final ResourceTrackingService resourceTrackingService;

    @Autowired
    public LevelingPlanningController(AscensionCostService ascensionCostService, LevelingCostService levelingCostService, PlayerBountyService playerBountyService, PlayerService playerService, PlayerCardService playerCardService, ResourceTrackingService resourceTrackingService) {
        this.ascensionCostService = ascensionCostService;
        this.levelingCostService = levelingCostService;
        this.playerBountyService = playerBountyService;
        this.playerService = playerService;
        this.playerCardService = playerCardService;
        this.resourceTrackingService = resourceTrackingService;
    }

    @GetMapping("/levelingPlanning/startPrep")
    public String getLevelingPrepPage(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Cannot load player information."));
        List<PlayerCard> playerCards = playerCardService.getAllPlayerCardsForGivenPlayerId(player.getId());
        List<PlayerCardList> playerCardLists = new ArrayList<>();
        playerCards.forEach(pc -> playerCardLists.add(new PlayerCardList(pc.getId(), pc.getCard().getName())));

        model.addAttribute("playerCards", playerCardLists);
        model.addAttribute("rarities", new String[]{"3 Star", "4 Star", "5 Star"});
        model.addAttribute("stellacrums", new String[]{"Amber", "Emerald", "Ruby", "Violet", "Sapphire", "Pearl"});

        return "/levelingPlanning/levelingPrep";
    }

    @PostMapping("/levelingPlanning/levelingPlanning")
    public String getCardLevelingPage(LevelingPlanningForm levelingPlanningForm, Principal principal, Model model) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Cannot load player information."));
        List<BountyReward> playerBounties = playerBountyService.getAllBountyRewardsForPlayer(player.getId());

        ResourceTracking currentPlayerResources = resourceTrackingService.getMostRecentResourceTracking(player.getId()).orElseGet(resourceTrackingService::getEmpty);

        List<CardLevelingCost> cardLevelingCosts = getCardLevelingCosts(levelingPlanningForm);
        CardLevelingCost totalLevelingCost = new CardLevelingCost();
        cardLevelingCosts.forEach(totalLevelingCost::add);

        CardLevelingCost currentResources = new CardLevelingCost().loadFromResourceTracking(currentPlayerResources);
        CardLevelingCost neededResources = totalLevelingCost.subtract(currentResources);
        StaminaCost staminaCost = neededResources.getStaminaCost(playerBounties);

        ResourceTrackingValues ascensionBoxes = currentPlayerResources.getResourceTrackingValues().stream()
                .filter(predicate -> predicate.getResourceType().equals("ascension crystal box"))
                .findFirst().orElse(new ResourceTrackingValues(null,"ascension crystal box", 0,0,0,0,0,currentPlayerResources));
        CardLevelingCost neededResourcesAfterBoxes = neededResources.useAscensionBoxes(ascensionBoxes, playerBounties);
        StaminaCost staminaCostAfterBoxes = neededResourcesAfterBoxes.getStaminaCost(playerBounties);

        model.addAttribute("cardLevelingCosts", cardLevelingCosts);
        model.addAttribute("totalLevelingCost", totalLevelingCost);
        model.addAttribute("neededResources", neededResources);
        model.addAttribute("staminaCost", staminaCost);
        model.addAttribute("neededResourcesAfterBoxes", neededResourcesAfterBoxes);
        model.addAttribute("staminaCostAfterBoxes", staminaCostAfterBoxes);
        return "/levelingPlanning/levelingPlanningView";
    }

    private List<CardLevelingCost> getCardLevelingCosts(LevelingPlanningForm levelingPlanningForm) {
        List<CardLevelingCost> levelingCosts = new ArrayList<>();
        List<LevelingPlanningSubForm> cardLevelingInfos = levelingPlanningForm.getCardInfo();
        for (int i = 0; i < levelingPlanningForm.getNumCards(); i++) {
            CardLevelingCost cardLevelingCost = new CardLevelingCost();
            LevelingPlanningSubForm cardLevelingInfo = cardLevelingInfos.get(i);
            if (cardLevelingInfo.getEndAscension() == null) { cardLevelingInfo.setEndAscension(true); }
            if (cardLevelingInfo.getStartAscension() == null) { cardLevelingInfo.setStartAscension(false); }
            cardLevelingCost.loadFromLevelingInformation(
                    levelingCostService.getLevelingCostFromStartLevelToEndGivenRarity(cardLevelingInfo.getStartLevel(), cardLevelingInfo.getEndLevel(), cardLevelingInfo.getRarityType()),
                    ascensionCostService.getAllAscensionCostsForGivenRarityStartAndEndLevel(cardLevelingInfo.getRarityType(), cardLevelingInfo.getStartLevel(),
                            cardLevelingInfo.getEndLevel(), cardLevelingInfo.getStartAscension(), cardLevelingInfo.getEndAscension()),
                    cardLevelingInfo.getStellacrum());
            levelingCosts.add(cardLevelingCost);
        }
        return levelingCosts;
    }

}
