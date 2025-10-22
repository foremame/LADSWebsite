package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table
public class ActivityLoveInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String activityRunType;

    private String loveInterestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="banner_id")
    private Banner banner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    public ActivityLoveInterest() {
    }

    public ActivityLoveInterest(Long id, String activityRunType, String loveInterestType, Banner banner, Event event) {
        this.id = id;
        this.activityRunType = activityRunType;
        this.loveInterestType = loveInterestType;
        this.banner = banner;
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityRunType() {
        return activityRunType;
    }

    public void setActivityRunType(String activityRunType) {
        this.activityRunType = activityRunType;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getLoveInterestType() {
        return loveInterestType;
    }

    public void setLoveInterestType(String loveInterestType) {
        this.loveInterestType = loveInterestType;
    }
}
