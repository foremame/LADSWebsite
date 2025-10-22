package lads.lads_website.repository;

import lads.lads_website.domain.AscensionCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AscensionCostRepository extends JpaRepository<AscensionCost, Long> {
    // 4 main methods needed to get every type of combination of inclusion for the start & end levels
    // start <= level <= end
    List<AscensionCost> findAllByLevelGreaterThanEqualAndLevelLessThanEqualAndRarityType(int startLevel, int endLevel, String rarity);
    // start < level <= end
    List<AscensionCost> findAllByLevelGreaterThanAndLevelLessThanEqualAndRarityType(int startLevel, int endLevel, String rarity);
    // start <= level < end
    List<AscensionCost> findAllByLevelGreaterThanEqualAndLevelLessThanAndRarityType(int startLevel, int endLevel, String rarity);
    // start < level < end
    List<AscensionCost> findAllByLevelGreaterThanAndLevelLessThanAndRarityType(int startLevel, int endLevel, String rarity);
}
