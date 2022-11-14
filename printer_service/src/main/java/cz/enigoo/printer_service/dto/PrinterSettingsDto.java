package cz.enigoo.printer_service.dto;

import cz.enigoo.printer_service.entity.Printer;

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
