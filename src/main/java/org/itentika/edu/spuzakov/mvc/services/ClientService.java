package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.persistence.dao.ClientRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional
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
        approvedClient.setName(nonApprovedClient.getName());
        approvedClient.setPhone(nonApprovedClient.getPhone());
        approvedClient.setBirthDate(nonApprovedClient.getBirthDate());
        approvedClient.setAddress(nonApprovedClient.getAddress());
        return clientRepository.save(approvedClient);
    }
}
