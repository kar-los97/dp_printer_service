package cz.enigoo.printer_service.entity;

import cz.enigoo.printer_service.enums.PrinterType;

/**
 * Helpful class to store data about printer at config.json file
 */
public class Printer {

    private String name;

    private Long pageHeight;

    private Long pageWidth;

    private PrinterType type;

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

    public PrinterType getType() {
        return type;
    }

    public void setType(PrinterType type) {
        this.type = type;
    }
}
