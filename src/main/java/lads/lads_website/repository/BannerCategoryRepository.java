package lads.lads_website.repository;

import lads.lads_website.domain.BannerCategory;
import lads.lads_website.domain.projections.MainTypeOnly;
import lads.lads_website.domain.projections.SubTypeOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerCategoryRepository extends JpaRepository<BannerCategory, Long> {

    List<MainTypeOnly> findAllDistinctProjectedBy();

    List<SubTypeOnly> findDistinctByBannerMainType(String bannerMainType);

    Optional<BannerCategory> findByBannerMainTypeAndBannerSubType(String bannerMainType, String bannerSubType);
}
