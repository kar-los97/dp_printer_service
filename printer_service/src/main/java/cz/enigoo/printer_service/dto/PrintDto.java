package cz.enigoo.printer_service.dto;

import java.util.List;

/**
 * Data transfer object for accepting request to print data
 */
public class PrintDto {

    private List<String> data;

    private String type;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
