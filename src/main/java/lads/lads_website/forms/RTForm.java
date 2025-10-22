package lads.lads_website.forms;

import java.util.ArrayList;
import java.util.List;

public class RTForm {
    private Integer diamonds;
    private Integer purpleDiamonds;
    private Integer deepspaceWish;
    private Integer empyreanWish;
    private Integer gold;
    private Integer awakeningHeartSr;
    private Integer awakeningHeartSsr;
    private List<RTValuesForm> rtValuesForms;

    public RTForm() {
        rtValuesForms = new ArrayList<>();
    }

    public Integer getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(Integer diamonds) {
        this.diamonds = diamonds;
    }

    public Integer getPurpleDiamonds() {
        return purpleDiamonds;
    }

    public void setPurpleDiamonds(Integer purpleDiamonds) {
        this.purpleDiamonds = purpleDiamonds;
    }

    public Integer getDeepspaceWish() {
        return deepspaceWish;
    }

    public void setDeepspaceWish(Integer deepspaceWish) {
        this.deepspaceWish = deepspaceWish;
    }

    public Integer getEmpyreanWish() {
        return empyreanWish;
    }

    public void setEmpyreanWish(Integer empyreanWish) {
        this.empyreanWish = empyreanWish;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getAwakeningHeartSr() {
        return awakeningHeartSr;
    }

    public void setAwakeningHeartSr(Integer awakeningHeartSr) {
        this.awakeningHeartSr = awakeningHeartSr;
    }

    public Integer getAwakeningHeartSsr() {
        return awakeningHeartSsr;
    }

    public void setAwakeningHeartSsr(Integer awakeningHeartSsr) {
        this.awakeningHeartSsr = awakeningHeartSsr;
    }

    public List<RTValuesForm> getRtValuesForms() {
        return rtValuesForms;
    }

    public void setRtValuesForms(List<RTValuesForm> rtValuesForms) {
        this.rtValuesForms = rtValuesForms;
    }
}
