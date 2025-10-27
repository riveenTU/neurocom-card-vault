package com.neurocom.cardvault.service;

import java.util.List;

import com.neurocom.cardvault.dto.CreditCardTrimmedDTO;

public interface CreditCardService {
    // 
    public void addCard(String cardHolderName, String pan);

    public List<CreditCardTrimmedDTO> findCards(String last4DigitPAN);
    
}