package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "order_status")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "comment")
    @Lob
    private String comment;

    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
