package lads.lads_website.forms;

public class PlayerCardForm {
    private Long cardId;
    private Long playerCardId;
    private String rankType;
    private Integer level;
    private Boolean awakened;

    public PlayerCardForm() {
    }

    public Long getPlayerCardId() {
        return playerCardId;
    }

    public void setPlayerCardId(Long playerCardId) {
        this.playerCardId = playerCardId;
    }

    public Boolean getAwakened() {
        return awakened;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getRankType() {
        return rankType;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setAwakened(Boolean awakened) {
        this.awakened = awakened;
    }
}
