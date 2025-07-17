package com.rohankumar.easylodge.dtos.payment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String sessionUrl;
}
