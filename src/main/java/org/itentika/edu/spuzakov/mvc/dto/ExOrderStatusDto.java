package org.itentika.edu.spuzakov.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExOrderStatusDto {
    private Long orderId;
    private String status;
    private String comment;
}
