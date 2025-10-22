package lads.lads_website.repository;

import lads.lads_website.domain.CardOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardOriginRepository extends JpaRepository<CardOrigin, Long> {

    Optional<CardOrigin> findByEventId(Long eventId);

    Optional<CardOrigin> findByBannerId(Long bannerId);

}
