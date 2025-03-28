package com.example.HotelBooking.entities;


import com.example.HotelBooking.enums.PaymentGateway;
import com.example.HotelBooking.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="payments")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {


    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    private BigDecimal amount;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String BookingReference;
    private String failureReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
