package lads.lads_website.service;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.domain.PlayerBounty;
import lads.lads_website.repository.PlayerBountyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerBountyService {

    private PlayerBountyRepository playerBountyRepository;

    @Autowired
    public PlayerBountyService(PlayerBountyRepository playerBountyRepository) {
        this.playerBountyRepository = playerBountyRepository;
    }

    public List<BountyReward> getAllBountyRewardsForPlayer(Long playerId) {
        List<BountyReward> bountyRewards = new ArrayList<>();
        List<PlayerBounty> playerBounties = playerBountyRepository.findAllByPlayerId(playerId);
        playerBounties.forEach(pb -> bountyRewards.add(pb.getBountyReward()));
        return bountyRewards;
    }

    public PlayerBounty getPlayerBountyByBountyName(String bountyName, Long playerId) {
        return playerBountyRepository.findByBountyRewardBountyNameTypeAndPlayerId(bountyName, playerId).get();
    }

    public void save(PlayerBounty playerBounty) {
        playerBountyRepository.save(playerBounty);
    }
}
