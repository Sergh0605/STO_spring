package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.ClientDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class ClientClientDto implements Converter<Client, ClientDto> {
    @Override
    public ClientDto convert(Client source) {
        return ClientDto.builder()
                .id(source.getId())
                .name(source.getName())
                .phone(source.getPhone())
                .birthDate(source.getBirthDate())
                .address(source.getAddress())
                .build();
    }
}
