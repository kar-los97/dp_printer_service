package cz.upce.spring.terminal_service.terminalutils;


import cz.upce.spring.terminal_service.dto.ResultDto;

import java.util.ArrayList;
import java.util.List;

public class ConvertedResponse {


    private boolean isDone = false;

    private boolean wantTicket = false;

    private List<String> merchantRecipe = new ArrayList<>();

    private List<String> customerRecipe = new ArrayList<>();


    public ConvertedResponse(ArrayList<String> block) {
        if (block.size() > 0) {
            this.setDone(true);
        }
    }

    public List<String> getRecipes() {
        return new ArrayList<>();
    }

    public boolean wantNext() {
        return false;
    }

    public boolean isWantTicket() {
        return wantTicket;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }


    public void setMerchantRecipe(List<String> merchantRecipe) {
        this.merchantRecipe = merchantRecipe;
    }

    public void setCustomerRecipe(List<String> customerRecipe) {
        this.customerRecipe = customerRecipe;
    }

    public ResultDto toResultDto() {
        ResultDto dto = new ResultDto();
        dto.setRecipeCustomer(this.customerRecipe);
        dto.setRecipeMerchant(this.merchantRecipe);


        return dto;
    }
}
