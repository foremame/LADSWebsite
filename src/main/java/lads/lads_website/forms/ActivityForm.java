package lads.lads_website.forms;

import lads.lads_website.forms.subforms.ActivitySubForm;

import java.util.List;

public class ActivityForm {
    private String name;
    private String mainType;
    private String subType;
    private String startDate;
    private String endDate;
    private List<ActivitySubForm> activitySubForms;

    private Long eventId;
    private Long bannerId;
    private String activityType;

    public ActivityForm() {
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<ActivitySubForm> getActivitySubForms() {
        return activitySubForms;
    }

    public void setActivitySubForms(List<ActivitySubForm> activitySubForms) {
        this.activitySubForms = activitySubForms;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getBannerId() {
        return bannerId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }
}
