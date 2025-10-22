package lads.lads_website.forms.list_wrappers;

import lads.lads_website.domain.ResourceTracking;

public class ResourceTrackingList {
    private ResourceTracking resourceTracking;
    private Integer totalWishes;
    private Integer wishesGainedSinceLastEntry;
    private Integer wishesGainedOverTime;
    private Integer averageWishesGainedPerDay;

    public ResourceTrackingList() {
    }

    public Integer getWishesGainedSinceLastEntry() {
        return wishesGainedSinceLastEntry;
    }

    public void setWishesGainedSinceLastEntry(Integer wishesGainedSinceLastEntry) {
        this.wishesGainedSinceLastEntry = wishesGainedSinceLastEntry;
    }

    public ResourceTracking getResourceTracking() {
        return resourceTracking;
    }

    public void setResourceTracking(ResourceTracking resourceTracking) {
        this.resourceTracking = resourceTracking;
    }

    public Integer getTotalWishes() {
        return totalWishes;
    }

    public void setTotalWishes(Integer totalWishes) {
        this.totalWishes = totalWishes;
    }

    public Integer getWishesGainedOverTime() {
        return wishesGainedOverTime;
    }

    public void setWishesGainedOverTime(Integer wishesGainedOverTime) {
        this.wishesGainedOverTime = wishesGainedOverTime;
    }

    public Integer getAverageWishesGainedPerDay() {
        return averageWishesGainedPerDay;
    }

    public void setAverageWishesGainedPerDay(Integer averageWishesGainedPerDay) {
        this.averageWishesGainedPerDay = averageWishesGainedPerDay;
    }
}
