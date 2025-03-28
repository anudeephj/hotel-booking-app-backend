package com.example.HotelBooking.entities;

import com.example.HotelBooking.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.EntityGraph;

import java.io.FilenameFilter;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "email is required")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "password is required")
    private String password;
    private String firstName;
    private String lastName;

    @NotBlank(message = "phone number is required")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;   //customer, admin

    private Boolean isActive;
    private final LocalDateTime created = LocalDateTime.now();
}
