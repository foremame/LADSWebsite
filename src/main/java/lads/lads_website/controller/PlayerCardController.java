package lads.lads_website.controller;

import lads.lads_website.domain.Card;
import lads.lads_website.domain.Player;
import lads.lads_website.domain.PlayerCard;
import lads.lads_website.forms.PlayerCardForm;
import lads.lads_website.service.CardService;
import lads.lads_website.service.PlayerCardService;
import lads.lads_website.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
public class PlayerCardController {

    private final PlayerCardService playerCardService;
    private final CardService cardService;
    private final PlayerService playerService;

    @Autowired
    public PlayerCardController(PlayerCardService playerCardService, CardService cardService, PlayerService playerService) {
        this.playerCardService = playerCardService;
        this.cardService = cardService;
        this.playerService = playerService;
    }

    @GetMapping("playerCard/add")
    public String getPlayerCardForm(Model model) {
        List<Card> cardList = cardService.getAllCards();
        model.addAttribute("cards", cardList);
        model.addAttribute("rankTypes", new String[]{"R0", "R1", "R2", "R3"});

        return "/playerCard/addPlayerCard";
    }

    @GetMapping("playerCard/update")
    public String getPlayerCardUpdateForm(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information"));
        model.addAttribute("playerCards", playerCardService.getAllPlayerCardsForGivenPlayerId(player.getId()));
        model.addAttribute("rankTypes", new String[]{"R0", "R1", "R2", "R3"});

        return "/playerCard/updatePlayerCard";
    }

    @PostMapping("playerCard/add")
    public String addNewPlayerCard(PlayerCardForm playerCardForm, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        Card card = cardService.getCardById(playerCardForm.getCardId()).orElseThrow(()->new RuntimeException("Cannot find card information"));

        PlayerCard playerCard = new PlayerCard();

        Optional<PlayerCard> playerCardOptional = playerCardService.getPlayerCardByPlayerAndCardId(player.getId(), card.getId());
        if (playerCardOptional.isPresent()) {
            throw new RuntimeException("Card " + card.getName() + " is already owned by user");
        }
        if (playerCardForm.getRankType().isEmpty() || playerCardForm.getLevel() < 1 || playerCardForm.getLevel() > 80) {
            throw new RuntimeException("Form input error. Something is wrong with the card data submitted");
        }

        playerCard.setCard(card);
        playerCard.setPlayer(player);
        playerCard.setRankType(playerCardForm.getRankType());
        playerCard.setLevel(playerCardForm.getLevel());
        if (playerCardForm.getAwakened() == null || playerCard.getLevel() < 80) { playerCard.setAwakened(false); }
        else { playerCard.setAwakened(playerCardForm.getAwakened()); }

        playerCardService.addNewPlayerCard(playerCard);
        return "redirect:/playerCard/list";
    }

    @PostMapping("playerCard/update")
    public String updatePlayerCard(PlayerCardForm playerCardForm, Principal principal) {
        Optional<PlayerCard> playerCardOptional = playerCardService.getPlayerCardById(playerCardForm.getPlayerCardId());
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        PlayerCard playerCard = playerCardService.getPlayerCardById(playerCardForm.getPlayerCardId())
                .orElseThrow(()->new RuntimeException("Cannot find existing player card entry for the selected card, update failed"));

        if (!playerCard.getPlayer().getId().equals(player.getId())) {
            throw new RuntimeException("Cannot update player card entry for another player.");
        }

        if (playerCardForm.getRankType().isEmpty() || playerCardForm.getLevel() < 1 || playerCardForm.getLevel() > 80) {
            throw new RuntimeException("Form input error. Something is wrong with the card data submitted");
        }

        playerCard.setAwakened(playerCardForm.getAwakened() != null && playerCard.getLevel() >= 80 && playerCardForm.getAwakened());
        playerCard.setLevel(playerCardForm.getLevel());
        playerCard.setRankType(playerCardForm.getRankType());

        playerCardService.updatePlayerCard(playerCard);

        return "redirect:/home";
    }

    @GetMapping("/playerCard/list")
    public String getPlayerCardListView(Model model, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        List<PlayerCard> playerCards = playerCardService.getAllPlayerCardsForGivenPlayerId(player.getId());

        model.addAttribute("playerCards", playerCards);
        return "/playerCard/listPlayerCard";
    }

    @RequestMapping(value="/playerCard/getPlayerCardInfoById", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getPlayerCardInfoById(Long playerCardId) {
        Optional<PlayerCard> playerCardOptional = playerCardService.getPlayerCardById(playerCardId);
        if (playerCardOptional.isEmpty()) {
            throw new RuntimeException("Cannot find player card given id");
        }
        PlayerCard playerCard = playerCardOptional.get();
        Card card = playerCard.getCard();
        // Cannot pass full object to javascript due to it exceeding max depth requirements (thanks to hibernate class linking)
        // Instead, pass map of the necessary values back
        Map<String, String> values = new HashMap<>();
        values.put("rankType", playerCard.getRankType());
        values.put("level", playerCard.getLevel().toString());
        values.put("awakened", playerCard.getAwakened().toString());
        values.put("stellacrum", card.getStellacrumType());
        values.put("rarity", card.getRarityType());
        values.put("name", card.getName());

        return values;
    }

    @RequestMapping(value="/playerCard/getPlayerCardInfoByCardId", method= RequestMethod.GET)
    @ResponseBody
    public Long getPlayerCardInfoByCardId(Long cardId, Principal principal) {
        Player player = playerService.findByUsername(principal.getName()).orElseThrow(()->new RuntimeException("Cannot find player information."));
        Optional<PlayerCard> playerCardOptional = playerCardService.getPlayerCardByPlayerAndCardId(player.getId(), cardId);

        return playerCardOptional.isPresent() ? playerCardOptional.get().getId() : -1;
    }

    @RequestMapping(value="/playerCard/validatePlayerCard", method= RequestMethod.GET)
    @ResponseBody
    public List<String> validatePlayerCard(Long cardId, Principal principal) {
        List<String> validationResults = new ArrayList<>();
        Long playerCardId = getPlayerCardInfoByCardId(cardId, principal);
        if (playerCardId != -1) {
            validationResults.add("You already own this card. To update owned card info, use the relevant update form instead.");
        }

        return validationResults;
    }
}
