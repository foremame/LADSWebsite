package lads.lads_website.service;

import lads.lads_website.domain.ResourceTracking;
import lads.lads_website.repository.ResourceTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceTrackingService {

    private ResourceTrackingRepository resourceTrackingRepository;

    @Autowired
    public ResourceTrackingService(ResourceTrackingRepository resourceTrackingRepository) {
        this.resourceTrackingRepository = resourceTrackingRepository;
    }

    public List<ResourceTracking> getAllResourceTrackingByPlayerId(Long playerId) {
        return resourceTrackingRepository.findAllByPlayerIdOrderByAddDateAsc(playerId);
    }

    public Optional<ResourceTracking> getMostRecentResourceTracking(Long playerId) {
        return resourceTrackingRepository.findFirstByPlayerIdOrderByAddDateDesc(playerId);
    }

    public ResourceTracking addNewResourceTracking(ResourceTracking resourceTracking) {
        return resourceTrackingRepository.save(resourceTracking);
    }

    public Optional<ResourceTracking> getLastRecordGivenDate(Timestamp date) {
        return resourceTrackingRepository.findFirstByAddDateBeforeOrderByAddDateDesc(date);
    }
}
