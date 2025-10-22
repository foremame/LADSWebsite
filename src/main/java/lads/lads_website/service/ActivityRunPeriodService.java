package lads.lads_website.service;

import lads.lads_website.domain.ActivityRunPeriod;
import lads.lads_website.repository.ActivityRunPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityRunPeriodService {

    private ActivityRunPeriodRepository activityRunPeriodRepository;

    @Autowired
    public ActivityRunPeriodService(ActivityRunPeriodRepository activityRunPeriodRepository) {
        this.activityRunPeriodRepository = activityRunPeriodRepository;
    }

    public Optional<ActivityRunPeriod> getMostRecentActivityRunPeriodForActivity(String activityType, Long id) {
        return activityRunPeriodRepository.findFirstByBannerIdOrEventIdAndActivityRunTypeOrderByRunNumDesc(id, activityType);
    }

    public Optional<ActivityRunPeriod> getActivityRunPeriodById(Long id) {
        return activityRunPeriodRepository.findById(id);
    }

    public Optional<ActivityRunPeriod> getActivityRunPeriodByBannerIdAndTimestamp(Long bannerId, LocalDateTime pullTimestamp) {
        return activityRunPeriodRepository.findByBannerIdAndPullTimestamp(bannerId, pullTimestamp);
    }

    public void addNewActivityRunPeriod(ActivityRunPeriod activityRunPeriod) {
        activityRunPeriodRepository.save(activityRunPeriod);
    }

    public List<ActivityRunPeriod> getAllBannerRuns() {
        return activityRunPeriodRepository.findAllByActivityRunTypeOrderByStartDate("Banner");
    }

    public List<ActivityRunPeriod> getAllEventRuns() {
        return activityRunPeriodRepository.findAllByActivityRunTypeOrderByStartDate("Event");
    }

    public List<ActivityRunPeriod> getAllRunsBetweenGivenStartAndDateForActivity(String activityType, Long activityId, LocalDate startDate, LocalDate endDate) {
        return activityRunPeriodRepository.findAllByActivityRunTypeAndActivityIdAndStartOrEndDateBetween(activityType, activityId, startDate, endDate);
    }
}
