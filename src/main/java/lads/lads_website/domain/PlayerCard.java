package lads.lads_website.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class PlayerCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rankType;

    private Integer level;

    @Column(name="isAwakened")
    private Boolean awakened;

    @Transient
    private LocalDate addDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="card_id")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "playerCard", fetch = FetchType.LAZY)
    private List<CardPull> cardPulls = new ArrayList<>();

    public PlayerCard() {
    }

    public PlayerCard(Long id, String rankType, Integer level, Boolean awakened, LocalDate addDate, Card card, Player player, List<CardPull> cardPulls) {
        this.id = id;
        this.rankType = rankType;
        this.level = level;
        this.awakened = awakened;
        this.addDate = addDate;
        this.card = card;
        this.player = player;
        this.cardPulls = cardPulls;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getAwakened() {
        return awakened;
    }

    public void setAwakened(Boolean awakened) {
        this.awakened = awakened;
    }

    public LocalDate getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<CardPull> getCardPulls() {
        return cardPulls;
    }

    public void setCardPulls(List<CardPull> cardPulls) {
        this.cardPulls = cardPulls;
    }
}
