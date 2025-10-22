package lads.lads_website.repository;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.domain.projections.BountyNameTypeOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BountyRewardRepository extends JpaRepository<BountyReward, Long> {
    List<BountyReward> findAllByLevel(int level);
    List<BountyNameTypeOnly> findAllDistinctProjectedBy();
    Optional<BountyReward> findByBountyNameTypeAndLevel(String name, int level);
}
