package lads.lads_website.service;

import lads.lads_website.domain.BannerCategory;
import lads.lads_website.domain.projections.MainTypeOnly;
import lads.lads_website.domain.projections.SubTypeOnly;
import lads.lads_website.repository.BannerCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BannerCategoryService {

    private BannerCategoryRepository bannerCategoryRepository;

    @Autowired
    public BannerCategoryService(BannerCategoryRepository bannerCategoryRepository) {
        this.bannerCategoryRepository = bannerCategoryRepository;
    }

    public List<String> getAllBannerMainCategories() {
        List<MainTypeOnly> mto = bannerCategoryRepository.findAllDistinctProjectedBy();
        List<String> categories = new ArrayList<>();
        mto.forEach(MainTypeOnly -> categories.add(MainTypeOnly.getBannerMainType()));
        return categories;
    }

    public List<String> getAllBannerSubTypesByMainType(String bannerMainType) {
        List<SubTypeOnly> sto = bannerCategoryRepository.findDistinctByBannerMainType(bannerMainType);
        List<String> categories = new ArrayList<>();
        sto.forEach(c -> categories.add(c.getBannerSubType()));
        return categories;
    }

    public Optional<BannerCategory> getBannerCategoryByMainTypeAndSubType(String bannerMainType, String bannerSubType) {
        return bannerCategoryRepository.findByBannerMainTypeAndBannerSubType(bannerMainType, bannerSubType);
    }
}
