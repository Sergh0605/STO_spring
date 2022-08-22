package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@ToString
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

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<OrderStatus> orderHistory;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<OrderItem> orderItem;

    public void addStatus(OrderStatus status) {
        orderHistory.add(status);
        status.setOrder(this);
    }

    public void removeStatus(OrderStatus status) {
        orderHistory.remove(status);
        status.setOrder(null);
    }

    public void addItem(OrderItem item) {
        boolean duplicatesFound = false;
        for (OrderItem currentItem : orderItem) {
            if (Objects.equals(currentItem.getPriceItem().getId(), item.getPriceItem().getId())) {
                duplicatesFound = true;
                currentItem.setCost(currentItem.getCost() + item.getCost());
                currentItem.setQuantity(currentItem.getQuantity() + item.getQuantity());
            }
        }
        if (!duplicatesFound) {
            orderItem.add(item);
            item.setOrder(this);
        }
    }

    public void removeItem(OrderItem item) {
        orderItem.remove(item);
        item.setOrder(null);
    }
}
