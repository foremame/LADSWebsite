package lads.lads_website.service;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.domain.projections.BountyNameTypeOnly;
import lads.lads_website.repository.BountyRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BountyRewardService {

    private final BountyRewardRepository bountyRewardRepository;

    @Autowired
    public BountyRewardService(BountyRewardRepository bountyRewardRepository) {
        this.bountyRewardRepository = bountyRewardRepository;
    }

    public List<BountyReward> getAllBountyRewardsByLevel(int level) {
        return bountyRewardRepository.findAllByLevel(level);
    }

    public Optional<BountyReward> getBountyRewardByNameAndLevel(String name, int level) {
        return bountyRewardRepository.findByBountyNameTypeAndLevel(name, level);
    }

    public List<String> getAllBountyNames() {
        List<BountyNameTypeOnly> bountyNameTypeOnlyList = bountyRewardRepository.findAllDistinctProjectedBy();
        List<String> names = new ArrayList<>();
        bountyNameTypeOnlyList.forEach(bn -> names.add(bn.getBountyNameType()));
        return names;
    }
}
