package cz.upce.spring.printer_service.dto;

import java.util.Set;

public class PrinterSettingsDto {

    private Set<PrinterDto> printers;

    public Set<PrinterDto> getPrinters() {
        return printers;
    }

    public void setPrinters(Set<PrinterDto> printers) {
        this.printers = printers;
    }
}
