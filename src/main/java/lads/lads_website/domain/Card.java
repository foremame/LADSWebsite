package lads.lads_website.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String name;

    private String loveInterestType;

    private String rarityType;

    private String cardType;

    private String stellacrumType;

    private String mainStatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="card_origin_id")
    private CardOrigin cardOrigin;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    private List<PlayerCard> playerCards = new ArrayList<>();

    public Card() {
    }

    public Card(Long id, String name, String loveInterestType, String rarityType, String cardType, String stellacrumType, String mainStatType, CardOrigin cardOrigin, List<PlayerCard> playerCards) {
        this.id = id;
        this.name = name;
        this.loveInterestType = loveInterestType;
        this.rarityType = rarityType;
        this.cardType = cardType;
        this.stellacrumType = stellacrumType;
        this.mainStatType = mainStatType;
        this.cardOrigin = cardOrigin;
        this.playerCards = playerCards;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoveInterestType() {
        return loveInterestType;
    }

    public void setLoveInterestType(String loveInterestType) {
        this.loveInterestType = loveInterestType;
    }

    public String getRarityType() {
        return rarityType;
    }

    public void setRarityType(String rarityType) {
        this.rarityType = rarityType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    public CardOrigin getCardOrigin() {
        return cardOrigin;
    }

    public void setCardOrigin(CardOrigin cardOrigin) {
        this.cardOrigin = cardOrigin;
    }

    public List<PlayerCard> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<PlayerCard> playerCards) {
        this.playerCards = playerCards;
    }
}
