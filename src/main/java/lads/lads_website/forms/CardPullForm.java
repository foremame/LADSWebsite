package lads.lads_website.forms;

import lads.lads_website.forms.subforms.CardPullSubForm;

import java.util.ArrayList;
import java.util.List;

public class CardPullForm {

    private String pullTimestamp;

    private Long bannerId;

    private Boolean multipleCardPulls;

    private List<CardPullSubForm> cardPulls;

    public CardPullForm() {
        cardPulls = new ArrayList<>();
    }

    public String getPullTimestamp() {
        return pullTimestamp;
    }

    public Long getBannerId() {
        return bannerId;
    }

    public void setPullTimestamp(String pullTimestamp) {
        this.pullTimestamp = pullTimestamp;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public List<CardPullSubForm> getCardPulls() {
        return cardPulls;
    }

    public void setCardPulls(List<CardPullSubForm> cardPulls) {
        this.cardPulls = cardPulls;
    }

    public Boolean getMultipleCardPulls() {
        return multipleCardPulls;
    }

    public void setMultipleCardPulls(Boolean multipleCardPulls) {
        this.multipleCardPulls = multipleCardPulls;
    }
}
