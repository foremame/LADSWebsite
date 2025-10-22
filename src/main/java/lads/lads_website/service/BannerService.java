package lads.lads_website.service;

import lads.lads_website.domain.Banner;
import lads.lads_website.repository.BannerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BannerService {

    private BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public Optional<Banner> getBannerById(Long bannerId) {
        return bannerRepository.findById(bannerId);
    }

    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    public Banner addNewBanner(Banner banner) {
        validateBanner(banner);
        return bannerRepository.save(banner);
    }

    public Optional<Banner> getBannerByName(String name) {
        return bannerRepository.findByName(name);
    }

    private void validateBanner(Banner banner) {
        Optional<Banner> bannerOptional = bannerRepository.findByName(banner.getName());
        if (bannerOptional.isPresent()) {
            throw new RuntimeException("The name " + banner.getName() + " is already taken.");
        }
    }
}
