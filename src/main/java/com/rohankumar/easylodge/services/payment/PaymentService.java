package com.rohankumar.easylodge.services.payment;

import com.rohankumar.easylodge.dtos.payment.PaymentRequest;

public interface PaymentService {

    String getSession(PaymentRequest paymentRequest);
}
