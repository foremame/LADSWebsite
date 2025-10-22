package lads.lads_website.forms;

import lads.lads_website.forms.subforms.ActivitySubForm;

import java.util.ArrayList;
import java.util.List;

public class BannerForm {
    private String name;
    private String mainType;
    private String subType;
    private String startDate;
    private String endDate;
    private List<ActivitySubForm> activitySubForms;

    public BannerForm() {
        activitySubForms = new ArrayList<>();
    }

    public List<ActivitySubForm> getActivitySubForms() {
        return activitySubForms;
    }

    public void setActivitySubForms(List<ActivitySubForm> activitySubForms) {
        this.activitySubForms = activitySubForms;
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
}
