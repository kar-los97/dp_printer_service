package cz.upce.spring.terminal_service.dto;

import java.util.List;

/**
 * Data transfer object for result of pay
 */
public class ResultDto {
    private String type;
    private String status;

    private List<String> recipeMerchant;
    private List<String> recipeCustomer;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getRecipeMerchant() {
        return recipeMerchant;
    }

    public void setRecipeMerchant(List<String> recipeMerchant) {
        this.recipeMerchant = recipeMerchant;
    }

    public List<String> getRecipeCustomer() {
        return recipeCustomer;
    }

    public void setRecipeCustomer(List<String> recipeCustomer) {
        this.recipeCustomer = recipeCustomer;
    }
}
