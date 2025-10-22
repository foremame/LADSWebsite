package lads.lads_website.forms.list_wrappers;

public class PlayerCardList {
    private Long cardId;
    private String name;

    public PlayerCardList() {
    }

    public PlayerCardList(Long cardId, String name) {
        this.cardId = cardId;
        this.name = name;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
