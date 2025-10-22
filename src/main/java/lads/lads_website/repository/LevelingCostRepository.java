package lads.lads_website.repository;

import lads.lads_website.domain.LevelingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelingCostRepository extends JpaRepository<LevelingCost, Long> {

    List<LevelingCost> findAllByLevelGreaterThanAndLevelLessThanEqualAndRarityType(int startLevel, int endLevel, String rarityType);
}
