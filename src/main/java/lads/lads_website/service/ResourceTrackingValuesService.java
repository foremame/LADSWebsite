package lads.lads_website.service;

import lads.lads_website.domain.ResourceTrackingValues;
import lads.lads_website.domain.projections.ResourceTypeOnly;
import lads.lads_website.repository.ResourceTrackingValuesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceTrackingValuesService {

    private ResourceTrackingValuesRepository repository;

    @Autowired
    public ResourceTrackingValuesService(ResourceTrackingValuesRepository repository) {
        this.repository = repository;
    }

    public List<ResourceTypeOnly> getAllResourceTypes() {
        return repository.findAllDistinctProjectedBy();
    }

    public void addNewResourceTrackingValue(ResourceTrackingValues rtv) {
        repository.save(rtv);
    }
}
