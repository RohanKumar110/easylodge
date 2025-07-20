package com.rohankumar.easylodge.services.payment.impl;

import com.rohankumar.easylodge.dtos.payment.PaymentRequest;
import com.rohankumar.easylodge.entities.booking.Booking;
import com.rohankumar.easylodge.entities.hotel.Hotel;
import com.rohankumar.easylodge.entities.room.Room;
import com.rohankumar.easylodge.entities.user.User;
import com.rohankumar.easylodge.repositories.booking.BookingRepository;
import com.rohankumar.easylodge.security.utils.SecurityUtils;
import com.rohankumar.easylodge.services.payment.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import static com.stripe.param.checkout.SessionCreateParams.BillingAddressCollection.REQUIRED;
import static com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentService {

    @Value("${app.payment.currency}")
    private String currency;

    private final BookingRepository bookingRepository;

    @Override
    public String getSession(PaymentRequest paymentRequest) {

        try {

            Booking booking = paymentRequest.getBooking();
            User currentUser = SecurityUtils.getCurrentUser();

            log.info("Initiating payment session for booking ID: {}", booking.getId());

            Customer customer = createStripeCustomer(currentUser);

            LineItem lineItem = buildLineItem(booking);

            Session session = createStripeSession(paymentRequest, customer, lineItem);

            booking.setSessionId(session.getId());
            bookingRepository.save(booking);

            return session.getUrl();

        } catch (StripeException e) {

            log.error("Stripe session creation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate payment session.", e);
        }
    }

    private Customer createStripeCustomer(User user) throws StripeException {

        log.debug("Creating Stripe customer for user: {}", user.getId());

        CustomerCreateParams customerParams = CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();

        return Customer.create(customerParams);
    }

    private LineItem buildLineItem(Booking booking) {

        Hotel hotel = booking.getHotel();
        Room room = booking.getRoom();
        BigDecimal amount = booking.getAmount();
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        return LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        PriceData.builder()
                                .setUnitAmount(amountInCents)
                                .setCurrency(currency)
                                .setProductData(
                                        PriceData.ProductData.builder()
                                                .setName(hotel.getName() + " - " + room.getType())
                                                .setDescription("Booking ID: " + booking.getId())
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private Session createStripeSession(PaymentRequest request, Customer customer, LineItem lineItem) throws StripeException {

        SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setMode(PAYMENT)
                .setBillingAddressCollection(REQUIRED)
                .setCustomer(customer.getId())
                .setSuccessUrl(request.getSuccessUrl())
                .setCancelUrl(request.getFailureUrl())
                .addLineItem(lineItem)
                .build();

        return Session.create(sessionParams);
    }
}
