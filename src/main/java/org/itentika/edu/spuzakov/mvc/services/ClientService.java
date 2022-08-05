package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.persistence.dao.ClientRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client getApprovedClient(Client nonApprovedClient) {
        Client approvedClient;
        if (nonApprovedClient.getId() == null) {
            approvedClient = clientRepository.save(nonApprovedClient);
        } else {
            approvedClient = clientRepository.findById(nonApprovedClient.getId()).orElseGet(() -> {
                nonApprovedClient.setId(null);
                return clientRepository.save(nonApprovedClient);
            });
        }
        return approvedClient;
    }
}
