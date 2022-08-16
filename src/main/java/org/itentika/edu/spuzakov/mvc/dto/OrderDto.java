package org.itentika.edu.spuzakov.mvc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

//нет списка обязательных полей
//добавил
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDto {
    private Long id;
    @NonNull
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private String comment;
    @NonNull
    private ClientDto client;
    private List<OrderItemDto> orderItem;
    private List<OrderStatusDto> orderHistory;
    private StaffDto master;
    private StaffDto administrator;


}
