package com.rohankumar.easylodge.dtos.payment;

import com.rohankumar.easylodge.entities.booking.Booking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Booking is required")
    private Booking booking;

    @NotBlank(message = "Success url is required")
    private String successUrl;

    @NotBlank(message = "Failure url is required")
    private String failureUrl;
}
