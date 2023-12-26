package com.hoangtien2k3.userservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", unique = true, nullable = false, updatable = false)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "fullName")
    private String fullname;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "userName")
    private String username;

    @NaturalId
    @NotBlank
    @Size(max = 50)
    @Email(message = "Input must be in Email format")
    @Column(name = "email")
    private String email;

    @JsonIgnore
    @NotBlank
    @Size(min = 6, max = 100)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "gender", nullable = false)
    private String gender;

    @Size(min = 10, max = 11)
    @Column(name = "phoneNumber", unique = true)
    private String phone;

    @Lob
    @Column(name = "imageUrl")
    private String avatar;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

}
