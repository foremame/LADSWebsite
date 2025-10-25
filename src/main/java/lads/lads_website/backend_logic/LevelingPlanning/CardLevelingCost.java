package lads.lads_website.backend_logic.LevelingPlanning;

import lads.lads_website.domain.*;

import java.util.*;

public class CardLevelingCost {
    private final int EXP_IN_WISH_BOTTLE_N = 10;
    private final int EXP_IN_WISH_BOTTLE_R = 50;
    private final int EXP_IN_WISH_BOTTLE_SR = 250;
    private final int EXP_IN_WISH_BOTTLE_SSR = 1000;
    private final int CRYSTAL_N_PER_GENERAL_BOX = 5;
    private final int CRYSTAL_R_PER_GENERAL_BOX = 2;
    private final int CRYSTAL_SR_PER_GENERAL_BOX = 1;

    private int expCost;
    private int goldCost;
    private List<CrystalCost> crystalCosts;

    public CardLevelingCost() {
        expCost = 0;
        goldCost = 0;
        crystalCosts = new ArrayList<>();
    }

    public CardLevelingCost(int expCost, int goldCost, List<CrystalCost> crystalCosts) {
        this.expCost = expCost;
        this.goldCost = goldCost;
        this.crystalCosts = crystalCosts;
    }

    public CardLevelingCost(CardLevelingCost cardLevelingCost) {
        this.expCost = cardLevelingCost.getExpCost();
        this.goldCost = cardLevelingCost.getGoldCost();
        crystalCosts = new ArrayList<>();
        cardLevelingCost.getCrystalCosts().forEach(crystalCost -> crystalCosts.add(new CrystalCost(crystalCost.getStellacrum(), crystalCost.getN(), crystalCost.getR(), crystalCost.getSr())));
    }

    public void loadFromLevelingInformation(List<LevelingCost> levelingCosts, List<AscensionCost> ascensionCosts, String stellacrum) {
        levelingCosts.forEach(levelingCost -> expCost += levelingCost.getExp());
        CrystalCost crystalCost = new CrystalCost();
        crystalCost.setStellacrum(stellacrum);
        ascensionCosts.forEach(ascensionCost -> {
            goldCost += ascensionCost.getGold();
            crystalCost.setN(crystalCost.getN() + ascensionCost.getCrystalCostN());
            crystalCost.setR(crystalCost.getR() + ascensionCost.getCrystalCostR());
            crystalCost.setSr(crystalCost.getSr() + ascensionCost.getCrystalCostSr());
        });
        crystalCosts.add(crystalCost);
    }

    public CardLevelingCost loadFromResourceTracking(ResourceTracking resourceTracking) {
        goldCost = resourceTracking.getGold();
        for (ResourceTrackingValues rtv : resourceTracking.getResourceTrackingValues()) {
            String resourceType = rtv.getResourceType();
            switch (resourceType) {
                case "wish bottle" ->
                    expCost = rtv.getN() * EXP_IN_WISH_BOTTLE_N + rtv.getR() * EXP_IN_WISH_BOTTLE_R + rtv.getSr() * EXP_IN_WISH_BOTTLE_SR + rtv.getSsr() * EXP_IN_WISH_BOTTLE_SSR;
                case "violet crystal" ->
                    crystalCosts.add(new CrystalCost("Violet", rtv.getN(), rtv.getR(), rtv.getSr()));
                case "pearl crystal" ->
                    crystalCosts.add(new CrystalCost("Pearl", rtv.getN(), rtv.getR(), rtv.getSr()));
                case "amber crystal" ->
                    crystalCosts.add(new CrystalCost("Amber", rtv.getN(), rtv.getR(), rtv.getSr()));
                case "ruby crystal" ->
                    crystalCosts.add(new CrystalCost("Ruby", rtv.getN(), rtv.getR(), rtv.getSr()));
                case "sapphire crystal" ->
                    crystalCosts.add(new CrystalCost("Sapphire", rtv.getN(), rtv.getR(), rtv.getSr()));
                case "emerald crystal" ->
                    crystalCosts.add(new CrystalCost("Emerald", rtv.getN(), rtv.getR(), rtv.getSr()));
            }
        }
        return this;
    }

    public CardLevelingCost subtract(CardLevelingCost compare) {
        CardLevelingCost result = new CardLevelingCost();
        result.setExpCost(Math.max(this.expCost - compare.getExpCost(), 0));
        result.setGoldCost(Math.max(this.goldCost - compare.getGoldCost(), 0));
        this.crystalCosts.forEach(crystalCost -> {
            Optional<CrystalCost> secondCC = compare.crystalCosts.stream().filter(predicate -> predicate.equals(crystalCost)).findFirst();
            result.getCrystalCosts().add(secondCC.isPresent() ? crystalCost.subtract(secondCC.get()) : crystalCost);
        });
        return result;
    }

