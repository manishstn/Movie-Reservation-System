package com.moviereservationsystem.repository;

import com.moviereservationsystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByProviderTransactionId(String transactionId);
    Optional<Payment> findByReservationId(Long reservationId);
}
