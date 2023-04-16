package cz.upce.gui;

import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<SettingsApplication.StageReadyEvent> {
    @Override
    public void onApplicationEvent(SettingsApplication.StageReadyEvent event) {
        Stage stage = event.getStage();

    }
}
