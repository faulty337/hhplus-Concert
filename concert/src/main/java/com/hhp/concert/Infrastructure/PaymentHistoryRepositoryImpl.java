package com.hhp.concert.Infrastructure;

import com.hhp.concert.Business.Domain.PaymentHistory;
import com.hhp.concert.Business.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepository {
    private final PaymentHistoryJpaRepository paymentHistoryJpaRepository;

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryJpaRepository.save(paymentHistory);
    }
}
