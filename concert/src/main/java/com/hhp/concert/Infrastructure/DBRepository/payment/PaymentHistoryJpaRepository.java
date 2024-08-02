package com.hhp.concert.Infrastructure.DBRepository.payment;

import com.hhp.concert.Business.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryJpaRepository extends JpaRepository<PaymentHistory, Long> {

    List<PaymentHistory> findAllByUserId(Long userId);
}
