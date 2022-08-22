package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@ToString
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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderStatus)) return false;
        return id != null && id.equals(((OrderStatus) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
