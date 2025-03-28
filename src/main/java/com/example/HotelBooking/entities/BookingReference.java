package com.example.HotelBooking.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name="booking_reference")
@AllArgsConstructor
@NoArgsConstructor
public class BookingReference {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String referenceNo;
}
