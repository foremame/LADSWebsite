package lads.lads_website.backend_logic.LevelingPlanning;

import lads.lads_website.domain.BountyReward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighestStaminaCostCrystals {
    private final int CRYSTAL_N_PER_GENERAL_BOX = 5;
    private final int CRYSTAL_R_PER_GENERAL_BOX = 2;
    private final int CRYSTAL_SR_PER_GENERAL_BOX = 1;
    private final int CANNOT_FARM_CRYSTAL = -1;
    private int highestStaminaCost;
    private int secondHighestStaminaCost;
    private Map<String, Integer> highestCrystalCosts;

    public HighestStaminaCostCrystals() {
        highestStaminaCost = 0;
        secondHighestStaminaCost = 0;
        highestCrystalCosts = new HashMap<>();
    }

    public void compareWithHighestValue(BountyReward crystalBounty, int crystals, String stellacrum, String crystalType) {
        int staminaCost = calculateStaminaCost(crystalBounty, crystals, crystalType);
        String fullKey = stellacrum + "_" + crystalType;
        if (highestStaminaCost == CANNOT_FARM_CRYSTAL || staminaCost == CANNOT_FARM_CRYSTAL) {
            if (highestStaminaCost == staminaCost) {
                highestCrystalCosts.put(fullKey, crystals);
            } else if (staminaCost == CANNOT_FARM_CRYSTAL) {
                highestStaminaCost = CANNOT_FARM_CRYSTAL;
                highestCrystalCosts.clear();
                highestCrystalCosts.put(fullKey, crystals);
            }
        } else if (highestStaminaCost == staminaCost) {
            highestCrystalCosts.put(fullKey, crystals);
        } else if (highestStaminaCost < staminaCost) {
            secondHighestStaminaCost = highestStaminaCost;
            highestStaminaCost = staminaCost;
            highestCrystalCosts.clear();
            highestCrystalCosts.put(fullKey, crystals);
        } else if (secondHighestStaminaCost < staminaCost) {
            secondHighestStaminaCost = staminaCost;
        }
    }

    public int calculateStaminaCost(BountyReward crystalBounty, int crystals, String crystalType) {
        Integer crystalPerBounty = crystalType.equals("N") ? crystalBounty.getN() : crystalType.equals("R") ? crystalBounty.getR() : crystalBounty.getSr();
        return crystalPerBounty == null ? CANNOT_FARM_CRYSTAL : (crystals / crystalPerBounty) * crystalBounty.getStaminaCost();
    }

    public AscensionBoxesNeeded getAscensionBoxesNeeded(List<BountyReward> playerBounties, String boxName) {
        boolean allCrystalsNeeded = highestStaminaCost == CANNOT_FARM_CRYSTAL;
        int staminaCost = highestStaminaCost - secondHighestStaminaCost;
        AscensionBoxesNeeded boxesNeeded = new AscensionBoxesNeeded();
        for (Map.Entry<String, Integer> highestCrystals : highestCrystalCosts.entrySet()) {
            String[] crystalType = highestCrystals.getKey().split("_");
            BountyReward bountyReward = CardLevelingCost.getBountyByStellacrum(playerBounties, crystalType[0]);
            Integer[] crystalsPerRun = crystalType[1].equals("N") ? new Integer[]{bountyReward.getN(), CRYSTAL_N_PER_GENERAL_BOX} : crystalType[1].equals("R") ? new Integer[]{bountyReward.getR(), CRYSTAL_R_PER_GENERAL_BOX} : new Integer[]{bountyReward.getSr(), CRYSTAL_SR_PER_GENERAL_BOX};
            int boxes = allCrystalsNeeded ? highestCrystals.getValue() : (staminaCost / bountyReward.getStaminaCost()) * crystalsPerRun[0];
            if (boxName.equals("General")) {
                boxes = boxes / crystalsPerRun[1];
            }
            boxesNeeded.addNewBoxAmount(boxes, highestCrystals.getKey(), crystalType[1]);
        }
        return boxesNeeded;
    }

    public int getHighestStaminaCost() {
        return highestStaminaCost;
    }

    public void setHighestStaminaCost(int highestStaminaCost) {
        this.highestStaminaCost = highestStaminaCost;
    }

    public int getSecondHighestStaminaCost() {
        return secondHighestStaminaCost;
    }

    public void setSecondHighestStaminaCost(int secondHighestStaminaCost) {
        this.secondHighestStaminaCost = secondHighestStaminaCost;
    }

    public Map<String, Integer> getHighestCrystalCosts() {
        return highestCrystalCosts;
    }

    public void setHighestCrystalCosts(Map<String, Integer> highestCrystalCosts) {
        this.highestCrystalCosts = highestCrystalCosts;
    }
}
