package lads.lads_website.controller;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.domain.Player;
import lads.lads_website.domain.PlayerBounty;
import lads.lads_website.forms.UpdateBountyLevelForm;
import lads.lads_website.service.BountyRewardService;
import lads.lads_website.service.PlayerBountyService;
import lads.lads_website.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class PlayerBountyController {

    private final PlayerBountyService playerBountyService;
    private final PlayerService playerService;
    private final BountyRewardService bountyRewardService;

    @Autowired
    public PlayerBountyController(PlayerBountyService playerBountyService, PlayerService playerService, BountyRewardService bountyRewardService) {
        this.playerBountyService = playerBountyService;
        this.playerService = playerService;
        this.bountyRewardService = bountyRewardService;
    }

    @GetMapping("/playerBounty/update")
    public String getUpdateBountyLevelPage(Model model) {
        List<String> bountyNames = bountyRewardService.getAllBountyNames();

        model.addAttribute("bountyNames", bountyNames);
        return "/playerBounty/changeBountyLevel";
    }

    @PostMapping("/playerBounty/update")
    public String updatePlayerBountyLevel(UpdateBountyLevelForm updateBountyLevelForm, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).get();
        String bountyName = updateBountyLevelForm.getBountyName();
        PlayerBounty playerBounty = playerBountyService.getPlayerBountyByBountyName(bountyName, player.getId());

        BountyReward bountyReward = bountyRewardService.getBountyRewardByNameAndLevel(bountyName, updateBountyLevelForm.getLevel()).get();
        playerBounty.setBountyReward(bountyReward);
        playerBountyService.save(playerBounty);
        return "redirect:/home";
    }
}
