package lads.lads_website.repository;

import lads.lads_website.domain.Card;
import lads.lads_website.domain.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByCardOriginBannerBannerCategoryBannerMainTypeOrderById(String bannerMainType);

    List<LoveInterestTypeOnly> findAllDistinctProjectedBy();

    Optional<Card> findByName(String cardName);

}
