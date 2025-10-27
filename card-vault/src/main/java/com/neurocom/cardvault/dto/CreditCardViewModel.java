package com.neurocom.cardvault.dto;

import java.util.Date;


public class CreditCardViewModel{
    private Long id;
    private String cardHolderName;
    private String pan;
    private String maskedPan;
    private Date createdDate;
    private String errorMessage;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCardHolderName() {
        return cardHolderName;
    }
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    public String getPan() {
        return pan;
    }
    public void setPan(String pan) {
        this.pan = pan;
    }
    public String getMaskedPan() {
        return maskedPan;
    }
    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
