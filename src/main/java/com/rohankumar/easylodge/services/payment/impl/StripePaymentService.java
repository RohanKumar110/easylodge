package com.rohankumar.easylodge.services.payment.impl;

import com.rohankumar.easylodge.dtos.payment.PaymentRequest;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.payment.Payment;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.enums.payment.PaymentStatus;
import com.rohankumar.easylodge.repositories.payment.PaymentRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.payment.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.stripe.param.checkout.SessionCreateParams.*;
import static com.stripe.param.checkout.SessionCreateParams.BillingAddressCollection.REQUIRED;
import static com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public String getSession(PaymentRequest paymentRequest) {

        try {

            log.info("Getting payment session for booking: {}", paymentRequest.getBooking().getId());

            log.info("Getting current user");
            User currentUser = SecurityUtils.getCurrentUser();

            log.info("Creating customer information for user: {}", currentUser.getId());
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setName(currentUser.getName())
                            .setEmail(currentUser.getEmail())
                            .build()
            );

            Hotel hotel = paymentRequest.getBooking().getHotel();
            Room room =  paymentRequest.getBooking().getRoom();
            BigDecimal amount = paymentRequest.getBooking().getAmount();
            Long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            LineItem lineItem =  LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(PriceData.builder()
                            .setUnitAmount(amountInCents)
                            .setCurrency("usd")
                            .setProductData(PriceData.ProductData.builder()
                                    .setName(hotel.getName() + " - " + room.getType())
                                    .setDescription("Booking ID: " + paymentRequest.getBooking().getId())
                                    .build())
                            .build())
                    .build();

            log.info("Creating session");
            SessionCreateParams sessionParams = builder()
                    .setMode(PAYMENT)
                    .setBillingAddressCollection(REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(paymentRequest.getSuccessUrl())
                    .setCancelUrl(paymentRequest.getFailureUrl())
                    .addLineItem(lineItem)
                    .build();

            Session session = Session.create(sessionParams);

            Payment newPayment = Payment.builder()
                    .sessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .amount(paymentRequest.getBooking().getAmount())
                    .booking(paymentRequest.getBooking())
                    .build();

            log.info("Saving Payment with session: {}", session.getId());
            Payment savedPayment = paymentRepository.save(newPayment);
            log.info("Payment saved successfully with status: {}", savedPayment.getStatus());

            return session.getUrl();

        } catch (StripeException ex) {
            log.error("Failed to create Stripe checkout session");
            log.error("Erroe Message: " + ex.getMessage());
            log.error("Error: ", ex);
            throw new RuntimeException("Payment Error", ex);
        }
    }
}
