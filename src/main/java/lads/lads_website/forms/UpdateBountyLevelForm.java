package lads.lads_website.forms;

public class UpdateBountyLevelForm {
    private String bountyName;
    private int level;

    public UpdateBountyLevelForm() {
    }

    public String getBountyName() {
        return bountyName;
    }

    public void setBountyName(String bountyName) {
        this.bountyName = bountyName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
