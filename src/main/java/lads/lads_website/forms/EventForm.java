package lads.lads_website.forms;

import lads.lads_website.forms.subforms.ActivitySubForm;

import java.util.List;

public class EventForm {

    private String name;

    private String eventType;

    private String startDate;

    private String endDate;

    private List<ActivitySubForm> activitySubForms;

    public EventForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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
}
