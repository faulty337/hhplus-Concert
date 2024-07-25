package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.PaymentHistory;

import java.util.List;

public interface PaymentService {
    PaymentHistory addPaymentHistory(PaymentHistory paymentHistory);

    List<PaymentHistory> getPaymentHistoryList(Long userId);
}
