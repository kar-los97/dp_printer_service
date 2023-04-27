package cz.upce.api;


import cz.upce.gui.SettingsApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class PrinterServiceApplication {

    public static void main(String[] args) {

       Application.launch(SettingsApplication.class, args);
    }


}
