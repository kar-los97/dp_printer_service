package cz.upce.gui;

import cz.upce.spring.Main;
import cz.upce.spring.PrinterServiceApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Optional;

/**
 * Main class of GUI application
 */
public class SettingsApplication extends Application {

    /**
     * Context of SpringBootApplication
     */
    public static ConfigurableApplicationContext applicationContext;

    /**
     * Init method
     */
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(PrinterServiceApplication.class).run();
    }

    /**
     * Start GUI
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     * @throws IOException if not exist resource to define GUI
     */
    @Override
    public void start(Stage stage) throws IOException {
        applicationContext.publishEvent(new StageReadyEvent(stage));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Pokladní servis (tisk, terminál)");
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);

        stage.getIcons().addAll(new Image(getClass().getClassLoader().getResource("cashier.png").toString()));
        stage.show();

    }

    /**
     * When application was closed
     */
    @Override
    public void stop() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ukončit aplikaci");
        alert.setHeaderText("Opravdu chcete ukončit aplikaci?");
        alert.setContentText("Po ukončení nebude možno tisknout ani odesílat platby na terminál.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            SettingsApplication.applicationContext.close();
            Platform.exit();
            if (Main.serverSocket != null) {
                Main.serverSocket.close();
            }

        }
    }

    /**
     * Main method to launch Application
     *
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}