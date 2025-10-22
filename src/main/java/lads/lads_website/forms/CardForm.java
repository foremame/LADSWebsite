package lads.lads_website.forms;

public class CardForm {
    private String name;
    private Long eventId;
    private Long bannerId;
    private String cardOrigin;
    private String loveInterestType;
    private String cardType;
    private String rarityType;
    private String stellacrumType;
    private String mainStatType;

    public CardForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getBannerId() {
        return bannerId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public String getCardOrigin() {
        return cardOrigin;
    }

    public void setCardOrigin(String cardOrigin) {
        this.cardOrigin = cardOrigin;
    }

    public String getLoveInterestType() {
        return loveInterestType;
    }

    public void setLoveInterestType(String loveInterestType) {
        this.loveInterestType = loveInterestType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getRarityType() {
        return rarityType;
    }

    public void setRarityType(String rarityType) {
        this.rarityType = rarityType;
    }

    public String getStellacrumType() {
        return stellacrumType;
    }

    public void setStellacrumType(String stellacrumType) {
        this.stellacrumType = stellacrumType;
    }

    public String getMainStatType() {
        return mainStatType;
    }

    public void setMainStatType(String mainStatType) {
        this.mainStatType = mainStatType;
    }
}
