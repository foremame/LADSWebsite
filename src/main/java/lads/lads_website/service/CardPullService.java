package lads.lads_website.service;

import lads.lads_website.domain.CardPull;
import lads.lads_website.repository.CardPullRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardPullService {

    private CardPullRepository cardPullRepository;

    @Autowired
    public CardPullService(CardPullRepository cardPullRepository) {
        this.cardPullRepository = cardPullRepository;
    }

    public void addNewCardPull(CardPull cardPull) {
        cardPullRepository.save(cardPull);
    }

    public Optional<CardPull> getLastCardPullByBannerTypeAndCardRarity(String cardRarity, String bannerMainType, boolean rerun, Long playerId) {
        List<String> rarityList = new ArrayList<>();
        rarityList.add("5 Star");
        if (cardRarity.equals("4 Star")) { rarityList.add("4 Star"); }
        return cardPullRepository.findTopByBannerTypeAndPlayerIdAndCardRarity(bannerMainType, rerun, playerId, rarityList);
    }

    public int getNumberOfPullsForBanner(Long runId, Long playerId) {
        return cardPullRepository.findAllByActivityRunPeriodIdAndPlayerCardPlayerId(runId, playerId).size();
    }

    public int getNumberOfPullsTotalForBannerType(String bannerMainType, boolean rerun, Long playerId) {
        return cardPullRepository.getAllByBannerTypeAndPlayerId(bannerMainType, rerun, playerId).size();
    }

    public int getNumberOfPullsSinceGivenIdForBannerType(String bannerMainType, boolean rerun, Long id, Long playerId) {
        return cardPullRepository.getAllSinceIdByBannerTypeAndPlayerId(id, bannerMainType, rerun, playerId).size();
    }

    public Optional<LocalDateTime> getMostRecentTimestampForBannerType(String bannerMainType, boolean rerun, Long playerId) {
        Optional<CardPull> cardPull = cardPullRepository.findTopByBannerTypeAndPlayerId(bannerMainType, rerun, playerId);
        return cardPull.map(CardPull::getPullTimestamp);
    }
}
