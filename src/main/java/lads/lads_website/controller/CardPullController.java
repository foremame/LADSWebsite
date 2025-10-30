package lads.lads_website.controller;

import lads.lads_website.domain.*;
import lads.lads_website.forms.CardPullForm;
import lads.lads_website.forms.subforms.CardPullSubForm;
import lads.lads_website.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class CardPullController {

    private final CardPullService cardPullService;
    private final CardService cardService;
    private final PlayerCardService playerCardService;
    private final ActivityRunPeriodService activityRunPeriodService;
    private final BannerService bannerService;
    private final PlayerService playerService;

    @Autowired
    public CardPullController(CardPullService cardPullService, CardService cardService, PlayerCardService playerCardService, ActivityRunPeriodService activityRunPeriodService, BannerService bannerService, PlayerService playerService) {
        this.cardPullService = cardPullService;
        this.cardService = cardService;
        this.playerCardService = playerCardService;
        this.activityRunPeriodService = activityRunPeriodService;
        this.bannerService = bannerService;
        this.playerService = playerService;
    }

    @ExceptionHandler(RuntimeException.class)
    public RedirectView handleNewBannerInfoException(Exception ex, RedirectAttributes redirectAttributes) {
        RedirectView redirectView = new RedirectView("/cardPull/add");
        redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        redirectView.setExposeModelAttributes(false);
        return redirectView;
    }

    @RequestMapping(value="/cardPull/getLimitedCardIds", method=RequestMethod.POST)
    @ResponseBody
    public List<Long> getCardIdsByBannerId(Long bannerId) {
        List<Long> ids = new ArrayList<>();
        Banner banner = bannerService.getBannerById(bannerId).orElseThrow(() -> new RuntimeException("Cannot find banner for id " + bannerId));
        if (banner.getBannerCategory().getBannerMainType().equals("Limited")) {
            List<CardOrigin> cardOrigins = banner.getCardOrigins();
            for (CardOrigin cardOrigin : cardOrigins) {
                List<Card> cards = cardOrigin.getCards();
                for (Card card : cards) {
                    ids.add(card.getId());
                }
            }
        }
        return ids;
    }

    @GetMapping("/cardPull/add")
    public String routeToCardPullForm(Model model, Principal principal) {
        Player usr = playerService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("Player information not found"));

        List<Card> standardCards = cardService.getAllCardsByBannerType("Standard");
        List<Card> limitedCards = cardService.getAllCardsByBannerType("Limited");

        model.addAttribute("banners", bannerService.getAllBanners());
        model.addAttribute("standardCards", standardCards);
        model.addAttribute("limitedCards", limitedCards);
        model.addAttribute("player", usr);
        return "/cardPull/cardPullForm";
    }

    @PostMapping("/cardPull/add")
    public RedirectView addCardPull(CardPullForm cardPullForm, Principal principal, RedirectAttributes redirectAttributes) {
        boolean updateRank = true;
        Optional<Player> playerOpt = playerService.findByUsername(principal.getName());
        Player player = playerOpt.orElseThrow(() -> new RuntimeException("Player information cannot be found."));
        LocalDateTime pullTimestamp = getPullTimestamp(cardPullForm.getPullTimestamp());
        ActivityRunPeriod arp = activityRunPeriodService.getActivityRunPeriodByBannerIdAndTimestamp(cardPullForm.getBannerId(), pullTimestamp).orElseThrow(()-> {
            Banner b = bannerService.getBannerById(cardPullForm.getBannerId()).orElseThrow();
            return new RuntimeException("The pull timestamp " + pullTimestamp + " cannot be matched to the " + b.getName() + " banner. No run within that timeframe exists.");
        });
        validateTimestamp(pullTimestamp, arp, player.getId());

        final int SINGLE_CARD_PULL = 1;
        final int MULTI_CARD_PULL = 10;
        int length = cardPullForm.getMultipleCardPulls() ? MULTI_CARD_PULL : SINGLE_CARD_PULL;
        for (int i = 0; i < length; i++) {
            cardPullService.addNewCardPull(createNewCardPull(arp, pullTimestamp, cardPullForm.getCardPulls().get(i), player, updateRank));
        }

        RedirectView rv = new RedirectView("/cardPull/add");
        redirectAttributes.addFlashAttribute("bannerId", cardPullForm.getBannerId());
        redirectAttributes.addFlashAttribute("date", pullTimestamp.toLocalDate().toString());
        redirectAttributes.addFlashAttribute("time", pullTimestamp.toLocalTime().toString());
        redirectAttributes.addFlashAttribute("numCards", length);
        rv.setExposeModelAttributes(false);

        return rv;
    }

    private CardPull createNewCardPull(ActivityRunPeriod arp, LocalDateTime pullTimestamp, CardPullSubForm cardPullSubForm, Player player, boolean updateRank) {
        CardPull cardPull = new CardPull();
        cardPull.setActivityRunPeriod(arp);
        cardPull.setPullTimestamp(pullTimestamp);
        Card card = cardService.getCardById(cardPullSubForm.getCardId()).orElseThrow(()-> new RuntimeException("Card information cannot be found"));
        cardPull.setPlayerCard(getPlayerCard(player, card, updateRank));

        String cardRarity = card.getRarityType();
        if (!cardRarity.equals("3 Star")) {
            setFieldsForRareCard(cardPull, arp, cardPullSubForm);
        }
        return cardPull;
    }

    private void setFieldsForRareCard(CardPull cardPull, ActivityRunPeriod arp, CardPullSubForm cardPullSubForm) {
        String bannerMainType = arp.getBanner().getBannerCategory().getBannerMainType();
        String cardRarity = cardPull.getPlayerCard().getCard().getRarityType();
        Long playerId = cardPull.getPlayerCard().getPlayer().getId();
        boolean rerun = arp.getRerun();

        Optional<CardPull> lastRareCardPullOptional = cardPullService.getLastCardPullByBannerTypeAndCardRarity(cardRarity, bannerMainType, rerun, playerId);

        int pityNum = findCurrentPity(cardRarity, lastRareCardPullOptional, bannerMainType, rerun, playerId);
        cardPull.setPullsUntilHardPity(pityNum);
        final int FOUR_STAR_HARD_PITY_ZONE = 0;
        final int FIVE_STAR_HARD_PITY_ZONE = 10;
        cardPull.setHitHardPity((cardRarity.equals("4 Star") && pityNum == FOUR_STAR_HARD_PITY_ZONE)
                || (cardRarity.equals("5 Star") && pityNum < FIVE_STAR_HARD_PITY_ZONE));
        if (cardRarity.equals("5 Star") && arp.getBanner().getBannerCategory().getBannerMainType().equals("Limited")) {
            setFiftyFiftyAndPreciseWishFields(cardPull, arp, cardPullSubForm, lastRareCardPullOptional);
        }
    }

    private void setFiftyFiftyAndPreciseWishFields(CardPull cardPull, ActivityRunPeriod arp, CardPullSubForm cardPullSubForm, Optional<CardPull> lastRareCardPullOptional) {
        String cardBannerMainType = cardPull.getPlayerCard().getCard().getCardOrigin().getBanner().getBannerCategory().getBannerMainType();
        cardPull.setLimited(cardBannerMainType.equals("Limited"));
        cardPull.setWonFiftyFifty(calculateFiftyFiftyWin(cardPull, lastRareCardPullOptional, arp));
        if (cardPull.getLimited() && arp.getBanner().getBannerCategory().getBannerSubType().equals("Multi")) {
            boolean preciseWish = cardPullSubForm.getPreciseWish() != null ? cardPullSubForm.getPreciseWish() : false;
            cardPull.setPreciseWish(preciseWish);
        }
    }

    private LocalDateTime getPullTimestamp(String timestamp) {
        try {
            String cleanSpaces = timestamp.replace(" ", "T");
            return LocalDateTime.parse(cleanSpaces);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Could not process timestamp value from string: " + timestamp);
        }
    }

    private void validateTimestamp(LocalDateTime pullTimestamp, ActivityRunPeriod arp, Long playerId) {
        Optional<LocalDateTime> timestampOptional = cardPullService.getMostRecentTimestampForBannerType(arp.getBanner().getBannerCategory().getBannerMainType(), arp.getRerun(), playerId);
        if (timestampOptional.isPresent()) {
            LocalDateTime timestamp = timestampOptional.get();
            if (pullTimestamp.isEqual(timestamp)) {
                throw new RuntimeException("The timestamp " + pullTimestamp + " has already been used. If you are tracking the results of a 10 pull, please use the relevant option on the form.");
            }
            else if (pullTimestamp.isBefore(timestamp)) {
                throw new RuntimeException("The provided timestamp: " + pullTimestamp + " is less than the most recent pull. Please add your pulls in the order they were made.");
            }
        }
    }

    private PlayerCard getPlayerCard(Player player, Card card, boolean updateRank) {
        PlayerCard playerCard;
        Optional<PlayerCard> pcOpt = playerCardService.getPlayerCardByPlayerAndCardId(player.getId(), card.getId());
        if (pcOpt.isEmpty()) {
            playerCard = playerCardService.createNewPlayerCard(player, card);
        } else {
            playerCard = pcOpt.get();
            if (updateRank) {
                int rankNum = playerCard.getRankType().charAt(1) - '0';
                if (rankNum < 3) {
                    rankNum++;
                    playerCard.setRankType("R" + rankNum);
                    playerCardService.updatePlayerCard(playerCard);
                }
            }
        }
        return playerCard;
    }

    private int findCurrentPity(String cardRarity, Optional<CardPull> lastRareCardPullOptional, String bannerMainType, boolean rerun, Long playerId) {
        final int FOUR_STAR_PITY = 10;
        final int FIVE_STAR_PITY = 70;
        int pityForRarity = cardRarity.equals("5 Star") ? FIVE_STAR_PITY : FOUR_STAR_PITY;

        int pityNum;
        if (lastRareCardPullOptional.isEmpty()) {
            pityNum = pityForRarity - cardPullService.getNumberOfPullsTotalForBannerType(bannerMainType, rerun, playerId);
        } else {
            CardPull lastRareCardPull = lastRareCardPullOptional.get();
            pityNum = pityForRarity - cardPullService.getNumberOfPullsSinceGivenIdForBannerType(bannerMainType, rerun, lastRareCardPull.getId(), playerId);
        }
        return pityNum - 1;
    }

    private boolean calculateFiftyFiftyWin(CardPull cardPull, Optional<CardPull> lastRareCardPullOptional, ActivityRunPeriod arp) {
        if (cardPull.getLimited()) {
            if (lastRareCardPullOptional.isEmpty()) {
                return true;
            } else {
                CardPull last5StarPulled = lastRareCardPullOptional.get();
                Banner lastPulledCardBanner = last5StarPulled.getPlayerCard().getCard().getCardOrigin().getBanner();
                return lastPulledCardBanner.getBannerCategory().getBannerMainType().equals("Limited") // if last card is limited...
                        && (!lastPulledCardBanner.getId().equals(arp.getBanner().getId()) // if last card wasn't pulled on this banner
                        || !arp.getBanner().getBannerCategory().getBannerSubType().equals("Multi") // if current banner isn't a multi
                        || !last5StarPulled.getWonFiftyFifty() // if last 5 star lost their 50/50
                        || last5StarPulled.getPreciseWish()); // if last card is the player's precise wish
            }
        }
        return false;
    }
}
