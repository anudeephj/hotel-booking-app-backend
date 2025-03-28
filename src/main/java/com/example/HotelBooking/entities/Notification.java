package com.example.HotelBooking.entities;

import com.example.HotelBooking.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name="notifications")
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    private String subject;

    @NotBlank(message="recipient is required")
    private String recipient;

    private String body;

    @Column(name = "reference_no")
    private String bookingReference;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private final LocalDateTime createdAt = LocalDateTime.now();
}
