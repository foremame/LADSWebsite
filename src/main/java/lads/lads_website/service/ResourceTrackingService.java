package lads.lads_website.service;

import lads.lads_website.domain.ResourceTracking;
import lads.lads_website.domain.ResourceTrackingValues;
import lads.lads_website.domain.projections.ResourceTypeOnly;
import lads.lads_website.repository.ResourceTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceTrackingService {

    private ResourceTrackingRepository resourceTrackingRepository;
    private ResourceTrackingValuesService resourceTrackingValuesService;

    @Autowired
    public ResourceTrackingService(ResourceTrackingRepository resourceTrackingRepository, ResourceTrackingValuesService resourceTrackingValuesService) {
        this.resourceTrackingRepository = resourceTrackingRepository;
        this.resourceTrackingValuesService = resourceTrackingValuesService;
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

    public ResourceTracking getEmpty() {
        ResourceTracking recentResources = new ResourceTracking(0L, 0, 0, 0, 0, 0, 0, 0,
                null, null, new ArrayList<>());
        List<ResourceTypeOnly> allValues = resourceTrackingValuesService.getAllResourceTypes();
        for (ResourceTypeOnly rto : allValues) {
            ResourceTrackingValues rtv = new ResourceTrackingValues(0L, rto.getResourceType(), 0, 0, 0, 0, 0, recentResources);
            recentResources.getResourceTrackingValues().add(rtv);
        }
        return recentResources;
    }
}
