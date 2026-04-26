package edu.sjsu.cmpe172.Doggy.model;

public class Appointment {
    private Long id;
    private String userId;
    private String providerId;
    private String serviceId;
    private String slotId;
    private String status;

    public Appointment() {
    }

    public Appointment(Long id, String userId, String providerId, String serviceId, String slotId, String status) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
        this.serviceId = serviceId;
        this.slotId = slotId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
