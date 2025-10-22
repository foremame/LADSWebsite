package lads.lads_website.repository;

import lads.lads_website.domain.PlayerBounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerBountyRepository extends JpaRepository<PlayerBounty, Long> {
    List<PlayerBounty> findAllByPlayerId(Long playerId);
    Optional<PlayerBounty> findByBountyRewardBountyNameTypeAndPlayerId(String bountyNameType, Long playerId);
}
