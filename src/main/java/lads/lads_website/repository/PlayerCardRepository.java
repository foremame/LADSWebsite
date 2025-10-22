package lads.lads_website.repository;

import lads.lads_website.domain.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCardRepository extends JpaRepository<PlayerCard, Long> {

    Optional<PlayerCard> findByPlayerIdAndCardId(Long playerId, Long cardId);

    List<PlayerCard> findAllByPlayerId(Long playerId);

}
