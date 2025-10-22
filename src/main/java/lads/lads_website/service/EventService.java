package lads.lads_website.service;

import lads.lads_website.domain.Event;
import lads.lads_website.domain.projections.EventTypeOnly;
import lads.lads_website.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event addNewEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> getEventByName(String eventName) {
        return eventRepository.findByName(eventName);
    }

    public List<String> getAllEventTypes() {
        List<EventTypeOnly> eto = eventRepository.findAllDistinctProjectedBy();
        List<String> eventTypes = new ArrayList<>();
        eto.forEach(et -> eventTypes.add(et.getEventType()));
        return eventTypes;
    }
}
