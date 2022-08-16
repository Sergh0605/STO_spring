package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.StaffRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StaffService {
    private final StaffRepository staffRepository;
    //а если добавим новую должность в таблицу?
    //а если в базе будет более одного сотрудника с одинаковым именем и должностью?

    //добавил роли и уникальные логины для персонала
    public Staff findByLogin(String login) {
        return staffRepository.findByLogin(login).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Staff with login %s not found", login));
        });
    }

    public Staff findById(Long id) {
        /*
         * предполагается ролевой доступ но в качестве роли сотрудника используем его должность, что логически неверно и при подключении
         * spring security потребуется рефакторинг
         *
         * также возможно совпадение имени и должности (для одного сотрудника несколько должностей, либо два сотрудника с одинаковым именем и одной должностью, во всяком случае БД нас в этом никак не ограничивает)
         * сотрудника в одной таблице и в таком случае метод работать не будет
         */

        //Поиск сотрудника по уникальному id
        return staffRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Staff with Id %s ", id));
        });
    }
}
