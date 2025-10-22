package lads.lads_website.controller;

import lads.lads_website.domain.BountyReward;
import lads.lads_website.domain.Player;
import lads.lads_website.domain.PlayerBounty;
import lads.lads_website.forms.PlayerRegistrationForm;
import lads.lads_website.service.BountyRewardService;
import lads.lads_website.service.PlayerBountyService;
import lads.lads_website.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class PlayerController {

    private PlayerService playerService;
    private BountyRewardService bountyRewardService;
    private PlayerBountyService playerBountyService;

    private UserDetailsService userDetailsService;

    @Autowired
    public PlayerController(PlayerService playerService, BountyRewardService bountyRewardService, PlayerBountyService playerBountyService, UserDetailsService userDetailsService) {
        this.playerService = playerService;
        this.bountyRewardService = bountyRewardService;
        this.playerBountyService = playerBountyService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/home")
    public String loadHomePage(Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        return "/home";
    }

    @GetMapping("/")
    public RedirectView directToHomePage(Principal principal, RedirectAttributes redirectAttributes) {
        RedirectView rv = new RedirectView("/home");
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        if (userDetails.getUsername() == null) {
            return new RedirectView("/login");
        }
        rv.setExposeModelAttributes(false);
        redirectAttributes.addFlashAttribute("user", userDetails);
        return rv;
    }

    @GetMapping("/register")
    public String loadRegisterPage(Model model) {
        return "/player/register";
    }

    @PostMapping("/player/register")
    public String registerPlayer(Model model, PlayerRegistrationForm playerRegistrationForm) {
        Optional<Player> player = playerService.findByUsername(playerRegistrationForm.getUsername());
        if (player.isPresent()) {
            model.addAttribute("Userexist", player);
            return "/register";
        }
        Player playerToSave = new Player();
        playerToSave.setUsername(playerRegistrationForm.getUsername());
        playerToSave.setPassword(playerRegistrationForm.getPassword());
        playerToSave.setEmail(playerRegistrationForm.getEmail());
        playerToSave.setPermissionLevel("user");
        Player returned = playerService.save(playerToSave);
        addDefaultBountyLevels(returned);
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String loadLoginPage(Model model) {
        return "/player/login";
    }

    // Default max bounty level for all bounties to 9. User can change this later as needed.
    private void addDefaultBountyLevels(Player player) {
        List<BountyReward> bountyRewards = bountyRewardService.getAllBountyRewardsByLevel(9);
        bountyRewards.forEach(bountyReward -> {
            PlayerBounty playerBounty = new PlayerBounty();
            playerBounty.setPlayer(player);
            playerBounty.setBountyReward(bountyReward);
            playerBountyService.save(playerBounty);
        });
    }
}
