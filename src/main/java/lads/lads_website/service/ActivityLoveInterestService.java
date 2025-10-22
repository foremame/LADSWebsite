package lads.lads_website.service;

import lads.lads_website.domain.ActivityLoveInterest;
import lads.lads_website.domain.projections.LoveInterestTypeOnly;
import lads.lads_website.repository.ActivityLoveInterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityLoveInterestService {

    private ActivityLoveInterestRepository activityLoveInterestRepository;

    @Autowired
    public ActivityLoveInterestService(ActivityLoveInterestRepository activityLoveInterestRepository) {
        this.activityLoveInterestRepository = activityLoveInterestRepository;
    }

    public void addNewActivityLoveInterest(ActivityLoveInterest activityLoveInterest) {
        activityLoveInterestRepository.save(activityLoveInterest);
    }

    public List<String> getAllLoveInterests() {
        List<LoveInterestTypeOnly> lito = activityLoveInterestRepository.findAllDistinctProjectedBy();
        List<String> loveInterests = new ArrayList<>();
        lito.forEach(li -> loveInterests.add(li.getLoveInterestType()));
        return loveInterests;
    }

    public List<String> getLoveInterestsByActivity(String activityType, Long id) {
        List<LoveInterestTypeOnly> lito;
        if (activityType.equals("Event")) {
            lito = activityLoveInterestRepository.findAllDistinctProjectByEventId(id);
        } else {
            lito = activityLoveInterestRepository.findAllDistinctProjectByBannerId(id);
        }
        List<String> loveInterests = new ArrayList<>();
        lito.forEach(li -> loveInterests.add(li.getLoveInterestType()));
        return loveInterests;
    }
}
