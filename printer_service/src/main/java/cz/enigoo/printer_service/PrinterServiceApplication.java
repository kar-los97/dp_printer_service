package cz.enigoo.printer_service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class PrinterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrinterServiceApplication.class, args);
    }


}
