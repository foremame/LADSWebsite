package lads.lads_website.forms;

import lads.lads_website.forms.subforms.LevelingPlanningSubForm;

import java.util.ArrayList;
import java.util.List;

public class LevelingPlanningForm {
    private int numCards;
    private List<LevelingPlanningSubForm> cardInfo;

    public LevelingPlanningForm() {
        cardInfo = new ArrayList<>();
    }

    public int getNumCards() {
        return numCards;
    }

    public void setNumCards(int numCards) {
        this.numCards = numCards;
    }

    public List<LevelingPlanningSubForm> getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(List<LevelingPlanningSubForm> cardInfo) {
        this.cardInfo = cardInfo;
    }
}
