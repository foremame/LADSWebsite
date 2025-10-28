package lads.lads_website.controller;

import lads.lads_website.domain.ActivityLoveInterest;
import lads.lads_website.domain.ActivityRunPeriod;
import lads.lads_website.domain.Banner;
import lads.lads_website.domain.BannerCategory;
import lads.lads_website.forms.ActivityForm;
import lads.lads_website.forms.subforms.ActivitySubForm;
import lads.lads_website.service.ActivityLoveInterestService;
import lads.lads_website.service.ActivityRunPeriodService;
import lads.lads_website.service.BannerCategoryService;
import lads.lads_website.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class BannerController {

    private BannerService bannerService;
    private BannerCategoryService bannerCategoryService;
    private ActivityRunPeriodService activityRunPeriodService;
    private ActivityLoveInterestService activityLoveInterestService;

    @Autowired
    public BannerController(BannerService bannerService, BannerCategoryService bannerCategoryService, ActivityRunPeriodService activityRunPeriodService, ActivityLoveInterestService activityLoveInterestService) {
        this.bannerService = bannerService;
        this.bannerCategoryService = bannerCategoryService;
        this.activityRunPeriodService = activityRunPeriodService;
        this.activityLoveInterestService = activityLoveInterestService;
    }

    @GetMapping("/banner/add")
    public String prepareBannerAddForm(Model model) {
        model.addAttribute("mainCategories", bannerCategoryService.getAllBannerMainCategories());
        return "/banner/add";
    }

    @PostMapping("/banner/add")
    public String addNewBanner(ActivityForm bannerForm) {
        Banner banner = new Banner();
        banner.setName(bannerForm.getName());
        BannerCategory bc = bannerCategoryService.getBannerCategoryByMainTypeAndSubType(bannerForm.getMainType(), bannerForm.getSubType()).orElseThrow(()-> new RuntimeException("Banner Category not found"));
        banner.setBannerCategory(bc);
        if (bannerForm.getActivitySubForms() == null) { throw new RuntimeException("No love interests selected for banner."); }
        banner = bannerService.addNewBanner(banner);
        ActivityRunPeriod arp = new ActivityRunPeriod();
        arp.setBanner(banner);
        arp.setActivityRunType("Banner");
        arp.setRunNum(0);
        arp.setRerun(false);

        try {
            arp.setStartDate(LocalDate.parse(bannerForm.getStartDate()));
            arp.setEndDate(LocalDate.parse(bannerForm.getEndDate()));
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Parse failed for start/end date: " + e.getMessage());
        }

        activityRunPeriodService.addNewActivityRunPeriod(arp);

        for (ActivitySubForm loveInterestInfo : bannerForm.getActivitySubForms()) {
            if (loveInterestInfo.getLoveInterest() != null) {
                ActivityLoveInterest ali = new ActivityLoveInterest();
                ali.setBanner(banner);
                ali.setActivityRunType("Banner");
                ali.setLoveInterestType(loveInterestInfo.getLoveInterest());
                activityLoveInterestService.addNewActivityLoveInterest(ali);
            }
        }

        return "redirect:/home";
    }

    @RequestMapping(value="/banner/isMulti", method= RequestMethod.POST)
    @ResponseBody
    public boolean isBannerAMultiBannerGivenBannerId(Long id) {
        Banner banner = bannerService.getBannerById(id).get();
        return banner.getBannerCategory().getBannerSubType().equals("Multi");
    }

    @RequestMapping(value="/banner/getAll", method= RequestMethod.GET)
    @ResponseBody
    public List<Banner> getAllBanners() {
        return bannerService.getAllBanners();
    }

    @RequestMapping(value="/banner/validateBannerForm", method= RequestMethod.GET)
    @ResponseBody
    public List<String> validateBannerForm(String bannerName, String startDate, String endDate) {
        List<String> validationResults = new ArrayList<>();

        if (bannerService.getBannerByName(bannerName).isPresent()) {
            validationResults.add("Banner " + bannerName + " already exists.");
        }

        try {
            LocalDate startDateParsed = LocalDate.parse(startDate);
        } catch (DateTimeParseException e) {
            validationResults.add("Start date provided: " + startDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        try {
            LocalDate endDateParsed = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            validationResults.add("End date provided: " + endDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        return validationResults;
    }
}