    public void add(CardLevelingCost cardLevelingCost) {
        expCost += cardLevelingCost.getExpCost();
        goldCost += cardLevelingCost.getGoldCost();
        for (CrystalCost crystalCost : cardLevelingCost.crystalCosts) {
            Optional<CrystalCost> existingCrystalCostOptional = this.crystalCosts.stream().filter(cost -> cost.equals(crystalCost)).findFirst();
            if (existingCrystalCostOptional.isPresent()) {
                existingCrystalCostOptional.get().add(crystalCost);
            } else {
                this.crystalCosts.add(crystalCost);
            }
        }
    }

    public CardLevelingCost useAscensionBoxes(ResourceTrackingValues ascensionBoxes, List<BountyReward> playerBounties) {
        CardLevelingCost result = new CardLevelingCost(this);
        result.useAscensionBox(ascensionBoxes.getN(), "N", playerBounties);
        result.useAscensionBox(ascensionBoxes.getR(), "R", playerBounties);
        result.useAscensionBox(ascensionBoxes.getSr(), "SR", playerBounties);
        result.useAscensionBox(ascensionBoxes.getGeneral(), "General", playerBounties);
        return result;
    }

    private void useAscensionBox(int numberOfBoxes, String boxName, List<BountyReward> playerBounties) {
        while (numberOfBoxes > 0) {
            HighestStaminaCostCrystals highestCost = findHighestCostCrystals(playerBounties, boxName);
            AscensionBoxesNeeded boxesNeeded = highestCost.getAscensionBoxesNeeded(playerBounties, boxName);
            if (boxesNeeded.getTotalBoxes() == 0) {
                break;
            }
            if (boxesNeeded.getTotalBoxes() > numberOfBoxes) {
                // divide remaining boxes evenly
                useBoxesNeeded(boxesNeeded, numberOfBoxes, boxName, true);
                numberOfBoxes = 0;
            } else {
                useBoxesNeeded(boxesNeeded, numberOfBoxes, boxName, false);
                numberOfBoxes -= boxesNeeded.getTotalBoxes();
            }
        }
    }

    private void useBoxesNeeded(AscensionBoxesNeeded boxesNeeded, int numberOfBoxes, String boxName, boolean lastRun) {
        Map<String, Integer> boxesNeededPerCrystal = boxesNeeded.getBoxesNeededPerCrystal();
        boolean isGeneralBox = boxName.equals("General");
        int boxPerNAndR = isGeneralBox && boxesNeeded.getSrCrystals() != 0 ? (numberOfBoxes * 2) / (boxesNeededPerCrystal.size() + boxesNeeded.getnCrystals() + boxesNeeded.getrCrystals())
                : numberOfBoxes / boxesNeededPerCrystal.size();
        int boxPerSr = isGeneralBox && (boxesNeeded.getnCrystals() + boxesNeeded.getrCrystals()) != 0 ? (numberOfBoxes) / (boxesNeededPerCrystal.size() + boxesNeeded.getnCrystals() + boxesNeeded.getrCrystals())
                : numberOfBoxes / boxesNeededPerCrystal.size();
        for (Map.Entry<String, Integer> currentBoxesNeeded : boxesNeededPerCrystal.entrySet()) {
            String[] keyParts = currentBoxesNeeded.getKey().split("_");
            CrystalCost crystalCost = crystalCosts.stream().filter(cc -> cc.equals(keyParts[0])).findFirst().get();
            switch (keyParts[1]) {
                case "N" -> {
                    int crystalsSaved = (lastRun ? boxPerNAndR : currentBoxesNeeded.getValue()) * (isGeneralBox ? CRYSTAL_N_PER_GENERAL_BOX : 1);
                    crystalCost.setN(Math.max(crystalCost.getN() - crystalsSaved, 0));
                }
                case "R" -> {
                    int crystalsSaved = (lastRun ? boxPerNAndR : currentBoxesNeeded.getValue()) * (isGeneralBox ? CRYSTAL_R_PER_GENERAL_BOX : 1);
                    crystalCost.setR(Math.max(crystalCost.getR() - crystalsSaved, 0));
                }
                case "SR" -> {
                    int crystalsSaved = (lastRun ? boxPerSr : currentBoxesNeeded.getValue()) * (isGeneralBox ? CRYSTAL_SR_PER_GENERAL_BOX : 1);
                    crystalCost.setSr(Math.max(crystalCost.getSr() - crystalsSaved, 0));
                }
            }
        }
    }

