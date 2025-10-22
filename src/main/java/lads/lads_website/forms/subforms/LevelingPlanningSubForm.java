package lads.lads_website.forms.subforms;

public class LevelingPlanningSubForm {
    private String rarityType;
    private int startLevel;
    private int endLevel;
    private String stellacrum;
    private Boolean startAscension;
    private Boolean endAscension;

    public LevelingPlanningSubForm() {
    }

    public String getRarityType() {
        return rarityType;
    }

    public void setRarityType(String rarityType) {
        this.rarityType = rarityType;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    public int getEndLevel() {
        return endLevel;
    }

    public void setEndLevel(int endLevel) {
        this.endLevel = endLevel;
    }

    public String getStellacrum() {
        return stellacrum;
    }

    public void setStellacrum(String stellacrum) {
        this.stellacrum = stellacrum;
    }

    public Boolean getStartAscension() {
        return startAscension;
    }

    public void setStartAscension(Boolean startAscension) {
        this.startAscension = startAscension;
    }

    public Boolean getEndAscension() {
        return endAscension;
    }

    public void setEndAscension(Boolean endAscension) {
        this.endAscension = endAscension;
    }
}
