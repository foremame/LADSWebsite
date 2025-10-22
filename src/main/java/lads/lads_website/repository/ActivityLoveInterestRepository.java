package lads.lads_website.repository;

import lads.lads_website.domain.ActivityLoveInterest;
import lads.lads_website.domain.projections.LoveInterestTypeOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLoveInterestRepository extends JpaRepository<ActivityLoveInterest, Long> {

    List<LoveInterestTypeOnly> findAllDistinctProjectedBy();

    List<LoveInterestTypeOnly> findAllDistinctProjectByEventId(Long id);

    List<LoveInterestTypeOnly> findAllDistinctProjectByBannerId(Long id);
}
