package lads.lads_website.domain;

import jakarta.persistence.*;

@Entity
@Table
public class ResourceTrackingValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceType;

    private Integer n;

    private Integer r;

    private Integer sr;

    private Integer ssr;

    private Integer general;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="resource_tracking_id")
    private ResourceTracking resourceTracking;

    public ResourceTrackingValues() {
    }

    public ResourceTrackingValues(Long id, String resourceType, Integer n, Integer r, Integer sr, Integer ssr, Integer general, ResourceTracking resourceTracking) {
        this.id = id;
        this.resourceType = resourceType;
        this.n = n;
        this.r = r;
        this.sr = sr;
        this.ssr = ssr;
        this.general = general;
        this.resourceTracking = resourceTracking;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public Integer getSr() {
        return sr;
    }

    public void setSr(Integer sr) {
        this.sr = sr;
    }

    public Integer getSsr() {
        return ssr;
    }

    public void setSsr(Integer ssr) {
        this.ssr = ssr;
    }

    public Integer getGeneral() {
        return general;
    }

    public void setGeneral(Integer general) {
        this.general = general;
    }

    public ResourceTracking getResourceTracking() {
        return resourceTracking;
    }

    public void setResourceTracking(ResourceTracking resourceTracking) {
        this.resourceTracking = resourceTracking;
    }
}
