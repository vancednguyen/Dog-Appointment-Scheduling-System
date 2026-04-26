package edu.sjsu.cmpe172.Doggy.service;

import org.springframework.stereotype.Service;
import edu.sjsu.cmpe172.Doggy.repository.ProviderRepository;
import edu.sjsu.cmpe172.Doggy.model.Provider;
@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public void registerProvider(Provider provider) {
        if (providerRepository.findByEmail(provider.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists.");
        }
        providerRepository.insertProvider(provider);
    }

    public Provider loginAndReturnProvider(String email, String password) {
        Provider provider = providerRepository.findByEmail(email);

        if (provider != null && provider.getPassword().equals(password)) {
            return provider;
        }
        return null;
    }
}