package lads.lads_website.service;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.repository.BountyRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BountyRewardService {

    private BountyRewardRepository bountyRewardRepository;

    @Autowired
    public BountyRewardService(BountyRewardRepository bountyRewardRepository) {
        this.bountyRewardRepository = bountyRewardRepository;
    }

    public List<BountyReward> getAllBountyRewardsByLevel(int level) {
        return bountyRewardRepository.findAllByLevel(level);
    }
}
