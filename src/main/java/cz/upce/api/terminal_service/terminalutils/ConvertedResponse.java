package cz.upce.api.terminal_service.terminalutils;


import cz.upce.api.terminal_service.dto.ResultDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents response from terminal transferred
 */
public class ConvertedResponse {

    private boolean isDone = false;

    private boolean wantTicket = false;

    private List<String> merchantRecipe = new ArrayList<>();

    private List<String> customerRecipe = new ArrayList<>();

    public ConvertedResponse(ArrayList<String> block) {
        //Podléhá interním pravidlům bankovní společnosti, proto je zde kód zjednodušen
        if (block.size() > 0) {
            this.setDone(true);
        }
    }

    /**
     * Method to get list of recipes from response
     * @return List of recipes
     */
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

    /**
     * Convert ConvertedResponse to ResultDto
     * @return ResultDto
     */
    public ResultDto toResultDto() {
        ResultDto dto = new ResultDto();
        dto.setType("PAY");
        dto.setStatus("OK");
        dto.setRecipeCustomer(this.customerRecipe);
        dto.setRecipeMerchant(this.merchantRecipe);


        return dto;
    }
}
