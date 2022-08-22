package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<OrderStatus> orderHistory;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<OrderItem> orderItem;

    public void addStatus(OrderStatus status) {
        orderHistory.add(status);
        status.setOrder(this);
    }

    public void addItem(OrderItem item) {
        orderItem.add(item);
        item.setOrder(this);
    }
}
