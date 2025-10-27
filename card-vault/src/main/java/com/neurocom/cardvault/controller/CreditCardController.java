package com.neurocom.cardvault.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.neurocom.cardvault.dto.CreditCardViewModel;
import com.neurocom.cardvault.exception.DuplicatePANException;
import com.neurocom.cardvault.exception.InvalidPANException;
import com.neurocom.cardvault.exception.NoCardDetialsException;
import com.neurocom.cardvault.service.CreditCardService;

@Controller
public class CreditCardController {
    CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }
    
    @GetMapping("/addCard")
    public String addNewCreditCard(Model model) {
        CreditCardViewModel creditCardViewModel = new CreditCardViewModel();
        model.addAttribute("creditCardViewModel", creditCardViewModel);
        return "add-card";
    }

    @PostMapping("/save")
    public String saveCreditCard(@ModelAttribute("creditCardViewModel") CreditCardViewModel creditCardViewModel,
            Model model) {
        try {
            creditCardService.addCard(creditCardViewModel.getCardHolderName(),
                creditCardViewModel.getPan());
        } catch (DuplicatePANException | InvalidPANException | NoCardDetialsException e) {
            creditCardViewModel.setErrorMessage("Needs Attention: "+e.getMessage());
            model.addAttribute("creditCardViewModel", creditCardViewModel);
            return "add-card";
        }
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchCards(Model model) {
        CreditCardViewModel creditCardViewModel = new CreditCardViewModel();
        model.addAttribute("creditCardViewModel", creditCardViewModel);
        return "search-cards";
    }

    @PostMapping("/searchByPan")
    public String searchByPANLastFourDigits(
            @ModelAttribute("creditCardViewModel") CreditCardViewModel creditCardViewModel,
             Model model) {
        //try {
        model.addAttribute("cardList",creditCardService.findCards(creditCardViewModel.getPan()));
        model.addAttribute("creditCardViewModel", creditCardViewModel);
        return "search-cards";
    }

}
