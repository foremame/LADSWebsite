package lads.lads_website.repository;

import lads.lads_website.domain.ResourceTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceTrackingRepository extends JpaRepository<ResourceTracking, Long> {

    Optional<ResourceTracking> findFirstByPlayerIdOrderByAddDateDesc(Long playerId);

    List<ResourceTracking> findAllByPlayerIdOrderByAddDateAsc(Long playerId);

    Optional<ResourceTracking> findFirstByAddDateBeforeOrderByAddDateDesc(Timestamp addDate);
}
