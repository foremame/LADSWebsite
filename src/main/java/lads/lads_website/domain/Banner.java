package lads.lads_website.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="banner_category_id")
    private BannerCategory bannerCategory;

    @OneToMany(mappedBy = "banner", fetch = FetchType.LAZY)
    private List<CardOrigin> cardOrigins = new ArrayList<>();

    @OneToMany(mappedBy = "banner", fetch = FetchType.LAZY)
    private List<ActivityLoveInterest> activityLoveInterests = new ArrayList<>();

    @OneToMany(mappedBy = "banner", fetch = FetchType.LAZY)
    private List<ActivityRunPeriod> activityRunPeriods = new ArrayList<>();

    public Banner() {
    }

    public Banner(Long id, String name, BannerCategory bannerCategory, List<CardOrigin> cardOrigins, List<ActivityLoveInterest> activityLoveInterests, List<ActivityRunPeriod> activityRunPeriods) {
        this.id = id;
        this.name = name;
        this.bannerCategory = bannerCategory;
        this.cardOrigins = cardOrigins;
        this.activityLoveInterests = activityLoveInterests;
        this.activityRunPeriods = activityRunPeriods;
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

    public BannerCategory getBannerCategory() {
        return bannerCategory;
    }

    public void setBannerCategory(BannerCategory bannerCategory) {
        this.bannerCategory = bannerCategory;
    }

    public List<CardOrigin> getCardOrigins() {
        return cardOrigins;
    }

    public void setCardOrigins(List<CardOrigin> cardOrigins) {
        this.cardOrigins = cardOrigins;
    }

    public List<ActivityLoveInterest> getActivityLoveInterests() {
        return activityLoveInterests;
    }

    public void setActivityLoveInterests(List<ActivityLoveInterest> activityLoveInterests) {
        this.activityLoveInterests = activityLoveInterests;
    }

    public List<ActivityRunPeriod> getActivityRunPeriods() {
        return activityRunPeriods;
    }

    public void setActivityRunPeriods(List<ActivityRunPeriod> activityRunPeriods) {
        this.activityRunPeriods = activityRunPeriods;
    }
}
