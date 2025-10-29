package lads.lads_website.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table
public class CardPull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime pullTimestamp;

    @Column(columnDefinition="BIT")
    private Boolean hitHardPity;

    private Integer pullsUntilHardPity;

    @Column(name="is_limited", columnDefinition="BIT")
    private Boolean limited;

    @Column(columnDefinition="BIT")
    private Boolean wonFiftyFifty;

    @Transient
    private LocalDate addDate;

    @Column(name="is_precise_wish", columnDefinition="BIT")
    private Boolean preciseWish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="activity_run_period_id")
    private ActivityRunPeriod activityRunPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_card_id")
    private PlayerCard playerCard;

    public CardPull() {
    }

    public CardPull(Long id, LocalDateTime pullTimestamp, Boolean hitHardPity, Integer pullsUntilHardPity, Boolean limited, Boolean wonFiftyFifty, LocalDate addDate, Boolean preciseWish, ActivityRunPeriod activityRunPeriod, PlayerCard playerCard) {
        this.id = id;
        this.pullTimestamp = pullTimestamp;
        this.hitHardPity = hitHardPity;
        this.pullsUntilHardPity = pullsUntilHardPity;
        this.limited = limited;
        this.wonFiftyFifty = wonFiftyFifty;
        this.addDate = addDate;
        this.preciseWish = preciseWish;
        this.activityRunPeriod = activityRunPeriod;
        this.playerCard = playerCard;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPullTimestamp() {
        return pullTimestamp;
    }

    public void setPullTimestamp(LocalDateTime pullTimestamp) {
        this.pullTimestamp = pullTimestamp;
    }

    public Boolean getHitHardPity() {
        return hitHardPity;
    }

    public void setHitHardPity(Boolean hitHardPity) {
        this.hitHardPity = hitHardPity;
    }

    public Integer getPullsUntilHardPity() {
        return pullsUntilHardPity;
    }

    public void setPullsUntilHardPity(Integer pullsUntilHardPity) {
        this.pullsUntilHardPity = pullsUntilHardPity;
    }

    public Boolean getLimited() {
        return limited;
    }

    public void setLimited(Boolean limited) {
        this.limited = limited;
    }

    public Boolean getWonFiftyFifty() {
        return wonFiftyFifty;
    }

    public void setWonFiftyFifty(Boolean wonFiftyFifty) {
        this.wonFiftyFifty = wonFiftyFifty;
    }

    public LocalDate getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    public ActivityRunPeriod getActivityRunPeriod() {
        return activityRunPeriod;
    }

    public void setActivityRunPeriod(ActivityRunPeriod activityRunPeriod) {
        this.activityRunPeriod = activityRunPeriod;
    }

    public PlayerCard getPlayerCard() {
        return playerCard;
    }

    public void setPlayerCard(PlayerCard playerCard) {
        this.playerCard = playerCard;
    }

    public Boolean getPreciseWish() {
        return preciseWish;
    }

    public void setPreciseWish(Boolean preciseWish) {
        this.preciseWish = preciseWish;
    }
}
