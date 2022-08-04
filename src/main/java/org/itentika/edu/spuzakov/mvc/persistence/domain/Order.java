package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "service_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reason")
    @Lob
    private String reason;

    @Column(name = "begin_date")
    @CreationTimestamp
    private LocalDateTime beginDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "comment")
    @Lob
    private String comment;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "master_id")
    private Staff master;

    @ManyToOne
    @JoinColumn(name = "administrator_id")
    private Staff administrator;

    @OneToMany(mappedBy = "order")
    private List<OrderStatus> orderHistory;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItem;
}
