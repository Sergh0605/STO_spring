package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "cost")
    private Long cost;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private PriceItem priceItem;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