    private HighestStaminaCostCrystals findHighestCostCrystals(List<BountyReward> playerBounties, String boxName) {
        HighestStaminaCostCrystals highestCost = new HighestStaminaCostCrystals();
        for (CrystalCost crystalCost : crystalCosts) {
            BountyReward crystalBounty = getBountyByStellacrum(playerBounties, crystalCost.getStellacrum());
            boolean isGeneralBox = boxName.equals("General");
            if (isGeneralBox || boxName.equals("N")) {
                highestCost.compareWithHighestValue(crystalBounty, crystalCost.getN(), crystalCost.getStellacrum(), "N");
            }
            if (isGeneralBox || boxName.equals("R")) {
                highestCost.compareWithHighestValue(crystalBounty, crystalCost.getR(), crystalCost.getStellacrum(), "R");
            }
            if (isGeneralBox || boxName.equals("SR")) {
                highestCost.compareWithHighestValue(crystalBounty, crystalCost.getSr(), crystalCost.getStellacrum(), "SR");
            }
        }
        return highestCost;
    }

    public static BountyReward getBountyByStellacrum(List<BountyReward> playerBounties, String stellacrum) {
        String bountyName = "";
        switch (stellacrum) {
            case "Amber", "Emerald" -> bountyName = "Lemonette";
            case "Violet", "Pearl" -> bountyName = "Snoozer";
            case "Ruby", "Sapphire" -> bountyName = "Pumpkin Magus";
        }
        String finalBountyName = bountyName;
        return playerBounties.stream().filter(playerBounty -> playerBounty.getBountyNameType().equals(finalBountyName)).findFirst().get();
    }

    public StaminaCost getStaminaCost(List<BountyReward> bountyRewards) {
        StaminaCost staminaCost = new StaminaCost();
        for (BountyReward bountyReward : bountyRewards) {
            List<String> crystalStellacrumFilter = new ArrayList<>();
            String bountyName = bountyReward.getBountyNameType();
            switch (bountyName) {
                case "Heartbreaker" -> {
                    int expPerRun = bountyReward.getR() == null && bountyReward.getSr() == null ? bountyReward.getN() * EXP_IN_WISH_BOTTLE_N : bountyReward.getSr() == null ?
                            bountyReward.getN() * EXP_IN_WISH_BOTTLE_N + bountyReward.getR() * bountyReward.getR() * EXP_IN_WISH_BOTTLE_R :
                            bountyReward.getN() * EXP_IN_WISH_BOTTLE_N + bountyReward.getR() * bountyReward.getR() * EXP_IN_WISH_BOTTLE_R + bountyReward.getSr() * EXP_IN_WISH_BOTTLE_SR;
                    staminaCost.setExpStaminaCost((this.expCost / expPerRun) * bountyReward.getStaminaCost());
                }
                case "Mr. Beanie" ->
                        staminaCost.setGoldStaminaCost((this.goldCost / bountyReward.getN()) * bountyReward.getStaminaCost());
                case "Lemonette" -> crystalStellacrumFilter = Arrays.asList("Emerald", "Amber");
                case "Pumpkin Magus" -> crystalStellacrumFilter = Arrays.asList("Ruby", "Sapphire");
                case "Snoozer" -> crystalStellacrumFilter = Arrays.asList("Violet", "Pearl");
            }
            if (!crystalStellacrumFilter.isEmpty()) {
                List<String> finalCrystalStellacrumFilter = crystalStellacrumFilter;
                crystalCosts.stream()
                        .filter(predicate -> predicate.equals(new CrystalCost(finalCrystalStellacrumFilter.getFirst()))
                                || predicate.equals(new CrystalCost(finalCrystalStellacrumFilter.get(1))))
                        .forEach(crystalCost -> {
                            int currentStamina = Math.max((crystalCost.getN() / bountyReward.getN()) * bountyReward.getStaminaCost(), Math.max((crystalCost.getR() / bountyReward.getR()) * bountyReward.getStaminaCost(), (crystalCost.getSr() / bountyReward.getSr()) * bountyReward.getStaminaCost()));
                            staminaCost.getCrystalStaminaCosts().put(bountyName, staminaCost.getCrystalStaminaCosts().containsKey(bountyName) ? Math.max(staminaCost.getCrystalStaminaCosts().get(bountyName), currentStamina) : currentStamina);
                        });
            }
        }
        staminaCost.setTotalStaminaCostByCurrentValues();
        return staminaCost;
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

    public List<CrystalCost> getCrystalCosts() {
        return crystalCosts;
    }

    public void setCrystalCosts(List<CrystalCost> crystalCosts) {
        this.crystalCosts = crystalCosts;
    }
}
