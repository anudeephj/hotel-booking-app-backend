package com.example.HotelBooking.payments.stripe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {

    @NotBlank(message="booking is required")
    private String BookingReference;
    private BigDecimal Amount;

    private String transactionId;
    private boolean success;
    private String failureReason;


}
