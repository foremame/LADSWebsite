package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table
public class LevelingCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rarityType;

    private int level;

    private int exp;

    public LevelingCost() {
    }

    public LevelingCost(Long id, String rarityType, int level, int exp) {
        this.id = id;
        this.rarityType = rarityType;
        this.level = level;
        this.exp = exp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRarityType() {
        return rarityType;
    }

    public void setRarityType(String rarityType) {
        this.rarityType = rarityType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
