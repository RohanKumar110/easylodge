package com.rohankumar.easylodge.controllers.webhook;

import com.rohankumar.easylodge.services.booking.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/webhook")
public class WebHookController {

    @Value("${stripe.webhook.secret}")
    private String endPointSecret;

    private final BookingService bookingService;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayment(
            @RequestBody String payload,
            @RequestHeader(name = "Stripe-Signature") String stripeSignature) {

        try {

            Event event = Webhook.constructEvent(payload, stripeSignature, endPointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException ex) {

            throw new RuntimeException(ex);
        }
    }
}
