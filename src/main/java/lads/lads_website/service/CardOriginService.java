package lads.lads_website.service;

import lads.lads_website.domain.CardOrigin;
import lads.lads_website.repository.CardOriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CardOriginService {

    private CardOriginRepository cardOriginRepository;

    @Autowired
    public CardOriginService(CardOriginRepository cardOriginRepository) {
        this.cardOriginRepository = cardOriginRepository;
    }

    public CardOrigin addNewCardOrigin(CardOrigin cardOrigin) {
        return cardOriginRepository.save(cardOrigin);
    }

    public Optional<CardOrigin> getCardOriginByActivityId(String originType, Long activityId) {
        if (originType.equals("Event")) {
            return cardOriginRepository.findByEventId(activityId);
        }
        else {
            return cardOriginRepository.findByBannerId(activityId);
        }
    }
}
