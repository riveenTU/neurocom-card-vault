package com.neurocom.cardvault.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurocom.cardvault.dto.CreditCardViewModel;
import com.neurocom.cardvault.service.CreditCardService;

@RestController
@RequestMapping("/creditcards")
public class CreditCardRestController {
    CreditCardService creditCardService;

    public CreditCardRestController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping("/")
    public void addCreditCard(@RequestBody CreditCardViewModel creditCardViewModel) {
        creditCardService.addCard(creditCardViewModel.getCardHolderName(),
                creditCardViewModel.getPan());
    }

    @GetMapping("/{panLastFourDigits}")
    public List<CreditCardViewModel> findCreditCard(@PathVariable String panLastFourDigits) {
        return creditCardService.findCards(panLastFourDigits)
                .stream().map(card -> {
                    CreditCardViewModel vm = new CreditCardViewModel();
                    vm.setId(card.id());
                    vm.setCardHolderName(card.cardHolderName());
                    vm.setMaskedPan(card.maskedPan());
                    vm.setCreatedDate(card.createdDate());
                    return vm;
                }).toList();
    }

}
