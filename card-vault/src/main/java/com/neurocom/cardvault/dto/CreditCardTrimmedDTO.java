package com.neurocom.cardvault.dto;

import java.util.Date;

public record CreditCardTrimmedDTO(
    Long id,
    String cardHolderName,
    String maskedPan,
    Date createdDate
) {
    
}
