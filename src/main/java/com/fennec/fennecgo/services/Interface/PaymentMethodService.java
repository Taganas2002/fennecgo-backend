package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.models.PaymentMethod;
import java.util.List;

public interface PaymentMethodService {

    PaymentMethod createPaymentMethod(PaymentMethod paymentMethod);

    PaymentMethod getPaymentMethod(Long id);

    List<PaymentMethod> getAllPaymentMethods();
}
