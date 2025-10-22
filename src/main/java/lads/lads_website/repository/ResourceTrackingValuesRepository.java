package lads.lads_website.repository;

import lads.lads_website.domain.ResourceTrackingValues;
import lads.lads_website.domain.projections.ResourceTypeOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceTrackingValuesRepository extends JpaRepository<ResourceTrackingValues, Long> {

    List<ResourceTypeOnly> findAllDistinctProjectedBy();

}
