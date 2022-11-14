package cz.enigoo.printer_settings.api;

public class Printer {

    private String name;

    private Long pageHeight;

    private Long pageWidth;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(Long pageHeight) {
        this.pageHeight = pageHeight;
    }

    public Long getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(Long pageWidth) {
        this.pageWidth = pageWidth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
