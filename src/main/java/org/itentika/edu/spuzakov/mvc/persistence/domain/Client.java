package org.itentika.edu.spuzakov.mvc.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @Lob
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "address")
    @Lob
    private String address;
}
