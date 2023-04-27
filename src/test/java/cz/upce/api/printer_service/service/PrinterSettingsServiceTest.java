package cz.upce.api.printer_service.service;


import cz.upce.api.error.SettingsException;
import cz.upce.api.printer_service.dto.PrinterDto;
import cz.upce.api.printer_service.entity.Printer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PrinterSettingsServiceTest {

    private PrinterSettingsService printerSettingsService;

    private void setDefaultSettings() throws SettingsException {
        Set<PrinterDto> settings = new HashSet<>();
        PrinterDto printer = new PrinterDto();
        printer.setName("TestThermo");
        printer.setPageWidth(60L);
        printer.setPageHeight(101L);
        printer.setType("THERMO");
        settings.add(printer);
        printer = new PrinterDto();
        printer.setName("TestOther");
        printer.setPageWidth(210L);
        printer.setPageHeight(297L);
        printer.setType("OTHER");
        settings.add(printer);
        printerSettingsService.changeSettings(settings);
    }

    @BeforeEach
    void setUp() throws SettingsException {
        printerSettingsService = new PrinterSettingsService();
        setDefaultSettings();
    }

    @Test
    public void testGetSettings() throws Exception {
        List<Printer> settings = printerSettingsService.getPrinterSettings();
        Assertions.assertEquals(2,settings.size());
        for(Printer p:settings){
            switch (p.getType()){
                case THERMO -> {
                    Assertions.assertEquals("TestThermo",p.getName());
                    Assertions.assertEquals(60L,p.getPageWidth());
                    Assertions.assertEquals(101L,p.getPageHeight());
                }
                case OTHER -> {
                    Assertions.assertEquals("TestOther",p.getName());
                    Assertions.assertEquals(210L,p.getPageWidth());
                    Assertions.assertEquals(297L,p.getPageHeight());
                }
            }
        }


    }

    @Test
    public void testChangeSettings() {
        Set<PrinterDto> settings = new HashSet<>();
        PrinterDto printer = new PrinterDto();
        printer.setName("TestThermo2");
        printer.setPageWidth(62L);
        printer.setPageHeight(102L);
        printer.setType("THERMO");
        settings.add(printer);
        printer = new PrinterDto();
        printer.setName("TestOther2");
        printer.setPageWidth(211L);
        printer.setPageHeight(298L);
        printer.setType("OTHER");
        settings.add(printer);

        Assertions.assertDoesNotThrow(()->{
            List<Printer> actualSettings = printerSettingsService.changeSettings(settings);
            for(Printer p:actualSettings){
                switch (p.getType()){
                    case THERMO -> {
                        Assertions.assertEquals("TestThermo2",p.getName());
                        Assertions.assertEquals(62L,p.getPageWidth());
                        Assertions.assertEquals(102L,p.getPageHeight());
                    }
                    case OTHER -> {
                        Assertions.assertEquals("TestOther2",p.getName());
                        Assertions.assertEquals(211L,p.getPageWidth());
                        Assertions.assertEquals(298L,p.getPageHeight());
                    }
                }
            }
        });


    }

    @Test
    public void testDeleteSettings() throws Exception {
        boolean result = printerSettingsService.deleteSettings();
        Assertions.assertTrue(result);
        Assertions.assertThrowsExactly(SettingsException.class, () -> printerSettingsService.getPrinterSettings());
    }
}
