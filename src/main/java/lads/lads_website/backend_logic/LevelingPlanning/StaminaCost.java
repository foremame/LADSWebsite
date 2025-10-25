package lads.lads_website.backend_logic.LevelingPlanning;

import java.util.HashMap;
import java.util.Map;

public class StaminaCost {
    private int expStaminaCost;
    private int goldStaminaCost;
    private Map<String, Integer> crystalStaminaCosts;
    private int total;

    public StaminaCost() {
        total = 0;
        crystalStaminaCosts = new HashMap<>();
    }

    public StaminaCost(int expStaminaCost, int goldStaminaCost, Map<String, Integer> crystalStaminaCosts) {
        this.expStaminaCost = expStaminaCost;
        this.goldStaminaCost = goldStaminaCost;
        this.crystalStaminaCosts = crystalStaminaCosts;
        total = 0;
    }

    public StaminaCost(int expStaminaCost, int goldStaminaCost, Map<String, Integer> crystalStaminaCosts, int total) {
        this.expStaminaCost = expStaminaCost;
        this.goldStaminaCost = goldStaminaCost;
        this.crystalStaminaCosts = crystalStaminaCosts;
        this.total = total;
    }

    public void setTotalStaminaCostByCurrentValues() {
        total += expStaminaCost + goldStaminaCost;
        crystalStaminaCosts.forEach((key,value) -> total += value);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getExpStaminaCost() {
        return expStaminaCost;
    }

    public void setExpStaminaCost(int expStaminaCost) {
        this.expStaminaCost = expStaminaCost;
    }

    public int getGoldStaminaCost() {
        return goldStaminaCost;
    }

    public void setGoldStaminaCost(int goldStaminaCost) {
        this.goldStaminaCost = goldStaminaCost;
    }

    public Map<String, Integer> getCrystalStaminaCosts() {
        return crystalStaminaCosts;
    }

    public void setCrystalStaminaCosts(Map<String, Integer> crystalStaminaCosts) {
        this.crystalStaminaCosts = crystalStaminaCosts;
    }
}
