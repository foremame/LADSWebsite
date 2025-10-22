package lads.lads_website.service;

import lads.lads_website.domain.Card;
import lads.lads_website.domain.projections.LoveInterestTypeOnly;
import lads.lads_website.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CardService {

    private CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card addNewCard(Card card) {
        return cardRepository.save(card);
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Optional<Card> getCardByName(String cardName) {
        return cardRepository.findByName(cardName);
    }

    public List<Card> getAllCardsByBannerType(String bannerMainType) {
        return cardRepository.findByCardOriginBannerBannerCategoryBannerMainTypeOrderById(bannerMainType);
    }

    public List<String> getAllLoveInterests() {
        List<LoveInterestTypeOnly> lito = cardRepository.findAllDistinctProjectedBy();
        List<String> loveInterests = new ArrayList<>();
        lito.forEach(li -> loveInterests.add(li.getLoveInterestType()));
        return loveInterests;
    }

}
