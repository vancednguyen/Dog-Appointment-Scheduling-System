package edu.sjsu.cmpe172.Doggy.service;

import org.springframework.stereotype.Service;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import edu.sjsu.cmpe172.Doggy.repository.ServiceOfferingRepository;

import java.util.List;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    public void addService(ServiceOffering service) {
        if (service.getProviderId() == null) {
            throw new IllegalArgumentException("Provider ID is required.");
        }
        if (service.getServiceName() == null || service.getServiceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Service name is required.");
        }
        if (service.getServiceDuration() == null || service.getServiceDuration() <= 0) {
            throw new IllegalArgumentException("Service duration must be greater than 0.");
        }
        if (service.getPrice() == null || service.getPrice() < 0) {
            throw new IllegalArgumentException("Price must be 0 or greater.");
        }

        serviceOfferingRepository.insertService(service);
    }

    public List<ServiceOffering> getServicesByProviderId(Long providerId) {
        return serviceOfferingRepository.findByProviderId(providerId);
    }
}