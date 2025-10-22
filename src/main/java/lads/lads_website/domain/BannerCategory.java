package lads.lads_website.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class BannerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String bannerMainType;

    private String bannerSubType;

    @OneToMany(mappedBy = "bannerCategory", fetch = FetchType.LAZY)
    private List<Banner> banners = new ArrayList<>();

    @OneToMany(mappedBy = "bannerCategory", fetch = FetchType.LAZY)
    private List<PackPrice> packPrices = new ArrayList<>();

    public BannerCategory() {
    }

    public BannerCategory(Long id, String bannerMainType, String bannerSubType, List<Banner> banners, List<PackPrice> packPrices) {
        this.id = id;
        this.bannerMainType = bannerMainType;
        this.bannerSubType = bannerSubType;
        this.banners = banners;
        this.packPrices = packPrices;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBannerMainType() {
        return bannerMainType;
    }

    public void setBannerMainType(String bannerMainType) {
        this.bannerMainType = bannerMainType;
    }

    public String getBannerSubType() {
        return bannerSubType;
    }

    public void setBannerSubType(String bannerSubType) {
        this.bannerSubType = bannerSubType;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<PackPrice> getPackPrices() {
        return packPrices;
    }

    public void setPackPrices(List<PackPrice> packPrices) {
        this.packPrices = packPrices;
    }
}
