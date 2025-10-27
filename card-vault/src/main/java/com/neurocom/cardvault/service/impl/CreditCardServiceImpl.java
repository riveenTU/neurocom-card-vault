package com.neurocom.cardvault.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neurocom.cardvault.dto.CreditCardTrimmedDTO;
import com.neurocom.cardvault.entity.CreditCard;
import com.neurocom.cardvault.exception.DuplicatePANException;
import com.neurocom.cardvault.exception.InvalidPANException;
import com.neurocom.cardvault.exception.NoCardDetialsException;
import com.neurocom.cardvault.repository.CreditCardRepository;
import com.neurocom.cardvault.service.CreditCardService;
import com.neurocom.cardvault.utility.CreditCardUtility;

@Service
public class CreditCardServiceImpl implements CreditCardService{
    CreditCardRepository creditCardRepository;
    @Value("${creditcard.encryption.key}")
    private String encryptionKeyString;

    @Value("${creditcard.encryption.algorithm}")
    private String encryptionAlgorithm;

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public void addCard(String cardHolderName, String pan) {
        final String encryptedPAN;
        final String panNumber;
        final CreditCard creditCard;
        final String panLast4Digits;
        validateAddCardInputs(cardHolderName, pan);
        panNumber = StringUtils.getDigits(pan);
        validatePAN(panNumber);
        validatePANAlreadyExists(panNumber);
        creditCard = new CreditCard();
        creditCard.setCardHolderName(cardHolderName);
        try {
            encryptedPAN = CreditCardUtility.encrypt(panNumber, encryptionKeyString, encryptionAlgorithm);
        } catch (Exception e) {
            throw new RuntimeException("Encryption Error",e);
        }
        creditCard.setPan(encryptedPAN);
        panLast4Digits = CreditCardUtility.panLast4DigitHash(panNumber);
        creditCard.setPanLastFourDigits(panLast4Digits);
        creditCard.setCreatedDate(new Date());
        creditCardRepository.save(creditCard);
    }

    private void validatePANAlreadyExists(String panNumber) {
        final String hashedLast4DigitPAN = CreditCardUtility.panLast4DigitHash(panNumber);
        long matchCount = creditCardRepository.findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true)
                            .stream()
                .filter(card -> {
                    try {
                        return panNumber.equals(
                                            CreditCardUtility.decrypt(
                                        card.getPan(), encryptionKeyString,
                                        encryptionAlgorithm));
                    } catch (Exception e) {
                        throw new RuntimeException("Dycription failed", e);
                    }
                })
                            .count();

        if (matchCount > 0) {
            throw new DuplicatePANException();
        }
    }

    private void validatePAN(final String panNumber) {
        if (!CreditCardUtility.isValidCreditCard(panNumber)) {
            throw new InvalidPANException();
        }
    }

    private void validateAddCardInputs(String cardHolderName, String pan) {
        if (StringUtils.isBlank(cardHolderName))
            throw new NoCardDetialsException("Card Holder Name");
        if (StringUtils.isBlank(pan))
            throw new NoCardDetialsException("PAN");
    }

    public List<CreditCardTrimmedDTO> findCards(String last4DigitPAN) {
        final String hashedLast4DigitPAN = CreditCardUtility.panLast4DigitHash(last4DigitPAN);
        return creditCardRepository.findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true)
                .stream().map(creditCard -> {
                    try {
                        return new CreditCardTrimmedDTO(
                                creditCard.getId(),creditCard.getCardHolderName()
                                , CreditCardUtility.maskPan(
                                    CreditCardUtility.decrypt(creditCard.getPan(), encryptionKeyString, encryptionAlgorithm)
                                )
                                , creditCard.getCreatedDate());
                    } catch (Exception e) {
                        throw new RuntimeException("Decryption Error",e);
                    }
                }).toList();
    }

}
