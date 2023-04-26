package cz.upce.spring.terminal_service.dto;

/**
 * Data transfer object for realize pay at terminal
 */
public class PayDto {
    private String price;

    private String orderId;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
