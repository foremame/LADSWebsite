package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table(name="ascension_cost")
public class AscensionCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int level;

    private String rarityType;

    @Column(name="crystal_cost_n")
    private Integer crystalCostN;

    @Column(name="crystal_cost_r")
    private Integer crystalCostR;

    @Column(name="crystal_cost_sr")
    private Integer crystalCostSr;

    private Integer gold;

    public AscensionCost() {
    }

    public AscensionCost(int level, Long id, String rarityType, Integer crystalCostN, Integer crystalCostSr, Integer crystalCostR, Integer gold) {
        this.level = level;
        this.id = id;
        this.rarityType = rarityType;
        this.crystalCostN = crystalCostN;
        this.crystalCostSr = crystalCostSr;
        this.crystalCostR = crystalCostR;
        this.gold = gold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRarityType() {
        return rarityType;
    }

    public void setRarityType(String rarityType) {
        this.rarityType = rarityType;
    }

    public Integer getCrystalCostN() {
        return crystalCostN;
    }

    public void setCrystalCostN(Integer crystalCostN) {
        this.crystalCostN = crystalCostN;
    }

    public Integer getCrystalCostR() {
        return crystalCostR;
    }

    public void setCrystalCostR(Integer crystalCostR) {
        this.crystalCostR = crystalCostR;
    }

    public Integer getCrystalCostSr() {
        return crystalCostSr;
    }

    public void setCrystalCostSr(Integer crystalCostSr) {
        this.crystalCostSr = crystalCostSr;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }
}
