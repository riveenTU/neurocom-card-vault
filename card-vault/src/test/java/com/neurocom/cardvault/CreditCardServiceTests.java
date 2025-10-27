package com.neurocom.cardvault;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.neurocom.cardvault.dto.CreditCardTrimmedDTO;
import com.neurocom.cardvault.entity.CreditCard;
import com.neurocom.cardvault.exception.DuplicatePANException;
import com.neurocom.cardvault.exception.InvalidPANException;
import com.neurocom.cardvault.exception.NoCardDetialsException;
import com.neurocom.cardvault.repository.CreditCardRepository;
import com.neurocom.cardvault.service.impl.CreditCardServiceImpl;
import com.neurocom.cardvault.utility.CreditCardUtility;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceTests {
    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(creditCardService, "encryptionKeyString", "1234567890123456");
        ReflectionTestUtils.setField(creditCardService, "encryptionAlgorithm", "AES");
    }

    @Test
    void shouldThrowExceptionWhenAddCardWithoutCardHolderName() {
        assertThrows(NoCardDetialsException.class,
                () -> creditCardService.addCard("", "1231"));
    }
    
    @Test
    void shouldThrowExceptionWhenAddCardWithoutPAN() {
        assertThrows(NoCardDetialsException.class,
                () -> creditCardService.addCard("Abc", ""));
    }

    @Test
    void shouldThrowExceptionWhenPANisInvalid() {
        assertThrows(InvalidPANException.class,
                () -> creditCardService.addCard("Abc", "1234"));
    }

    @Test
    void shouldNotThrowExceptionWhenPANisValid() {
        assertDoesNotThrow(
                () -> creditCardService.addCard("Abc", "6011111111111117"));
    }

    @Test
    void shouldNotThrowExceptionWhenPANisHavingCharactersInBetween() {
        assertDoesNotThrow(
                () -> creditCardService.addCard("Abc", "6011-1111-1111-1117"));
        assertDoesNotThrow(
                () -> creditCardService.addCard("Abc", "6011 1111 1111 1117"));
    }
    
    @Test
    void shouldSaveWithTheGivenCardHolderName() {
        String cardHolderName = "Abc";
        creditCardService.addCard(cardHolderName, "6011111111111117");
        ArgumentCaptor<CreditCard> creditCard = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(creditCard.capture());
        assertEquals(cardHolderName,creditCard.getValue().getCardHolderName());
    }

    @Test
    void shouldSaveWithEncryptedPAN() {
        String encryptedPAN = "VQu5Pz8V6vGMxyMtGZOmRgUBh6DN5amHLLqwkatz5VM=";
        creditCardService.addCard("Abc", "6011111111111117");
        ArgumentCaptor<CreditCard> creditCard = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(creditCard.capture());
        assertEquals(encryptedPAN,creditCard.getValue().getPan());
    }

    @Test
    void shouldSaveWithHashedLast4DigitsOfPAN() {
        String hashedLast4DigitPAN = "0eec27c419d0fe24e53c90338cdc8bc6";
        creditCardService.addCard("Abc", "6011111111111117");
        ArgumentCaptor<CreditCard> creditCard = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(creditCard.capture());
        assertEquals(hashedLast4DigitPAN, creditCard.getValue().getPanLastFourDigits());
    }
    
    //-----
    @Test
    void shouldThrowExceptionWhenPANisDuplicated() {
        final String hashedLast4DigitPAN = "0eec27c419d0fe24e53c90338cdc8bc6";
        final String encryptedPAN = "VQu5Pz8V6vGMxyMtGZOmRgUBh6DN5amHLLqwkatz5VM";
        final List<CreditCard> creditCards = new ArrayList<CreditCard>();
        final CreditCard creditCard = new CreditCard();
        final String cardHolderName = "Abc";
        final Date date = new Date();
        creditCard.setId((long) 1);
        creditCard.setCardHolderName(cardHolderName);
        creditCard.setPan(encryptedPAN);
        creditCard.setCreatedDate(date);
        creditCard.setPanLastFourDigits(hashedLast4DigitPAN);
        creditCards.add(creditCard);
        Mockito.when(creditCardRepository.findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true)).thenReturn(
                creditCards);
        assertThrows(DuplicatePANException.class,
                () -> creditCardService.addCard("Abc", "6011111111111117"));
    }

    @Test
    void shouldSaveWithCurrentDate() {
        creditCardService.addCard("Abc", "6011111111111117");
        ArgumentCaptor<CreditCard> creditCard = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(creditCard.capture());
        assertEquals(
            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDate(),
            creditCard.getValue().getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        );
    }
    
    @Test
    void shouldFindCardsWithHashedLast4DigitsOfPAN() {
        String hashedLast4DigitPAN = "0eec27c419d0fe24e53c90338cdc8bc6";
        creditCardService.findCards("6011111111111117");
        Mockito.verify(creditCardRepository,
                times(1)).findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true);
    }

    @Test
    void shouldFindCardsReturnCreditCardTrimmedDTOWithoutMasekedPAN() {
        final String hashedLast4DigitPAN = "0eec27c419d0fe24e53c90338cdc8bc6";
        final String encryptedPAN = "VQu5Pz8V6vGMxyMtGZOmRgUBh6DN5amHLLqwkatz5VM";
        final List<CreditCard> creditCards = new ArrayList<CreditCard>();
        final CreditCard creditCard = new CreditCard();
        final String cardHolderName = "Abc";
        final Date date = new Date();
        creditCard.setId((long) 1);
        creditCard.setCardHolderName(cardHolderName);
        creditCard.setPan(encryptedPAN);
        creditCard.setCreatedDate(date);
        creditCard.setPanLastFourDigits(hashedLast4DigitPAN);
        creditCards.add(creditCard);
        Mockito.when(creditCardRepository.findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true)).thenReturn(
                creditCards);
        List<CreditCardTrimmedDTO> ccTrimedDTOs = creditCardService.findCards("6011111111111117");
        assertEquals(1, ccTrimedDTOs.size());
        final CreditCardTrimmedDTO ccTrimedDTO = ccTrimedDTOs.get(0);
        assertEquals(cardHolderName, ccTrimedDTO.cardHolderName());
        assertEquals(date, ccTrimedDTO.createdDate());
    }
    
    @Test
    void shouldFindCardsReturnCreditCardTrimmedDTOWithMaskedPAN() {
        final String hashedLast4DigitPAN = "0eec27c419d0fe24e53c90338cdc8bc6";
        final String encryptedPAN = "VQu5Pz8V6vGMxyMtGZOmRgUBh6DN5amHLLqwkatz5VM";
        final List<CreditCard> creditCards = new ArrayList<CreditCard>();
        final CreditCard creditCard = new CreditCard();
        final String cardHolderName = "Abc";
        final Date date = new Date();
        creditCard.setId((long) 1);
        creditCard.setCardHolderName(cardHolderName);
        creditCard.setPan(encryptedPAN);
        creditCard.setCreatedDate(date);
        creditCard.setPanLastFourDigits(hashedLast4DigitPAN);
        creditCards.add(creditCard);
        Mockito.when(creditCardRepository.findByPanLastFourDigitsAndActiveStatus(hashedLast4DigitPAN,true)).thenReturn(
                creditCards);
        List<CreditCardTrimmedDTO> ccTrimedDTOs = creditCardService.findCards("6011111111111117");
        assertEquals(1, ccTrimedDTOs.size());
        final CreditCardTrimmedDTO ccTrimedDTO = ccTrimedDTOs.get(0);
        assertEquals("**** **** **** 1117", ccTrimedDTO.maskedPan());
        assertEquals(cardHolderName, ccTrimedDTO.cardHolderName());
        assertEquals(date, ccTrimedDTO.createdDate());
    }


}
