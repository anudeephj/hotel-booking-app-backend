package com.example.HotelBooking.payments.stripe;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.entities.Booking;
import com.example.HotelBooking.entities.PaymentEntity;
import com.example.HotelBooking.enums.NotificationType;
import com.example.HotelBooking.enums.PaymentGateway;
import com.example.HotelBooking.enums.PaymentStatus;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.payments.stripe.dto.PaymentRequest;
import com.example.HotelBooking.repositories.BookingRepository;
import com.example.HotelBooking.repositories.PaymentRepository;
import com.example.HotelBooking.services.NotificationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Value("${stripe.api.secret.key}")
    private String secretKey;

    //This method:
    //
    //Validates the booking using the reference
    //Checks if payment is already made
    //Creates a Stripe PaymentIntent with the amount and metadata
    //Returns the client secret needed by the frontend to complete the payment
    public String createPaymentIntent(PaymentRequest paymentRequest) {

        log.info("Inside createPaymentIntent()");
        Stripe.apiKey = secretKey;

        //getting bookingreference from frontend
        String bookingReference = paymentRequest.getBookingReference();

        //search the database if we have that booking already
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking Not Found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new NotFoundException("Payment already made for this booking");

        }
        //amount is multiplied by 100 because Stripe uses smallest currency unit:
        //e.g., if user pays $25.99, you pass 2599 cents
        try{
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) //amount cents
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();

            //Actually creates the payment intent with Stripe API.
            //Returns the client secret back to frontend.
            //This is used on the frontend to complete the payment using Stripe.js.
            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();

        }catch (Exception e){
            throw new RuntimeException("Error creating payment intent");
        }

    }

    //Saves the result of the payment (success/failure)
    //Updates the booking payment status
    //Sends a notification (email) to the user
    public void updatePaymentBooking(PaymentRequest paymentRequest) {

        log.info("Inside updatePaymentBooking()");
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booing Not Found"));

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());

        if (!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment); //save payment to database

        //create and send notifiaction
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        log.info("About to send notification inside updatePaymentBooking  by sms");


        if (paymentRequest.isSuccess()){
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking); //Update the booking

            notificationDTO.setSubject("Booking Payment Successful");
            notificationDTO.setBody("Congratulation!! Your payment for booking with reference: " + bookingReference + "is successful");
            notificationService.sendEmail(notificationDTO); //send email

        }else {

            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking); //Update the booking

            notificationDTO.setSubject("Booking Payment Failed");
            notificationDTO.setBody("Your payment for booking with reference: " + bookingReference + "failed with reason: " + paymentRequest.getFailureReason());
            notificationService.sendEmail(notificationDTO); //send email
        }

    }

}


