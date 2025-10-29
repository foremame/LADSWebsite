package lads.lads_website.repository;

import lads.lads_website.domain.ActivityRunPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRunPeriodRepository extends JpaRepository<ActivityRunPeriod, Long> {

    @Query("select a from ActivityRunPeriod a where a.banner.id = ?1 and a.startDate <= ?2 and a.endDate >= ?2")
    Optional<ActivityRunPeriod> findByBannerIdAndPullTimestamp(Long bannerId, LocalDate pullTimestamp);

    @Query("SELECT a from ActivityRunPeriod a WHERE ((a.banner is not null AND a.banner.id = ?1) OR (a.event is not NULL AND a.event.id = ?1)) AND a.activityRunType = ?2 ORDER BY a.runNum DESC FETCH FIRST 1 ROWS ONLY")
    Optional<ActivityRunPeriod> findFirstByBannerIdOrEventIdAndActivityRunTypeOrderByRunNumDesc(Long id, String activityRunType);

    List<ActivityRunPeriod> findAllByActivityRunTypeOrderByStartDate(String activityRunType);

    @Query("SELECT a from ActivityRunPeriod a WHERE ((a.banner is not null AND a.banner.id = ?2) OR (a.event is not NULL AND a.event.id = ?2)) AND a.activityRunType = ?1 AND ((a.startDate between ?3 and ?4) OR (a.endDate between ?3 and ?4))")
    List<ActivityRunPeriod> findAllByActivityRunTypeAndActivityIdAndStartOrEndDateBetween(String activityRunType, Long id, LocalDate startDate, LocalDate endDate);
}
