package lads.lads_website.service;

import lads.lads_website.domain.PlayerCard;
import lads.lads_website.repository.PlayerCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerCardService {

    private PlayerCardRepository playerCardRepository;

    @Autowired
    public PlayerCardService(PlayerCardRepository playerCardRepository) {
        this.playerCardRepository = playerCardRepository;
    }

    public Optional<PlayerCard> getPlayerCardById(Long id) {
        return playerCardRepository.findById(id);
    }

    public Optional<PlayerCard> getPlayerCardByPlayerAndCardId(Long playerId, Long cardId) {
        return playerCardRepository.findByPlayerIdAndCardId(playerId, cardId);
    }

    public PlayerCard addNewPlayerCard(PlayerCard playerCard) {
        return playerCard = playerCardRepository.save(playerCard);
    }

    public List<PlayerCard> getAllPlayerCardsForGivenPlayerId(Long playerId) {
        return playerCardRepository.findAllByPlayerId(playerId);
    }

    public void updatePlayerCard(PlayerCard playerCard) {
        playerCardRepository.save(playerCard);
    }
}
