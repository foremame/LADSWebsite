package lads.lads_website.repository;

import lads.lads_website.domain.Event;
import lads.lads_website.domain.projections.EventTypeOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<EventTypeOnly> findAllDistinctProjectedBy();

    Optional<Event> findByName(String eventName);

}
