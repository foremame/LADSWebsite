package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table
public class PackPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tier;

    private int numOfWishes;

    private float price;

    private Integer amountOfPacks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="banner_category_id")
    private BannerCategory bannerCategory;

    public PackPrice() {
    }

    public PackPrice(Long id, int tier, int numOfWishes, float price, Integer amountOfPacks, BannerCategory bannerCategory) {
        this.id = id;
        this.tier = tier;
        this.numOfWishes = numOfWishes;
        this.price = price;
        this.amountOfPacks = amountOfPacks;
        this.bannerCategory = bannerCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getNumOfWishes() {
        return numOfWishes;
    }

    public void setNumOfWishes(int numOfWishes) {
        this.numOfWishes = numOfWishes;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Integer getAmountOfPacks() {
        return amountOfPacks;
    }

    public void setAmountOfPacks(Integer amountOfPacks) {
        this.amountOfPacks = amountOfPacks;
    }

    public BannerCategory getBannerCategory() {
        return bannerCategory;
    }

    public void setBannerCategory(BannerCategory bannerCategory) {
        this.bannerCategory = bannerCategory;
    }
}
