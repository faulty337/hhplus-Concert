package com.hhp.concert.Business.service;

import com.hhp.concert.Business.Domain.PaymentHistory;
import com.hhp.concert.Business.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public PaymentHistory addPaymentHistory(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    @Override
    public List<PaymentHistory> getPaymentHistoryList(Long userId) {
        return paymentHistoryRepository.findAllByUserId(userId);
    }
}
