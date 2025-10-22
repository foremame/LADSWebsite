package lads.lads_website.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class ResourceTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer purpleDiamonds;

    private Integer diamonds;

    private Integer deepspaceWish;

    private Integer empyreanWish;

    private Integer gold;

    private Integer awakeningHeartSr;

    private Integer awakeningHeartSsr;

    private LocalDateTime addDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="player_id")
    private Player player;

    @OneToMany(mappedBy = "resourceTracking", fetch = FetchType.LAZY)
    private List<ResourceTrackingValues> resourceTrackingValues = new ArrayList<>();

    public ResourceTracking() {
    }

    public ResourceTracking(Long id, Integer purpleDiamonds, Integer diamonds, Integer deepspaceWish, Integer empyreanWish, Integer gold, Integer awakeningHeartSr, Integer awakeningHeartSsr, LocalDateTime addDate, Player player, List<ResourceTrackingValues> resourceTrackingValues) {
        this.id = id;
        this.purpleDiamonds = purpleDiamonds;
        this.diamonds = diamonds;
        this.deepspaceWish = deepspaceWish;
        this.empyreanWish = empyreanWish;
        this.gold = gold;
        this.awakeningHeartSr = awakeningHeartSr;
        this.awakeningHeartSsr = awakeningHeartSsr;
        this.addDate = addDate;
        this.player = player;
        this.resourceTrackingValues = resourceTrackingValues;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPurpleDiamonds() {
        return purpleDiamonds;
    }

    public void setPurpleDiamonds(Integer purpleDiamonds) {
        this.purpleDiamonds = purpleDiamonds;
    }

    public Integer getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(Integer diamonds) {
        this.diamonds = diamonds;
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

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDateTime addDate) {
        this.addDate = addDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<ResourceTrackingValues> getResourceTrackingValues() {
        return resourceTrackingValues;
    }

    public void setResourceTrackingValues(List<ResourceTrackingValues> resourceTrackingValues) {
        this.resourceTrackingValues = resourceTrackingValues;
    }
}
