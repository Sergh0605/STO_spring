package org.itentika.edu.spuzakov.mvc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

//нет списка обязательных полей
//добавил
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ClientDto {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String address;
}
