package lads.lads_website.controller;

import lads.lads_website.domain.*;
import lads.lads_website.forms.CardPullForm;
import lads.lads_website.forms.subforms.CardPullSubForm;
import lads.lads_website.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class CardPullController {

    private final int SINGLE_CARD_PULL = 1;
    private final int MULTI_CARD_PULL = 10;

    private final int FOUR_STAR_PITY = 10;
    private final int FIVE_STAR_PITY = 70;

    private final int FOUR_STAR_HARD_PITY_ZONE = 0;
    private final int FIVE_STAR_HARD_PITY_ZONE = 10;

    private CardPullService cardPullService;
    private CardService cardService;
    private PlayerCardService playerCardService;
    private ActivityRunPeriodService activityRunPeriodService;
    private BannerService bannerService;

    // For finding current session user
    private UserDetailsService userDetailsService;
    private PlayerService playerService;

    @Autowired
    public CardPullController(CardPullService cardPullService, UserDetailsService userDetailsService, PlayerService playerService, CardService cardService,
                              PlayerCardService playerCardService, ActivityRunPeriodService activityRunPeriodService, BannerService bannerService) {
        this.cardPullService = cardPullService;
        this.userDetailsService = userDetailsService;
        this.playerService = playerService;
        this.cardService = cardService;
        this.playerCardService = playerCardService;
        this.activityRunPeriodService = activityRunPeriodService;
        this.bannerService = bannerService;
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
        Banner banner = bannerService.getBannerById(bannerId).get();
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
        UserDetails usr = userDetailsService.loadUserByUsername(principal.getName());

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
        // if player wants card pulling service to update card rank (defaults to true for now)
        boolean updateRank = false;

        LocalDateTime pullTimestamp;
        try {
            pullTimestamp = LocalDateTime.parse(cardPullForm.getPullTimestamp());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Could not process timestamp value from string: " + cardPullForm.getPullTimestamp());
        }

        // Find the activity run period given the banner id and the pull timestamp
        Optional<ActivityRunPeriod> arpOpt = activityRunPeriodService.getActivityRunPeriodByBannerIdAndTimestamp(cardPullForm.getBannerId(), pullTimestamp);

        if (arpOpt.isEmpty()) {
            Banner b = bannerService.getBannerById(cardPullForm.getBannerId()).get();
            throw new RuntimeException("The pull timestamp " + pullTimestamp + " cannot be matched to the " + b.getName() + " banner. No run within that timeframe exists.");
        }
        ActivityRunPeriod arp = arpOpt.get();

        UserDetails usr = userDetailsService.loadUserByUsername(principal.getName());
        Optional<Player> playerOpt = playerService.findByUsername(usr.getUsername());
        if (playerOpt.isEmpty()) {
            throw new RuntimeException("User " + principal.getName() + " cannot be found.");
        }
        Player player = playerOpt.get();

        // Validate that the timestamp hasn't been used yet or is out of order
        validateTimestamp(pullTimestamp, arp, player.getId());

        int length = cardPullForm.getMultipleCardPulls() ? MULTI_CARD_PULL : SINGLE_CARD_PULL;

        // Loop through the pulls returned by the form (single or multi)
        for (int i = 0; i < length; i++) {

            CardPullSubForm cardPullSubForm = cardPullForm.getCardPulls().get(i);

            CardPull cardPull = new CardPull();

            cardPull.setActivityRunPeriod(arp);
            cardPull.setPullTimestamp(pullTimestamp);

            Optional<Card> cardOpt = cardService.getCardById(cardPullSubForm.getCardId());
            if (cardOpt.isEmpty()) {
                throw new RuntimeException("Card information cannot be found");
            }
            Card card = cardOpt.get();

            PlayerCard playerCard = getPlayerCard(player, card, updateRank);
            cardPull.setPlayerCard(playerCard);

            String cardRarity = playerCard.getCard().getRarityType();
            if (!cardRarity.equals("3 Star")) {
                // If it is a 4 or 5 star, we need to calculate the current pity.
                String bannerMainType = arp.getBanner().getBannerCategory().getBannerMainType();
                boolean rerun = arp.getRerun();

                // Find the id of the last 4/5* card pulled on this banner type (Standard/Limited/Rerun)
                Optional<CardPull> lastRareCardPullOptional = cardPullService.getLastCardPullByBannerTypeAndCardRarity(cardRarity, bannerMainType, rerun, player.getId());

                int pityNum = findCurrentPity(cardRarity, lastRareCardPullOptional, bannerMainType, rerun, player.getId());

                cardPull.setPullsUntilHardPity(pityNum);

                // Calculate if we hit hard pity on the pull
                boolean hitHardPity = (cardRarity.equals("4 Star") && pityNum == FOUR_STAR_HARD_PITY_ZONE)
                        || (cardRarity.equals("5 Star") && pityNum < FIVE_STAR_HARD_PITY_ZONE);
                cardPull.setHitHardPity(hitHardPity);

                if (cardRarity.equals("5 Star") && bannerMainType.equals("Limited")) {
                    // For 5*s pulled on limited banners there are 3 more fields that need to be calculated:
                    //Check if card is limited
                    String cardBannerMainType = card.getCardOrigin().getBanner().getBannerCategory().getBannerMainType();
                    cardPull.setLimited(cardBannerMainType.equals("Limited"));

                    // Check if the 50/50 was won
                    cardPull.setWonFiftyFifty(calculateFiftyFiftyWin(cardPull, lastRareCardPullOptional, arp));

                    // If current banner is a multi banner, need to check if the user marked the 5* as their precise wish
                    if (cardPull.getLimited() && arp.getBanner().getBannerCategory().getBannerSubType().equals("Multi")) {
                        boolean preciseWish = cardPullSubForm.getPreciseWish() != null ? cardPullSubForm.getPreciseWish() : false;
                        cardPull.setPreciseWish(preciseWish);
                    }
                }
            }
            cardPullService.addNewCardPull(cardPull);
        }

        RedirectView rv = new RedirectView("/cardPull/add");
        redirectAttributes.addFlashAttribute("bannerId", cardPullForm.getBannerId());
        redirectAttributes.addFlashAttribute("date", new SimpleDateFormat("yyyy-MM-dd").format(pullTimestamp));
        redirectAttributes.addFlashAttribute("time", new SimpleDateFormat("HH:mm:ss").format(pullTimestamp.toLocalTime()));
        redirectAttributes.addFlashAttribute("numCards", length);
        rv.setExposeModelAttributes(false);

        return rv;
    }

    private void validateTimestamp(LocalDateTime pullTimestamp, ActivityRunPeriod arp, Long playerId) {
        // Get most recent timestamp pulled on current banner type
        Optional<LocalDateTime> timestampOptional = cardPullService.getMostRecentTimestampForBannerType(arp.getBanner().getBannerCategory().getBannerMainType(), arp.getRerun(), playerId);
        // Check if the current pullTimestamp is less than or equal to that timestamp
        if (timestampOptional.isPresent()) {
            LocalDateTime timestamp = timestampOptional.get();
            if (pullTimestamp.compareTo(timestamp) == 0) {
                throw new RuntimeException("The timestamp " + pullTimestamp + " has already been used. If you are tracking the results of a 10 pull, please use the relevant option on the form.");
            }
            else if (pullTimestamp.compareTo(timestamp) < 0) {
                throw new RuntimeException("The provided timestamp: " + pullTimestamp + " is less than the most recent pull. Please add your pulls in the order they were made.");
            }
        }
    }

    private PlayerCard getPlayerCard(Player player, Card card, boolean updateRank) {
        PlayerCard playerCard;
        Optional<PlayerCard> pcOpt = playerCardService.getPlayerCardByPlayerAndCardId(player.getId(), card.getId());
        if (pcOpt.isEmpty()) {
            // If card does not exist, add a new entry with starting values for lvl/rank/awakened
            playerCard = new PlayerCard();
            playerCard.setCard(card);
            playerCard.setPlayer(player);
            playerCard.setAwakened(false);
            playerCard.setLevel(1);
            playerCard.setRankType("R0");
            playerCard = playerCardService.addNewPlayerCard(playerCard);
        } else {
            // If it exists, update the rank of the card if the user indicates they want that functionality and it
            // is not already at max rank (R3)
            playerCard = pcOpt.get();
            if (updateRank) {
                // Convert the card rank to an integer (Starts as: R0,R1,R2, or R3)
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
        int pityForRarity = cardRarity.equals("5 Star") ? FIVE_STAR_PITY : FOUR_STAR_PITY;

        int pityNum;
        if (lastRareCardPullOptional.isEmpty()) {
            // If it is empty, this is the first recorded pull of this rarity on this banner type. In which case we
            // return the pity num for the given rarity - total number of pulls made on this banner type
            pityNum = pityForRarity - cardPullService.getNumberOfPullsTotalForBannerType(bannerMainType, rerun, playerId);
        } else {
            // If we find a CardPull, we need to return the pity num for the given rarity - the number of pulls since
            // the found cardPull
            CardPull lastRareCardPull = lastRareCardPullOptional.get();
            pityNum = pityForRarity - cardPullService.getNumberOfPullsSinceGivenIdForBannerType(bannerMainType, rerun, lastRareCardPull.getId(), playerId);
        }
        // Need to subtract one to account for the current row being pulled but not currently stored in the db
        return pityNum - 1;
    }

    private boolean calculateFiftyFiftyWin(CardPull cardPull, Optional<CardPull> lastRareCardPullOptional, ActivityRunPeriod arp) {
        boolean wonFiftyFifty = false;
        if (cardPull.getLimited()) {
            // If there is no previous 5* pulled on this banner type, we have won the 50/50
            if (lastRareCardPullOptional.isEmpty()) {
                return true;
            } else {
                CardPull last5StarPulled = lastRareCardPullOptional.get();
                Banner lastPulledCardBanner = last5StarPulled.getPlayerCard().getCard().getCardOrigin().getBanner();
                // If the last 5 star pulled is limited, we might have won, but there is still more to check
                if (lastPulledCardBanner.getBannerCategory().getBannerMainType().equals("Limited")) {
                    // If the 5* was not pulled on the current banner or the sub_type of the current banner is
                    // not a multi banner, 50/50 is won since we don't need to worry about precise wish
                    if (!lastPulledCardBanner.getId().equals(arp.getBanner().getId())
                            || !arp.getBanner().getBannerCategory().getBannerSubType().equals("Multi")) {
                        return true;
                    } else {
                        // If we know the last card lost the 50/50 (b/c it was the guaranteed win following a standard pull or due to a precise wish point)
                        // we know we won this one
                        if (!last5StarPulled.getWonFiftyFifty()) {
                            return true;
                        } else {
                            // If the last 5* pulled was the precise wish of the multi banner, that means there
                            // is not currently a precise wish point in effect to guarantee the limited card pull
                            return last5StarPulled.getPreciseWish();
                        }
                    }
                }
            }
        }
        return false;
    }
}
