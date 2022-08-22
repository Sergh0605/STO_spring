package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@ToString
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

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "item_id", nullable = false)
    private PriceItem priceItem;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
}
