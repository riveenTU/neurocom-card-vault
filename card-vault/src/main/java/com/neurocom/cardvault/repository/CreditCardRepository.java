package com.neurocom.cardvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.neurocom.cardvault.entity.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard,Long> {
    @Query("SELECT c FROM CreditCard c WHERE c.panLastFourDigits = :hashedLast4DigitPAN AND c.active = :active")
    List<CreditCard> findByPanLastFourDigitsAndActiveStatus(String hashedLast4DigitPAN, boolean active);

}
