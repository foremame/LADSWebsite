package lads.lads_website.repository;

import lads.lads_website.domain.BountyReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BountyRewardRepository extends JpaRepository<BountyReward, Long> {
    List<BountyReward> findAllByLevel(int level);
}
