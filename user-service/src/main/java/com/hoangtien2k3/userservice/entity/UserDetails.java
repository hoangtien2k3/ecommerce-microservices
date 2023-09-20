package com.hoangtien2k3.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;
    @Column(name = "street", length = 30)
    private String street;
    @Column(name = "street_number", length = 10)
    private String streetNumber;
    @Column(name = "zip_code", length = 6)
    private String zipCode;
    @Column(name = "locality", length = 30)
    private String locality;
    @Column(name = "country", length = 30)
    private String country;

    @OneToOne(mappedBy = "userDetails") // bidirectional
    @JsonIgnore
    private User user;

}
