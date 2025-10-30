package lads.lads_website.controller;

import lads.lads_website.domain.*;
import lads.lads_website.forms.CardForm;
import lads.lads_website.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class CardController {

    private final CardService cardService;
    private final BannerService bannerService;
    private final CardOriginService cardOriginService;
    private final EventService eventService;


    @Autowired
    public CardController(CardService cardService, BannerService bannerService, CardOriginService cardOriginService, EventService eventService) {
        this.cardService = cardService;
        this.bannerService = bannerService;
        this.cardOriginService = cardOriginService;
        this.eventService = eventService;
    }

    @GetMapping("/card/add")
    public String getCardForm(Model model) {
        model.addAttribute("originType", new String[]{"Event", "Banner"});
        model.addAttribute("rarityTypes", new String[]{"3 Star", "4 Star", "5 Star"});
        model.addAttribute("cardTypes", new String[]{"Lunar", "Solar"});
        model.addAttribute("stellacrumTypes", new String[]{"Amber", "Emerald", "Ruby", "Sapphire", "Violet", "Pearl"});
        model.addAttribute("mainStats", new String[]{"Attack", "Defense", "Health"});
        model.addAttribute("banners", bannerService.getAllBanners());
        model.addAttribute("events", eventService.getAllEvents());
        return "/card/addCard";
    }

    @PostMapping("/card/add")
    public String addNewCard(CardForm cardForm) {
        CardOrigin cardOrigin;
        if (cardForm.getCardOrigin().equals("Event")) {
            Event event = eventService.getEventById(cardForm.getEventId()).orElseThrow(()->new RuntimeException("No event found for id: " + cardForm.getEventId()));
            cardOrigin = cardOriginService.getCardOriginByActivityId("Event", event.getId())
                    .orElse(cardOriginService.addNewCardOrigin(new CardOrigin(event)));
        }
        else {
            Banner banner = bannerService.getBannerById(cardForm.getBannerId()).orElseThrow(()->new RuntimeException("No banner found for id: " + cardForm.getBannerId()));
            cardOrigin = cardOriginService.getCardOriginByActivityId("Banner", banner.getId())
                    .orElse(cardOriginService.addNewCardOrigin(new CardOrigin(banner)));
        }

        Card card = new Card(cardForm.getName(), cardForm.getLoveInterestType(), cardForm.getRarityType(), cardForm.getCardType(),
                cardForm.getStellacrumType(), cardForm.getMainStatType(), cardOrigin);

        cardService.addNewCard(card);
        return "redirect:/home";
    }

    @RequestMapping(value="/card/validateCardForm", method= RequestMethod.GET)
    @ResponseBody
    public List<String> validateCardForm(String cardName) {
        List<String> validationResults = new ArrayList<>();

        Optional<Card> card = cardService.getCardByName(cardName);
        if (card.isPresent()) {
            validationResults.add("Card " + cardName + " already exists.");
        }

        return validationResults;
    }
}
