package lads.lads_website.controller;

import lads.lads_website.service.BannerCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class BannerCategoryController {

    private final BannerCategoryService bannerCategoryService;

    @Autowired
    public BannerCategoryController(BannerCategoryService bannerCategoryService) {
        this.bannerCategoryService = bannerCategoryService;
    }

    @RequestMapping(value="/bannerCategory/getSubTypes", method= RequestMethod.POST)
    @ResponseBody
    public List<String> getBannerSubTypesGivenMainType(String mainType) {
        return bannerCategoryService.getAllBannerSubTypesByMainType(mainType);
    }
}
