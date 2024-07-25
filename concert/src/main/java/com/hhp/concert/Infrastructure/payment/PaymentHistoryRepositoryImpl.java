package com.hhp.concert.Infrastructure.payment;

import com.hhp.concert.Business.Domain.PaymentHistory;
import com.hhp.concert.Business.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepository {
    private final PaymentHistoryJpaRepository paymentHistoryJpaRepository;

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryJpaRepository.save(paymentHistory);
    }

    @Override
    public List<PaymentHistory> findAllByUserId(Long userId) {
        return paymentHistoryJpaRepository.findAllByUserId(userId);
    }
}
