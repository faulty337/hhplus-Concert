package com.hhp.concert.Infrastructure.payment;

import com.hhp.concert.Business.Domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryJpaRepository extends JpaRepository<PaymentHistory, Long> {

}
