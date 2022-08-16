package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.ClientRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepository clientRepository;

    //здесь предполагается получение клиента, но в тоже время делаем его сохранение, логика метода неоднозначна
    //переработал
    @Transactional
    public Client create(Client newClient) {
        //а если все поля переданного клиента null мы его все равно сохраним?
        //добавил проверку в DTO и в БД
        return clientRepository.save(newClient);
    }

    @Transactional
    public Client update(Client clientForUpdate) {
        getById(clientForUpdate.getId());
        return clientRepository.save(clientForUpdate);
    }

    public Client getById(Long clientId) {
        return clientRepository.findById(clientId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Client with Id %s not found", clientId));
        });
    }

}
