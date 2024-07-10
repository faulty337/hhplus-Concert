package com.hhp.concert.Business.Repository;

import com.hhp.concert.Business.Domain.PaymentHistory;

public interface PaymentHistoryRepository {
    PaymentHistory save(PaymentHistory paymentHistory);
}
