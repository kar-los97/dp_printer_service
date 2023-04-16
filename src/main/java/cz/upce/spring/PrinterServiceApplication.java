package cz.upce.spring;


import cz.upce.gui.SettingsApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
public class PrinterServiceApplication {

    public static void main(String[] args) {

       Application.launch(SettingsApplication.class, args);
    }


}
