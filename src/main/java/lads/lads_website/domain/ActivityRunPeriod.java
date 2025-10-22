package lads.lads_website.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class ActivityRunPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activityRunType;

    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    private int runNum;

    @Column(name="is_rerun", columnDefinition="BIT")
    private boolean rerun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="banner_id")
    private Banner banner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    @OneToMany(mappedBy = "activityRunPeriod", fetch = FetchType.LAZY)
    private List<CardPull> cardPulls = new ArrayList<>();

    public ActivityRunPeriod() {
    }

    public ActivityRunPeriod(Long id, String activityRunType, LocalDate startDate, LocalDate endDate, int runNum, boolean rerun, Banner banner, Event event, List<CardPull> cardPulls) {
        this.id = id;
        this.activityRunType = activityRunType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runNum = runNum;
        this.rerun = rerun;
        this.banner = banner;
        this.event = event;
        this.cardPulls = cardPulls;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getRunNum() {
        return runNum;
    }

    public boolean getRerun() {
        return rerun;
    }

    public void setRerun(boolean rerun) {
        this.rerun = rerun;
    }

    public void setRunNum(int runNum) {
        this.runNum = runNum;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<CardPull> getCardPulls() {
        return cardPulls;
    }

    public void setCardPulls(List<CardPull> cardPulls) {
        this.cardPulls = cardPulls;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }
}
