package org.itentika.edu.spuzakov.mvc.dto;

import lombok.*;

//нет списка обязательных полей
//добавил
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExOrderStatusDto {
    private Long orderId;
    @NonNull
    private String status;
    private String comment;
}
