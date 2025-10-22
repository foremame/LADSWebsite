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

    private final AscensionCostService ascensionCostService;
    private final LevelingCostService levelingCostService;
    private final BountyRewardService bountyRewardService;
    private final PlayerService playerService;
    private final PlayerCardService playerCardService;
    private final ResourceTrackingService resourceTrackingService;
    private final ResourceTrackingValuesService resourceTrackingValuesService;

    @Autowired
    public LevelingPlanningController(AscensionCostService ascensionCostService, LevelingCostService levelingCostService, BountyRewardService bountyRewardService, PlayerService playerService, PlayerCardService playerCardService, ResourceTrackingService resourceTrackingService, ResourceTrackingValuesService resourceTrackingValuesService) {
        this.ascensionCostService = ascensionCostService;
        this.levelingCostService = levelingCostService;
        this.bountyRewardService = bountyRewardService;
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

    // todo have bounty hunt level come from the player instead of defaulting to 9
    @PostMapping("/levelingPlanning/levelingPlanning")
    public String getCardLevelingPage(LevelingPlanningForm levelingPlanningForm, Principal principal, Model model) {
        Player player = playerService.findByUsername(principal.getName()).get();
        int bountyHuntLevel = 9;
        boolean resourcesFound = true;
        final int EXP_IN_WISH_BOTTLE_N = 10;
        final int EXP_IN_WISH_BOTTLE_R = 50;
        final int EXP_IN_WISH_BOTTLE_SR = 250;
        final int EXP_IN_WISH_BOTTLE_SSR = 1000;

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

        // This takes the current bounty level the player can clear as a parameter. Don't have a way to get this
        // information currently so will default to 9 (highest level) for now.
        List<BountyReward> bountyRewards = bountyRewardService.getAllBountyRewardsByLevel(bountyHuntLevel);
        int totalStamCost = 0;
        int expStamCost = 0;
        int goldStamCost = 0;
        Map<String, Integer> crystalStamCost = new HashMap<>();
        BountyReward crystalBounty = null;
        for (BountyReward bountyReward : bountyRewards) {
            if (bountyReward.getResourceType().equals("gold")) {
                goldStamCost = (neededGold / bountyReward.getN()) * bountyReward.getStaminaCost();
                totalStamCost += goldStamCost;
            } else if (bountyReward.getResourceType().equals("wish bottle")) {
                int expPerBounty = bountyReward.getN() * EXP_IN_WISH_BOTTLE_N + bountyReward.getR() * EXP_IN_WISH_BOTTLE_R + bountyReward.getSr() * EXP_IN_WISH_BOTTLE_SR;
                expStamCost = (neededExp / expPerBounty) * bountyReward.getStaminaCost();
                totalStamCost += expStamCost;
            } else if (bountyReward.getResourceType().equals("crystal") && crystalStamCost.isEmpty()) {
                crystalBounty = bountyReward;
                for (String stellacrum : neededCrystals.keySet()) {
                    Map<String, Integer> crystalNeeded = neededCrystals.get(stellacrum);
                    int nStam = (crystalNeeded.get("N") / bountyReward.getN()) * bountyReward.getStaminaCost();
                    int rStam = (crystalNeeded.get("R") / bountyReward.getR()) * bountyReward.getStaminaCost();
                    int srStam = (crystalNeeded.get("SR") / bountyReward.getSr()) * bountyReward.getStaminaCost();
                    int maxStam = Math.max(nStam, Math.max(rStam, srStam));
                    switch (stellacrum) {
                        case "Emerald", "Amber" -> {
                            if (crystalStamCost.containsKey("EmeraldAmber")) {
                                crystalStamCost.compute("EmeraldAmber", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCost.put("EmeraldAmber", maxStam);
                            }
                        }
                        case "Ruby", "Sapphire" -> {
                            if (crystalStamCost.containsKey("RubySapphire")) {
                                crystalStamCost.compute("RubySapphire", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCost.put("RubySapphire", maxStam);
                            }
                        }
                        case "Violet", "Pearl" -> {
                            if (crystalStamCost.containsKey("VioletPearl")) {
                                crystalStamCost.compute("VioletPearl", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCost.put("VioletPearl", maxStam);
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : crystalStamCost.entrySet()) {
            totalStamCost += entry.getValue();
        }

        Map<String, Integer> nCrystals = new HashMap<>();
        Map<String, Integer> rCrystals = new HashMap<>();
        Map<String, Integer> srCrystals = new HashMap<>();
        Map<String, Map<String, Integer>> neededCrystalsAfterAscensionBoxes = new HashMap<>();
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
        nCrystals = useBoxesOnCrystals(nCrystals, ascensionBoxes.get("N"));
        rCrystals = useBoxesOnCrystals(rCrystals, ascensionBoxes.get("R"));
        srCrystals = useBoxesOnCrystals(srCrystals, ascensionBoxes.get("SR"));
        for (String key : neededCrystalsAfterAscensionBoxes.keySet()) {
            neededCrystalsAfterAscensionBoxes.get(key).put("N", nCrystals.get(key));
            neededCrystalsAfterAscensionBoxes.get(key).put("R", rCrystals.get(key));
            neededCrystalsAfterAscensionBoxes.get(key).put("SR", srCrystals.get(key));
        }

        neededCrystalsAfterAscensionBoxes = useGeneralBoxes(neededCrystalsAfterAscensionBoxes, ascensionBoxes.get("General"), crystalBounty);

        // Should probably move this to a method but I'm going to be lazy for now
        int totalStamCostAfterBoxes = expStamCost + goldStamCost;
        Map<String, Integer> crystalStamCostAfterBoxes = new HashMap<>();
        for (BountyReward bountyReward : bountyRewards) {
            if (bountyReward.getResourceType().equals("crystal") && crystalStamCostAfterBoxes.isEmpty()) {
                for (String stellacrum : neededCrystalsAfterAscensionBoxes.keySet()) {
                    Map<String, Integer> crystalNeeded = neededCrystalsAfterAscensionBoxes.get(stellacrum);
                    int nStam = (crystalNeeded.get("N") / bountyReward.getN()) * bountyReward.getStaminaCost();
                    int rStam = (crystalNeeded.get("R") / bountyReward.getR()) * bountyReward.getStaminaCost();
                    int srStam = (crystalNeeded.get("SR") / bountyReward.getSr()) * bountyReward.getStaminaCost();
                    int maxStam = Math.max(nStam, Math.max(rStam, srStam));
                    switch (stellacrum) {
                        case "Emerald", "Amber" -> {
                            if (crystalStamCostAfterBoxes.containsKey("EmeraldAmber")) {
                                crystalStamCostAfterBoxes.compute("EmeraldAmber", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCostAfterBoxes.put("EmeraldAmber", maxStam);
                            }
                        }
                        case "Ruby", "Sapphire" -> {
                            if (crystalStamCostAfterBoxes.containsKey("RubySapphire")) {
                                crystalStamCostAfterBoxes.compute("RubySapphire", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCostAfterBoxes.put("RubySapphire", maxStam);
                            }
                        }
                        case "Violet", "Pearl" -> {
                            if (crystalStamCostAfterBoxes.containsKey("VioletPearl")) {
                                crystalStamCostAfterBoxes.compute("VioletPearl", (k, currMaxStamCost) -> (currMaxStamCost == null) ? maxStam : Math.max(currMaxStamCost, maxStam));
                            } else {
                                crystalStamCostAfterBoxes.put("VioletPearl", maxStam);
                            }
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : crystalStamCostAfterBoxes.entrySet()) {
            totalStamCostAfterBoxes += entry.getValue();
        }

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
        model.addAttribute("expStamCost", expStamCost);
        model.addAttribute("goldStamCost", goldStamCost);
        model.addAttribute("crystalStamCosts", crystalStamCost);
        model.addAttribute("totalStamCost", totalStamCost);
        // Stam Cost After Ascension Boxes
        model.addAttribute("crystalStamCostAfterBoxes", crystalStamCostAfterBoxes);
        model.addAttribute("totalStamCostAfterBoxes", totalStamCostAfterBoxes);

        return "/levelingPlanning/levelingPlanningView";
    }

    public Map<String, Map<String, Integer>> useGeneralBoxes(Map<String, Map<String, Integer>> crystals, int numBoxes, BountyReward crystalBounty) {
        while (numBoxes > 0) {
            // find highest stam cost across all needed crystals
            int highestStamCost = 0;
            int secondHighestStamCost = 0;
            // keys will be something like: SapphireN, PearlSR, etc.
            Map<String, Integer> highestCostCrystals = new HashMap<>();
            for (Map.Entry<String, Map<String, Integer>> entry : crystals.entrySet()) {
                String stellaColor = entry.getKey();
                for (Map.Entry<String, Integer> crystalVals : entry.getValue().entrySet()) {
                    String crystalType = crystalVals.getKey();
                    String fullKey = stellaColor + "_" + crystalType;

                    int stamCost = getStamCost(crystalBounty, crystalVals, crystalType);

                    if (highestCostCrystals.isEmpty()) {
                        highestStamCost = stamCost;
                        highestCostCrystals.put(fullKey, crystalVals.getValue());
                    } else if (highestStamCost < stamCost) {
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

            // bring the value of crystals down to the equivalent stam value of the second highest stam cost using available boxes

            int boxesN = ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getN()) / CRYSTAL_N_PER_GENERAL_BOX;
            int boxesR = ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getR()) / CRYSTAL_R_PER_GENERAL_BOX;
            int boxesSr = ((stamCostDiff / crystalBounty.getStaminaCost()) * crystalBounty.getSr()) / CRYSTAL_SR_PER_GENERAL_BOX;

            int totalBoxesNeeded = 0;
            int numOfNandR = 0;
            int numOfSr = 0;
            for (Map.Entry<String, Integer> entry : highestCostCrystals.entrySet()) {
                if (entry.getKey().contains("_N")) {
                    totalBoxesNeeded += boxesN;
                    numOfNandR++;
                } else if (entry.getKey().contains("_R")) {
                    totalBoxesNeeded += boxesR;
                    numOfNandR++;
                } else if (entry.getKey().contains("_SR")) {
                    totalBoxesNeeded += boxesSr;
                    numOfSr++;
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
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesN * CRYSTAL_N_PER_GENERAL_BOX), 0));
                    } else if (keys[1].equals("R")) {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesR * CRYSTAL_R_PER_GENERAL_BOX), 0));
                    } else {
                        crystals.get(keys[0]).put(keys[1], Math.max(crystal.getValue() - (boxesSr * CRYSTAL_SR_PER_GENERAL_BOX), 0));
                    }
                }
                numBoxes = numBoxes - totalBoxesNeeded;
            }
        }

        return crystals;
    }

    private int getStamCost(BountyReward crystalBounty, Map.Entry<String, Integer> crystalVals, String crystalType) {
        int stamCost;
        if (crystalType.equals("N")) {
            stamCost = (crystalVals.getValue()/ crystalBounty.getN()) * crystalBounty.getStaminaCost();
        } else if (crystalType.equals("R")) {
            stamCost = (crystalVals.getValue()/ crystalBounty.getR()) * crystalBounty.getStaminaCost();
        } else {
            stamCost = (crystalVals.getValue()/ crystalBounty.getSr()) * crystalBounty.getStaminaCost();
        }
        return stamCost;
    }

    public Map<String, Integer> useBoxesOnCrystals(Map<String, Integer> crystals, int numBoxes) {
        Map<String, Integer> results = new HashMap<>(crystals);
        while (numBoxes > 0) {
            List<String> highestVals = new ArrayList<>();
            int secondHighestVal = 0;
            // Find all the highest values, and what the value of the second highest crystal val is
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                if (highestVals.isEmpty()) {
                    highestVals.add(entry.getKey());
                } else if (results.get(highestVals.getFirst()) < entry.getValue()) {
                    secondHighestVal = results.get(highestVals.getFirst());
                    highestVals.clear();
                    highestVals.add(entry.getKey());
                } else if (results.get(highestVals.getFirst()).equals(entry.getValue())) {
                    highestVals.add(entry.getKey());
                } else if (entry.getValue() > secondHighestVal) {
                    secondHighestVal = entry.getValue();
                }
            }

            // Subtract the number of boxes needed from each highest value to bring them down to the value of
            // the second highest
            int boxesNeededPerHighest = results.get(highestVals.getFirst()) - secondHighestVal;
            // If all crystal vals are 0, no more needs to be done
            if (boxesNeededPerHighest == 0) { break; }

            if (boxesNeededPerHighest * highestVals.size() > numBoxes) {
                int boxUsedPerHighest = numBoxes / highestVals.size();
                for (String s : highestVals) {
                    results.put(s, results.get(s) - boxUsedPerHighest);
                }
                numBoxes = 0;
            } else {
                for (String s : highestVals) {
                    results.put(s, results.get(s) - boxesNeededPerHighest);
                }
                numBoxes = numBoxes - (boxesNeededPerHighest * highestVals.size());
            }
        }
        return results;
    }

    public Map<String, Map<String, Integer>> compareCrystalMaps(Map<String, Map<String, Integer>> crystalCost, Map<String, Map<String, Integer>> currentCrystals) {
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

    public Map<String, Integer> prepareCrystalMap(ResourceTrackingValues resourceTrackingValues) {
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
