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

    private CardService cardService;
    private BannerService bannerService;
    private CardOriginService cardOriginService;
    private EventService eventService;


    @Autowired
    public CardController(CardService cardService, BannerService bannerService, CardOriginService cardOriginService, EventService eventService) {
        this.cardService = cardService;
        this.bannerService = bannerService;
        this.cardOriginService = cardOriginService;
        this.eventService = eventService;
    }

    @GetMapping("/card/add")
    public String getCardForm(Model model) {
        model.addAttribute("originType", Arrays.asList(new String[]{"Event", "Banner"}));
        model.addAttribute("rarityTypes", Arrays.asList(new String[]{"3 Star", "4 Star", "5 Star"}));
        model.addAttribute("cardTypes", Arrays.asList(new String[]{"Lunar", "Solar"}));
        model.addAttribute("stellacrumTypes", Arrays.asList(new String[]{"Amber", "Emerald", "Ruby", "Sapphire", "Violet", "Pearl"}));
        model.addAttribute("mainStats", Arrays.asList(new String[]{"Attack", "Defense", "Health"}));
        model.addAttribute("banners", bannerService.getAllBanners());
        model.addAttribute("events", eventService.getAllEvents());
        return "/card/addCard";
    }

    @PostMapping("/card/add")
    public String addNewCard(CardForm cardForm) {

        Card card = new Card();

        CardOrigin cardOrigin;
        if (cardForm.getCardOrigin().equals("Event")) {
            Optional<Event> eventOptional = eventService.getEventById(cardForm.getEventId());
            if (eventOptional.isEmpty()) {
                throw new RuntimeException("Event is not found/set");
            }
            Event event = eventOptional.get();
            Optional<CardOrigin> cardOriginOptional = cardOriginService.getCardOriginByActivityId("Event", event.getId());
            if (cardOriginOptional.isEmpty()) {
                cardOrigin = new CardOrigin();
                cardOrigin.setEvent(event);
                cardOrigin = cardOriginService.addNewCardOrigin(cardOrigin);
            } else {
                cardOrigin = cardOriginOptional.get();
            }
        }
        else {
            Optional<Banner> bannerOptional = bannerService.getBannerById(cardForm.getBannerId());
            if (bannerOptional.isEmpty()) {
                throw new RuntimeException("Banner is not found/set");
            }
            Banner banner = bannerOptional.get();
            Optional<CardOrigin> cardOriginOptional = cardOriginService.getCardOriginByActivityId("Banner", banner.getId());
            if (cardOriginOptional.isEmpty()) {
                cardOrigin = new CardOrigin();
                cardOrigin.setBanner(banner);
                cardOrigin = cardOriginService.addNewCardOrigin(cardOrigin);
            } else {
                cardOrigin = cardOriginOptional.get();
            }
        }

        card.setCardOrigin(cardOrigin);
        card.setName(cardForm.getName());
        card.setCardType(cardForm.getCardType());
        card.setLoveInterestType(cardForm.getLoveInterestType());
        card.setMainStatType(cardForm.getMainStatType());
        card.setRarityType(cardForm.getRarityType());
        card.setStellacrumType(cardForm.getStellacrumType());

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
