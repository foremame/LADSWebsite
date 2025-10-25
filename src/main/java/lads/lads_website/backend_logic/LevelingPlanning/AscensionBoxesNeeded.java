package lads.lads_website.backend_logic.LevelingPlanning;

import java.util.HashMap;
import java.util.Map;

public class AscensionBoxesNeeded {
    private int totalBoxes;
    private int nCrystals;
    private int rCrystals;
    private int srCrystals;
    private Map<String, Integer> boxesNeededPerCrystal;

    public AscensionBoxesNeeded() {
        totalBoxes = 0;
        nCrystals = 0;
        rCrystals = 0;
        srCrystals = 0;
        boxesNeededPerCrystal = new HashMap<>();
    }

    public void addNewBoxAmount(int numBoxes, String fullKey, String crystalType) {
        switch (crystalType) {
            case "N" -> nCrystals++;
            case "R" -> rCrystals++;
            case "SR" -> srCrystals++;
        }
        totalBoxes += numBoxes;
        boxesNeededPerCrystal.put(fullKey, numBoxes);
    }

    public int getTotalBoxes() {
        return totalBoxes;
    }

    public void setTotalBoxes(int totalBoxes) {
        this.totalBoxes = totalBoxes;
    }

    public int getnCrystals() {
        return nCrystals;
    }

    public void setnCrystals(int nCrystals) {
        this.nCrystals = nCrystals;
    }

    public int getrCrystals() {
        return rCrystals;
    }

    public void setrCrystals(int rCrystals) {
        this.rCrystals = rCrystals;
    }

    public int getSrCrystals() {
        return srCrystals;
    }

    public void setSrCrystals(int srCrystals) {
        this.srCrystals = srCrystals;
    }

    public Map<String, Integer> getBoxesNeededPerCrystal() {
        return boxesNeededPerCrystal;
    }

    public void setBoxesNeededPerCrystal(Map<String, Integer> boxesNeededPerCrystal) {
        this.boxesNeededPerCrystal = boxesNeededPerCrystal;
    }
}
