package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.PaymentHistory;

import java.util.List;

public interface PaymentHistoryRepository {
    PaymentHistory save(PaymentHistory paymentHistory);

    List<PaymentHistory> findAllByUserId(Long userId);
}
