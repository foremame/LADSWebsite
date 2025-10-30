package lads.lads_website.controller;

import lads.lads_website.service.ActivityLoveInterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ActivityLoveInterestController {

    private final ActivityLoveInterestService activityLoveInterestService;

    @Autowired
    public ActivityLoveInterestController(ActivityLoveInterestService activityLoveInterestService) {
        this.activityLoveInterestService = activityLoveInterestService;
    }

    @RequestMapping(value="/activityLoveInterest/getLoveInterestList", method= RequestMethod.GET)
    @ResponseBody
    public List<String> getLoveInterestList() {
        return activityLoveInterestService.getAllLoveInterests();
    }

    @RequestMapping(value="/activityLoveInterest/getLoveInterestListByActivity", method= RequestMethod.GET)
    @ResponseBody
    public List<String> getLoveInterestListByActivity(String activityType, Long activityId) {
        return activityLoveInterestService.getLoveInterestsByActivity(activityType, activityId);
    }
}
