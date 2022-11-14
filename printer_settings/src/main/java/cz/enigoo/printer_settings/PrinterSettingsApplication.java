package cz.enigoo.printer_settings;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PrinterSettingsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PrinterSettingsApplication.class.getResource("printer_settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ENIGOO - Konfigurace tisku");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);

        stage.getIcons().addAll(new Image(this.getClass().getResource("logo-E.png").toString()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}