package cz.upce.spring.printer_service.dto;

import java.util.Set;

/**
 * Data transfer object for send set printers
 */
public class PrinterSettingsDto {

    private Set<PrinterDto> printers;

    public Set<PrinterDto> getPrinters() {
        return printers;
    }

    public void setPrinters(Set<PrinterDto> printers) {
        this.printers = printers;
    }
}
