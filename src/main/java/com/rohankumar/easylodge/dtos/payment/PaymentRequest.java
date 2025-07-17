package com.rohankumar.easylodge.dtos.payment;

import com.rohankumar.easylodge.entities.booking.Booking;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private Booking booking;
    private String successUrl;
    private String failureUrl;
}
