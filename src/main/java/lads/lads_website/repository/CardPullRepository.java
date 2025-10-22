package lads.lads_website.repository;

import lads.lads_website.domain.CardPull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CardPullRepository extends JpaRepository<CardPull, Long> {

    // I could refactor these into jpa method name queries, but the names will be way too long.
    @Query("SELECT c FROM CardPull c WHERE c.playerCard.card.rarityType IN ?4 AND c.playerCard.player.id = ?3 AND c.activityRunPeriod.rerun = ?2 AND c.activityRunPeriod.banner.bannerCategory.bannerMainType = ?1 ORDER BY c.id DESC, c.pullTimestamp DESC FETCH FIRST 1 ROWS ONLY")
    Optional<CardPull> findTopByBannerTypeAndPlayerIdAndCardRarity(String bannerMainType, boolean rerun, Long playerId, List<String> cardRarityList);

    @Query("SELECT c FROM CardPull c WHERE c.id > ?1 AND c.activityRunPeriod.rerun = ?3 AND c.playerCard.player.id = ?4 AND c.activityRunPeriod.banner.bannerCategory.bannerMainType = ?2 ORDER BY c.id DESC, c.pullTimestamp DESC")
    List<CardPull> getAllSinceIdByBannerTypeAndPlayerId(Long id, String bannerMainType, boolean rerun, Long playerId);

    @Query("SELECT c FROM CardPull c WHERE c.activityRunPeriod.rerun = ?2 AND c.playerCard.player.id = ?3 AND c.activityRunPeriod.banner.bannerCategory.bannerMainType = ?1 ORDER BY c.id DESC, c.pullTimestamp DESC")
    List<CardPull> getAllByBannerTypeAndPlayerId(String bannerMainType, boolean rerun, Long playerId);

    @Query("SELECT c FROM CardPull c WHERE c.activityRunPeriod.rerun = ?2 AND c.playerCard.player.id = ?3 AND c.activityRunPeriod.banner.bannerCategory.bannerMainType = ?1 ORDER BY c.id DESC, c.pullTimestamp DESC FETCH FIRST 1 ROWS ONLY")
    Optional<CardPull> findTopByBannerTypeAndPlayerId(String bannerMainType, boolean rerun, Long playerId);

    List<CardPull> findAllByActivityRunPeriodIdAndPlayerCardPlayerId(Long activityRunPeriodId, Long playerId);
}
