package lads.lads_website.controller;

import lads.lads_website.domain.*;
import lads.lads_website.domain.projections.ResourceTypeOnly;
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

    private final int CRYSTAL_N_PER_GENERAL_BOX = 5;
    private final int CRYSTAL_R_PER_GENERAL_BOX = 2;
    private final int CRYSTAL_SR_PER_GENERAL_BOX = 1;
    private final int EXP_IN_WISH_BOTTLE_N = 10;
    private final int EXP_IN_WISH_BOTTLE_R = 50;
    private final int EXP_IN_WISH_BOTTLE_SR = 250;
    private final int EXP_IN_WISH_BOTTLE_SSR = 1000;

    private final AscensionCostService ascensionCostService;
    private final LevelingCostService levelingCostService;
    private final PlayerBountyService playerBountyService;
    private final PlayerService playerService;
    private final PlayerCardService playerCardService;
    private final ResourceTrackingService resourceTrackingService;
    private final ResourceTrackingValuesService resourceTrackingValuesService;

    @Autowired
    public LevelingPlanningController(AscensionCostService ascensionCostService, LevelingCostService levelingCostService, PlayerBountyService playerBountyService, PlayerService playerService, PlayerCardService playerCardService, ResourceTrackingService resourceTrackingService, ResourceTrackingValuesService resourceTrackingValuesService) {
        this.ascensionCostService = ascensionCostService;
        this.levelingCostService = levelingCostService;
        this.playerBountyService = playerBountyService;
        this.playerService = playerService;
        this.playerCardService = playerCardService;
        this.resourceTrackingService = resourceTrackingService;
        this.resourceTrackingValuesService = resourceTrackingValuesService;
    }

    @GetMapping("/levelingPlanning/startPrep")
    public String getLevelingPrepPage(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).get();
        List<PlayerCard> playerCards = playerCardService.getAllPlayerCardsForGivenPlayerId(player.getId());
        List<PlayerCardList> playerCardLists = new ArrayList<>();
        // Passing the full PlayerCard object violates the nesting depth requirements when used in Javascript, so need to only send necessary
        // information to the page.
        playerCards.forEach(pc -> playerCardLists.add(new PlayerCardList(pc.getId(), pc.getCard().getName())));

        model.addAttribute("playerCards", playerCardLists);
        model.addAttribute("rarities", new String[]{"3 Star", "4 Star", "5 Star"});
        model.addAttribute("stellacrums", new String[]{"Amber", "Emerald", "Ruby", "Violet", "Sapphire", "Pearl"});

        return "/levelingPlanning/levelingPrep";
    }

    @PostMapping("/levelingPlanning/levelingPlanning")
    public String getCardLevelingPage(LevelingPlanningForm levelingPlanningForm, Principal principal, Model model) {
        Player player = playerService.findByUsername(principal.getName()).get();
        boolean resourcesFound = true;

        // Get current player total EXP/Gold/Crystals
        Optional<ResourceTracking> recentResourcesOptional = resourceTrackingService.getMostRecentResourceTracking(player.getId());
        ResourceTracking recentResources;
        if(recentResourcesOptional.isEmpty()) {
            // Assume player has 0 of everything and let them know no resources were found attached to their account
            recentResources = new ResourceTracking(0L, 0, 0, 0, 0, 0, 0, 0,
                    null, player, new ArrayList<>());
            List<ResourceTypeOnly> allValues = resourceTrackingValuesService.getAllResourceTypes();
            for (ResourceTypeOnly rto : allValues) {
                ResourceTrackingValues rtv = new ResourceTrackingValues(0L, rto.getResourceType(), 0, 0, 0, 0, 0, recentResources);
                recentResources.getResourceTrackingValues().add(rtv);
            }
            resourcesFound = false;
        } else {
            // Player has resources
            recentResources = recentResourcesOptional.get();
        }

        // Each card should include: Total cost (exp/gold/crystals) to get card from start level -> end level
        List<LevelingPlanningSubForm> subForms = levelingPlanningForm.getCardInfo();
        List<TotalCardLevelingCost> cardLevelingCosts = new ArrayList<>();
        for (int i = 0; i < levelingPlanningForm.getNumCards(); i++) {
            LevelingPlanningSubForm subForm = subForms.get(i);
            if (subForm.getEndAscension() == null) { subForm.setEndAscension(true); }
            if (subForm.getStartAscension() == null) { subForm.setStartAscension(false); }

            // how much exp to go from start level -> end level?
            List<LevelingCost> levelingCosts = levelingCostService.getLevelingCostFromStartLevelToEndGivenRarity(
                    subForm.getStartLevel(), subForm.getEndLevel(), subForm.getRarityType());
            int expCost = 0;
            for (LevelingCost lc : levelingCosts) { expCost += lc.getExp(); }

            // how many ascensions between start level -> end level and what is the total cost?
            List<AscensionCost> ascensionCosts = ascensionCostService.getAllAscensionCostsForGivenRarityStartAndEndLevel(
                    subForm.getRarityType(), subForm.getStartLevel(), subForm.getEndLevel(), subForm.getStartAscension(), subForm.getEndAscension());

            int goldCost = 0;
            int nCost = 0;
            int rCost = 0;
            int srCost = 0;
            for (AscensionCost ascensionCost : ascensionCosts) {
                goldCost += ascensionCost.getGold();
                nCost += ascensionCost.getCrystalCostN();
                rCost += ascensionCost.getCrystalCostR();
                srCost += ascensionCost.getCrystalCostSr();
            }

            cardLevelingCosts.add(new TotalCardLevelingCost(expCost, goldCost, subForm.getStellacrum(), nCost, rCost, srCost));
        }

        // Get cumulative cost for all selected cards
        int totalExpCost = 0;
        int totalGoldCost = 0;
        Map<String, Map<String, Integer>> crystalCosts = new HashMap<>();
        for (TotalCardLevelingCost cardCosts : cardLevelingCosts) {
            String stellacrum = cardCosts.getStellacrum();
            totalExpCost += cardCosts.getExpCost();
            totalGoldCost += cardCosts.getGoldCost();

            Map<String, Integer> specificCrystalCosts;
            if (!crystalCosts.containsKey(stellacrum)) {
                specificCrystalCosts = new HashMap<>();
                specificCrystalCosts.put("N", cardCosts.getnCost());
                specificCrystalCosts.put("R", cardCosts.getrCost());
                specificCrystalCosts.put("SR", cardCosts.getSrCost());
            } else {
                specificCrystalCosts = crystalCosts.get(stellacrum);
                specificCrystalCosts.put("N", specificCrystalCosts.get("N") + cardCosts.getnCost());
                specificCrystalCosts.put("R", specificCrystalCosts.get("R") + cardCosts.getrCost());
                specificCrystalCosts.put("SR", specificCrystalCosts.get("SR") + cardCosts.getSrCost());
            }
            crystalCosts.put(stellacrum, specificCrystalCosts);
        }

        // Get Current PLayer Resources
        List<ResourceTrackingValues> playerResources = recentResources.getResourceTrackingValues();
        int currentExp = 0;
        int currentGold = recentResources.getGold();
        Map<String, Map<String, Integer>> currentCrystals = new HashMap<>();
        Map<String, Integer> ascensionBoxes = new HashMap<>();
        for (ResourceTrackingValues resourceTrackingValues : playerResources) {
            switch (resourceTrackingValues.getResourceType()) {
                case "wish bottle" ->
                        currentExp = resourceTrackingValues.getN() * EXP_IN_WISH_BOTTLE_N + resourceTrackingValues.getR() * EXP_IN_WISH_BOTTLE_R
                                + resourceTrackingValues.getSr() * EXP_IN_WISH_BOTTLE_SR + resourceTrackingValues.getSsr() * EXP_IN_WISH_BOTTLE_SSR;
                case "violet crystal" -> {
                    if (crystalCosts.containsKey("Violet")) {
                        currentCrystals.put("Violet", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "amber crystal" -> {
                    if (crystalCosts.containsKey("Amber")) {
                        currentCrystals.put("Amber", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "pearl crystal" -> {
                    if (crystalCosts.containsKey("Pearl")) {
                        currentCrystals.put("Pearl", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "sapphire crystal" -> {
                    if (crystalCosts.containsKey("Sapphire")) {
                        currentCrystals.put("Sapphire", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "ruby crystal" -> {
                    if (crystalCosts.containsKey("Ruby")) {
                        currentCrystals.put("Ruby", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "emerald crystal" -> {
                    if (crystalCosts.containsKey("Emerald")) {
                        currentCrystals.put("Emerald", prepareCrystalMap(resourceTrackingValues));
                    }
                }
                case "ascension crystal box" -> {
                    ascensionBoxes.put("N", resourceTrackingValues.getN());
                    ascensionBoxes.put("R", resourceTrackingValues.getR());
                    ascensionBoxes.put("SR", resourceTrackingValues.getSr());
                    ascensionBoxes.put("General", resourceTrackingValues.getGeneral());
                }
            }
        }

        int neededExp = Math.max(totalExpCost - currentExp, 0);
        int neededGold = Math.max(totalGoldCost - currentGold, 0);
        Map<String, Map<String, Integer>> neededCrystals = compareCrystalMaps(crystalCosts, currentCrystals);

        List<BountyReward> bountyRewards = playerBountyService.getAllBountyRewardsForPlayer(player.getId());
        Map<String, Integer> crystalStamCost = new HashMap<>();
        Map<String, BountyReward> crystalBounties = new HashMap<>();
        bountyRewards.forEach(br -> {
            String bountyType = br.getBountyNameType();
            switch (bountyType) {
                case "Pumpkin Magus", "Lemonette", "Snoozer" : crystalBounties.put(bountyType, br);
            }
        });

        getStaminaCosts(crystalStamCost, bountyRewards, neededGold, neededExp, neededCrystals);

        Map<String, Integer> nCrystals = new HashMap<>();
        Map<String, Integer> rCrystals = new HashMap<>();
        Map<String, Integer> srCrystals = new HashMap<>();
        Map<String, Map<String, Integer>> neededCrystalsAfterAscensionBoxes = new HashMap<>();
        // Create an exact copy of the existing neededCrystals map to modify using the player's ascension boxes
        for (String key : neededCrystals.keySet()) {
            Map<String, Integer> newVals = new HashMap<>();
            for (String key2 : neededCrystals.get(key).keySet()) {
                newVals.put(key2, neededCrystals.get(key).get(key2));
            }
            neededCrystalsAfterAscensionBoxes.put(key, newVals);
        }

        for (Map.Entry<String, Map<String, Integer>> entry : neededCrystals.entrySet()) {
            nCrystals.put(entry.getKey(), entry.getValue().get("N"));
            rCrystals.put(entry.getKey(), entry.getValue().get("R"));
            srCrystals.put(entry.getKey(), entry.getValue().get("SR"));
        }
        useBoxesOnCrystals(nCrystals, ascensionBoxes.get("N"), crystalBounties, "N");
        useBoxesOnCrystals(rCrystals, ascensionBoxes.get("R"), crystalBounties, "R");
        useBoxesOnCrystals(srCrystals, ascensionBoxes.get("SR"), crystalBounties, "SR");
        for (String key : neededCrystalsAfterAscensionBoxes.keySet()) {
            neededCrystalsAfterAscensionBoxes.get(key).put("N", nCrystals.get(key));
            neededCrystalsAfterAscensionBoxes.get(key).put("R", rCrystals.get(key));
            neededCrystalsAfterAscensionBoxes.get(key).put("SR", srCrystals.get(key));
        }

        useGeneralBoxes(neededCrystalsAfterAscensionBoxes, ascensionBoxes.get("General"), crystalBounties);

        Map<String, Integer> crystalStamCostAfterBoxes = new HashMap<>();
        getStaminaCosts(crystalStamCostAfterBoxes, bountyRewards, neededGold, neededExp, neededCrystalsAfterAscensionBoxes);

        // Card leveling cost (individual & total cost)
        model.addAttribute("cardLevelingCosts", cardLevelingCosts);
        model.addAttribute("totalExpCost", totalExpCost);
        model.addAttribute("totalGoldCost", totalGoldCost);
        model.addAttribute("totalCrystalCosts", crystalCosts);
        // Current player resources & if player has any to begin with
        model.addAttribute("resourcesFound", resourcesFound);
        model.addAttribute("currentExp", currentExp);
        model.addAttribute("currentGold", currentGold);
        model.addAttribute("currentCrystals", currentCrystals);
        model.addAttribute("ascensionBoxes", ascensionBoxes);
        // Needed materials
        model.addAttribute("neededExp", neededExp);
        model.addAttribute("neededGold", neededGold);
        model.addAttribute("neededCrystals", neededCrystals);
        model.addAttribute("neededCrystalsAfterBoxes", neededCrystalsAfterAscensionBoxes);
        // Stam Cost
        model.addAttribute("expStamCost", crystalStamCost.get("Exp Stamina Cost"));
        model.addAttribute("goldStamCost", crystalStamCost.get("Gold Stamina Cost"));
        model.addAttribute("crystalStamCosts", crystalStamCost);
        model.addAttribute("totalStamCost", crystalStamCost.get("Total Stamina Cost"));
        // Stam Cost After Ascension Boxes
        model.addAttribute("crystalStamCostAfterBoxes", crystalStamCostAfterBoxes);
        model.addAttribute("totalStamCostAfterBoxes", crystalStamCostAfterBoxes.get("Total Stamina Cost"));

        return "/levelingPlanning/levelingPlanningView";
    }

    private void getStaminaCosts(Map<String, Integer> crystalStamCost, List<BountyReward> bountyRewards, int neededGold, int neededExp, Map<String, Map<String, Integer>> neededCrystals) {
        int totalStaminaCost = 0;
        for (BountyReward bountyReward : bountyRewards) {
            String bountyType = bountyReward.getBountyNameType();
            switch(bountyType) {
                case "Mr. Beanie" -> {
                    crystalStamCost.put("Gold",(neededGold / bountyReward.getN()) * bountyReward.getStaminaCost());
                    totalStaminaCost += crystalStamCost.get("Gold");
                }
                case "Heartbreaker" -> {
                    int expPerBounty = bountyReward.getR() == null && bountyReward.getSr() == null ? bountyReward.getN() * EXP_IN_WISH_BOTTLE_N : bountyReward.getSr() == null ?
                            bountyReward.getN() * EXP_IN_WISH_BOTTLE_N + bountyReward.getR() * bountyReward.getR() * EXP_IN_WISH_BOTTLE_R :
                            bountyReward.getN() * EXP_IN_WISH_BOTTLE_N + bountyReward.getR() * bountyReward.getR() * EXP_IN_WISH_BOTTLE_R + bountyReward.getSr() * EXP_IN_WISH_BOTTLE_SR;
                    crystalStamCost.put("Exp",(neededExp / expPerBounty) * bountyReward.getStaminaCost());
                    totalStaminaCost += crystalStamCost.get("Exp");
                }
                case "Pumpkin Magus" -> {
                    if (neededCrystals.containsKey("Ruby")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Ruby");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                    if (neededCrystals.containsKey("Sapphire")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Sapphire");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                }
                case "Lemonette" -> {
                    if (neededCrystals.containsKey("Emerald")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Emerald");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                    if (neededCrystals.containsKey("Amber")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Amber");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                }
                case "Snoozer" -> {
                    if (neededCrystals.containsKey("Pearl")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Pearl");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                    if (neededCrystals.containsKey("Violet")) {
                        Map<String, Integer> crystalNeeded = neededCrystals.get("Violet");
                        addToCrystalStaminaCost(crystalStamCost, crystalNeeded, bountyReward, bountyType);
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : crystalStamCost.entrySet()) {
            if (!entry.getKey().equals("Exp") && !entry.getKey().equals("Gold")) { totalStaminaCost += entry.getValue(); }
        }
        crystalStamCost.put("Total", totalStaminaCost);
    }

    private void addToCrystalStaminaCost(Map<String, Integer> crystalStamCost, Map<String, Integer> crystalNeeded, BountyReward bountyReward,
                                                         String bountyType) {
        int nStam = (crystalNeeded.get("N") / bountyReward.getN()) * bountyReward.getStaminaCost();
        int rStam = (crystalNeeded.get("R") / bountyReward.getR()) * bountyReward.getStaminaCost();
        int srStam = (crystalNeeded.get("SR") / bountyReward.getSr()) * bountyReward.getStaminaCost();
        int maxStam = Math.max(nStam, Math.max(rStam, srStam));
        if (crystalStamCost.containsKey(bountyType)) {
            crystalStamCost.compute(bountyType, (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
        } else {
            crystalStamCost.put(bountyType, maxStam);
        }
    }

    private void useGeneralBoxes(Map<String, Map<String, Integer>> crystals, int numBoxes, Map<String, BountyReward> crystalBounties) {
        while (numBoxes > 0) {
            // find highest stam cost across all needed crystals
            int highestStamCost = 0;
            int secondHighestStamCost = 0;
            // keys will be something like: Sapphire_N, Pearl_SR, etc.
            Map<String, Integer> highestCostCrystals = new HashMap<>();
            for (Map.Entry<String, Map<String, Integer>> entry : crystals.entrySet()) {
                String stellaColor = entry.getKey();
                BountyReward crystalBounty = getCrystalBountyByStellacrumColor(crystalBounties, stellaColor);
                for (Map.Entry<String, Integer> crystalVals : entry.getValue().entrySet()) {
                    String crystalType = crystalVals.getKey();
                    String fullKey = stellaColor + "_" + crystalType;

                    int stamCost = getStamCost(crystalBounty, crystalVals, crystalType);

                    if (highestCostCrystals.isEmpty()) {
                        highestStamCost = stamCost;
                        highestCostCrystals.put(fullKey, crystalVals.getValue());
                    } else if (highestStamCost == -1 || stamCost == -1) {
                        if (highestStamCost == -1 && stamCost == -1) {
                            highestCostCrystals.put(fullKey, crystalVals.getValue());
                        } else if (stamCost == -1) {
                            highestStamCost = stamCost;
                            highestCostCrystals.clear();
                            highestCostCrystals.put(fullKey, crystalVals.getValue());
                        }
                    }
                    else if (highestStamCost < stamCost) {
                        secondHighestStamCost = highestStamCost;
                        highestStamCost = stamCost;
                        highestCostCrystals.clear();
                        highestCostCrystals.put(fullKey, crystalVals.getValue());
                    } else if (highestStamCost == stamCost) {
                        highestCostCrystals.put(fullKey, crystalVals.getValue());
                    } else if (stamCost > secondHighestStamCost) {
                        secondHighestStamCost = stamCost;
                    }
                }
            }

            // compare it to the second highest stam cost
            int stamCostDiff = highestStamCost - secondHighestStamCost;
            if (stamCostDiff == 0) { break; }

            int totalBoxesNeeded = 0;
            int numOfNandR = 0;
            int numOfSr = 0;
            Map<String, Integer> boxesNeededPerCrystal = new HashMap<>();
            for (Map.Entry<String, Integer> entry : highestCostCrystals.entrySet()) {
                String[] stellaColorAndCrystalType = entry.getKey().split("_");
                String stellacrumColor = stellaColorAndCrystalType[0];
                String crystalType = stellaColorAndCrystalType[1];
                BountyReward crystalBounty = getCrystalBountyByStellacrumColor(crystalBounties, stellacrumColor);
                switch (crystalType) {
                    case "N" -> {
                        int boxesNeeded = crystalBounty.getN() == null ? crystals.get(stellacrumColor).get(crystalType) / CRYSTAL_N_PER_GENERAL_BOX : ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getN()) / CRYSTAL_N_PER_GENERAL_BOX;
                        totalBoxesNeeded += boxesNeeded;
                        boxesNeededPerCrystal.put(entry.getKey(), boxesNeeded);
                        numOfNandR++;
                    }
                    case "R" -> {
                        int boxesNeeded = crystalBounty.getR() == null ? crystals.get(stellacrumColor).get(crystalType) / CRYSTAL_R_PER_GENERAL_BOX : ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getR()) / CRYSTAL_R_PER_GENERAL_BOX;
                        totalBoxesNeeded += boxesNeeded;
                        boxesNeededPerCrystal.put(entry.getKey(), boxesNeeded);
                        numOfNandR++;
                    }
                    case "SR" -> {
                        int boxesNeeded = crystalBounty.getSr() == null ? crystals.get(stellacrumColor).get(crystalType) / CRYSTAL_SR_PER_GENERAL_BOX : ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getSr()) / CRYSTAL_SR_PER_GENERAL_BOX;
                        totalBoxesNeeded += boxesNeeded;
                        boxesNeededPerCrystal.put(entry.getKey(), boxesNeeded);
                        numOfSr++;
                    }
                }
            }

            if (totalBoxesNeeded > numBoxes) {
                int boxPerSr = 0;
                int boxPerNAndR = 0;
                if (numOfSr == 0 || (numOfNandR == 0)) {
                    // if there are no sr boxes, or only sr boxes, we can divide evenly
                    boxPerSr = numBoxes / highestCostCrystals.size();
                    boxPerNAndR = numBoxes / highestCostCrystals.size();
                } else {
                    boxPerNAndR = (numBoxes * 2) / (highestCostCrystals.size() + numOfNandR);
                    boxPerSr = (numBoxes) / (highestCostCrystals.size() + numOfNandR);
                }

                for (Map.Entry<String, Integer> crystal : highestCostCrystals.entrySet()) {
                    String[] keys = crystal.getKey().split("_");
                    if (keys[1].equals("N")) {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (CRYSTAL_N_PER_GENERAL_BOX*boxPerNAndR),0));
                    } else if (keys[1].equals("R")) {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (CRYSTAL_R_PER_GENERAL_BOX*boxPerNAndR),0));
                    } else {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (CRYSTAL_SR_PER_GENERAL_BOX*boxPerSr),0));
                    }
                }

                numBoxes = 0;
            } else {
                for (Map.Entry<String, Integer> crystal : highestCostCrystals.entrySet()) {
                    String[] keys = crystal.getKey().split("_");
                    if(keys[1].equals("N")) {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesNeededPerCrystal.get(crystal.getKey()) * CRYSTAL_N_PER_GENERAL_BOX), 0));
                    } else if (keys[1].equals("R")) {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesNeededPerCrystal.get(crystal.getKey()) * CRYSTAL_R_PER_GENERAL_BOX), 0));
                    } else {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesNeededPerCrystal.get(crystal.getKey()) * CRYSTAL_SR_PER_GENERAL_BOX), 0));
                    }
                }
                numBoxes = numBoxes - totalBoxesNeeded;
            }
        }
    }

    private int getStamCost(BountyReward crystalBounty, Map.Entry<String, Integer> crystalVals, String crystalType) {
        int stamCost;
        if (crystalType.equals("N")) {
            stamCost = (crystalVals.getValue()/ crystalBounty.getN()) * crystalBounty.getStaminaCost();
        } else if (crystalType.equals("R")) {
            if (crystalBounty.getR() == null) { return -1; }
            stamCost = (crystalVals.getValue()/ crystalBounty.getR()) * crystalBounty.getStaminaCost();
        } else {
            if (crystalBounty.getSr() == null) { return -1; }
            stamCost = (crystalVals.getValue()/ crystalBounty.getSr()) * crystalBounty.getStaminaCost();
        }
        return stamCost;
    }

    private void useBoxesOnCrystals(Map<String, Integer> crystals, int numBoxes, Map<String, BountyReward> crystalBounties, String crystalType) {
        while (numBoxes > 0) {
            List<String> highestCostCrystals = new ArrayList<>();
            int highestStaminaCost = 0;
            int secondHighestStaminaCost = 0;

            // Get a list of the highest stamina cost crystals, and the second highest stamina cost value.
            for (Map.Entry<String, Integer> crystalCost : crystals.entrySet()) {
                BountyReward bountyReward = getCrystalBountyByStellacrumColor(crystalBounties, crystalCost.getKey());
                int currentStaminaCost = getStamCost(bountyReward, crystalCost, crystalType);
                if (highestCostCrystals.isEmpty()) {
                    highestStaminaCost = currentStaminaCost;
                    highestCostCrystals.add(crystalCost.getKey());
                } else if (highestStaminaCost == -1 || currentStaminaCost == -1) {
                    // This occurs if it is impossible for the player to get the current resource by farming bounties
                    // (Ie. they can't clear a high enough bounty to get a higher level material)
                    if (highestStaminaCost == -1 && currentStaminaCost == -1) {
                        highestCostCrystals.add(crystalCost.getKey());
                    } else if (currentStaminaCost == -1) {
                        highestCostCrystals.clear();
                        highestStaminaCost = currentStaminaCost;
                        highestCostCrystals.add(crystalCost.getKey());
                    }
                } else if (highestStaminaCost < currentStaminaCost) {
                    secondHighestStaminaCost = highestStaminaCost;
                    highestStaminaCost = currentStaminaCost;
                    highestCostCrystals.clear();
                    highestCostCrystals.add(crystalCost.getKey());
                } else if (highestStaminaCost == currentStaminaCost) {
                    highestCostCrystals.add(crystalCost.getKey());
                } else if (currentStaminaCost > secondHighestStaminaCost) {
                    secondHighestStaminaCost = currentStaminaCost;
                }
            }

            int staminaCostDifference = highestStaminaCost - secondHighestStaminaCost;
            if (staminaCostDifference == 0) { break; }

            Map<String, Integer> boxCostForEachCrystal = new HashMap<>();
            int totalBoxesNeeded = 0;
            for (String stellacrumColor : highestCostCrystals) {
                BountyReward crystalBounty = getCrystalBountyByStellacrumColor(crystalBounties, stellacrumColor);
                Integer crystalsPerRun = crystalType.equals("N") ? crystalBounty.getN() : crystalType.equals("R") ? crystalBounty.getR() : crystalBounty.getSr();
                // If we cannot farm this crystal, the boxes needed = total number of crystals
                int boxesNeeded = crystalsPerRun == null ? crystals.get(stellacrumColor) : (staminaCostDifference / crystalBounty.getStaminaCost()) * crystalsPerRun;
                boxCostForEachCrystal.put(stellacrumColor, boxesNeeded);
                totalBoxesNeeded += boxesNeeded;
            }

            if (totalBoxesNeeded == 0) { break; }

            if (totalBoxesNeeded > numBoxes) {
                int boxesPerCrystal = numBoxes / highestCostCrystals.size();
                for (String stellacrumColor : highestCostCrystals) {
                    crystals.put(stellacrumColor, Math.max(crystals.get(stellacrumColor) - boxesPerCrystal, 0));
                }
                numBoxes = 0;
            }
            else {
                for (String stellacrumColor : highestCostCrystals) {
                    crystals.put(stellacrumColor, Math.max(crystals.get(stellacrumColor) - boxCostForEachCrystal.get(stellacrumColor),0));
                }
                numBoxes -= totalBoxesNeeded;
            }
        }
    }

    private BountyReward getCrystalBountyByStellacrumColor(Map<String, BountyReward> crystalBounties, String stellacrumColor) {
        return switch (stellacrumColor) {
            case "Amber", "Emerald" -> crystalBounties.get("Lemonette");
            case "Violet", "Pearl" -> crystalBounties.get("Snoozer");
            default -> crystalBounties.get("Pumpkin Magus");
        };
    }

    private Map<String, Map<String, Integer>> compareCrystalMaps(Map<String, Map<String, Integer>> crystalCost, Map<String, Map<String, Integer>> currentCrystals) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (String stellacrum : crystalCost.keySet()) {
            Map<String, Integer> needed = new HashMap<>();
            Map<String, Integer> costs = crystalCost.get(stellacrum);
            Map<String, Integer> current = currentCrystals.get(stellacrum);
            needed.put("N", Math.max(costs.get("N") - current.get("N"), 0));
            needed.put("R", Math.max(costs.get("R") - current.get("R"), 0));
            needed.put("SR", Math.max(costs.get("SR") - current.get("SR"), 0));
            result.put(stellacrum, needed);
        }
        return result;
    }

    private Map<String, Integer> prepareCrystalMap(ResourceTrackingValues resourceTrackingValues) {
        Map<String, Integer> crystals = new HashMap<>();
        crystals.put("N", resourceTrackingValues.getN());
        crystals.put("R", resourceTrackingValues.getR());
        crystals.put("SR", resourceTrackingValues.getSr());
        return crystals;
    }

    // Custom class to pass info back to the html view
    public class TotalCardLevelingCost {
        private int expCost;
        private int goldCost;
        private String stellacrum;
        private int nCost;
        private int rCost;
        private int srCost;

        public TotalCardLevelingCost() {
        }

        public TotalCardLevelingCost(int expCost, int goldCost, String stellacrum, int nCost, int rCost, int srCost) {
            this.expCost = expCost;
            this.goldCost = goldCost;
            this.stellacrum = stellacrum;
            this.nCost = nCost;
            this.rCost = rCost;
            this.srCost = srCost;
        }

        public String getStellacrum() {
            return stellacrum;
        }

        public void setStellacrum(String stellacrum) {
            this.stellacrum = stellacrum;
        }

        public int getExpCost() {
            return expCost;
        }

        public void setExpCost(int expCost) {
            this.expCost = expCost;
        }

        public int getGoldCost() {
            return goldCost;
        }

        public void setGoldCost(int goldCost) {
            this.goldCost = goldCost;
        }

        public int getnCost() {
            return nCost;
        }

        public void setnCost(int nCost) {
            this.nCost = nCost;
        }

        public int getrCost() {
            return rCost;
        }

        public void setrCost(int rCost) {
            this.rCost = rCost;
        }

        public int getSrCost() {
            return srCost;
        }

        public void setSrCost(int srCost) {
            this.srCost = srCost;
        }
    }
}
