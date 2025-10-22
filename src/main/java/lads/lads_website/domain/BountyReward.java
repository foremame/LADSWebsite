package lads.lads_website.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class BountyReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bountyNameType;

    private int level;

    private int staminaCost;

    private String resourceType;

    private Integer n;

    private Integer r;

    private Integer sr;

    @OneToMany(mappedBy = "bountyReward", fetch = FetchType.LAZY)
    private List<PlayerBounty> playerBounties = new ArrayList<>();

    public BountyReward() {
    }

    public BountyReward(Long id, String bountyNameType, int level, int staminaCost, String resourceType, Integer n, Integer r, Integer sr, List<PlayerBounty> playerBounties) {
        this.id = id;
        this.bountyNameType = bountyNameType;
        this.level = level;
        this.staminaCost = staminaCost;
        this.resourceType = resourceType;
        this.n = n;
        this.r = r;
        this.sr = sr;
        this.playerBounties = playerBounties;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBountyNameType() {
        return bountyNameType;
    }

    public void setBountyNameType(String bountyNameType) {
        this.bountyNameType = bountyNameType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public void setStaminaCost(int staminaCost) {
        this.staminaCost = staminaCost;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public Integer getSr() {
        return sr;
    }

    public void setSr(Integer sr) {
        this.sr = sr;
    }

    public List<PlayerBounty> getPlayerBounties() {
        return playerBounties;
    }

    public void setPlayerBounties(List<PlayerBounty> playerBounties) {
        this.playerBounties = playerBounties;
    }
}
