package cz.upce.api.printer_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PrintServiceTest {

    private PrintService printService;

    @BeforeEach
    public void setUp(){
        printService = new PrintService();
    }

    @Test
    public void testPrint(){

    }

    @Test
    public void testCheckPrinters(){

    }
}
