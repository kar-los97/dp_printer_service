package cz.enigoo.printer_settings;

import com.google.gson.Gson;
import cz.enigoo.printer_settings.api.Api;
import cz.enigoo.printer_settings.api.Printer;
import cz.enigoo.printer_settings.api.TestPrintData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PrinterSettingsController {

    private final Api api = new Api();
    private ObservableList<String> printerNames;

    @FXML
    protected void initialize() {
        tfBasicPrinterHeight.textProperty().addListener(getListener(tfBasicPrinterHeight));
        tfBasicPrinterWidth.textProperty().addListener(getListener(tfBasicPrinterWidth));
        tfThermoPrinterHeight.textProperty().addListener(getListener(tfThermoPrinterHeight));
        tfThermoPrinterWidth.textProperty().addListener(getListener(tfThermoPrinterWidth));

        setComboBoxs();
        setTextFields();

    }

    @FXML
    public ComboBox<String> cbThermoPrinterName;
    @FXML
    public TextField tfThermoPrinterWidth;
    @FXML
    public TextField tfThermoPrinterHeight;
    @FXML
    public ComboBox<String> cbBasicPrinterName;
    @FXML
    public TextField tfBasicPrinterWidth;
    @FXML
    public TextField tfBasicPrinterHeight;

    @FXML
    public void btnSaveOnAction(ActionEvent actionEvent) throws IOException {
        save();
    }

    @FXML
    public void btnCloseOnAction(ActionEvent actionEvent) {
        close();
    }

    @FXML
    public void menuSaveOnAction(ActionEvent actionEvent) {
        save();
    }

    @FXML
    public void menuCloseOnAction(ActionEvent actionEvent) {
        close();
    }

    @FXML
    public void menuDelSettingsOnAction(ActionEvent actionEvent) {
        delete();
    }

    @FXML
    public void menuAboutOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void btnTestThermoPrintOnAction(ActionEvent actionEvent) throws URISyntaxException {
        testPrint("thermo");
    }

    @FXML
    public void btnTestBasicPrintOnAction(ActionEvent actionEvent) throws URISyntaxException {
        testPrint("basic");
    }

    private ChangeListener<String> getListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    private void setComboBoxs() {
        String[] printerName = api.getPrinterNames();
        if(printerName.length==0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Nedostupné tiskárny");
            alert.setHeaderText("Nebyli nalezeny žádné tiskárny");
            alert.setContentText("V systému nebyli nalezeny žádné tiskárny, aplikace bude ukončena.");
            alert.showAndWait();
            Platform.exit();
        }
        printerNames = FXCollections.observableArrayList(printerName);
        cbBasicPrinterName.setItems(printerNames);
        cbThermoPrinterName.setItems(printerNames);
    }

    private void setTextFields() {
        Printer[] printers = api.getSettings();
        for (Printer printer : printers) {
            switch (printer.getType()) {
                case "THERMO" -> {
                    tfThermoPrinterHeight.setText(String.valueOf(printer.getPageHeight()));
                    tfThermoPrinterWidth.setText(String.valueOf(printer.getPageWidth()));
                    cbThermoPrinterName.getSelectionModel().select(printer.getName());
                }
                case "OTHER" -> {
                    tfBasicPrinterHeight.setText(String.valueOf(printer.getPageHeight()));
                    tfBasicPrinterWidth.setText(String.valueOf(printer.getPageWidth()));
                    cbBasicPrinterName.getSelectionModel().select(printer.getName());
                }
            }
        }
    }

    private void save() {
        if (validate()) {
            Printer[] printers = new Printer[2];
            Printer thermo = new Printer();
            thermo.setType("THERMO");
            thermo.setName(cbThermoPrinterName.getValue());
            thermo.setPageHeight(Long.valueOf(tfThermoPrinterHeight.getText()));
            thermo.setPageWidth(Long.valueOf(tfThermoPrinterWidth.getText()));


            Printer basic = new Printer();
            basic.setType("OTHER");
            basic.setName(cbBasicPrinterName.getValue());
            basic.setPageHeight(Long.valueOf(tfBasicPrinterHeight.getText()));
            basic.setPageWidth(Long.valueOf(tfBasicPrinterWidth.getText()));

            printers[0] = thermo;
            printers[1] = basic;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrďte uložení");
            alert.setHeaderText("Prosím potvrďte uložení.");
            alert.setContentText("Opravdu si přejete uložit toto nastavení tiskáren?");
            Optional<ButtonType> confirm = alert.showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                boolean result = api.saveSettings(printers);
                Alert alertResult = new Alert(result ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alertResult.setTitle("Uložení " + (result ? "proběhlo" : "neproběhlo"));
                alertResult.setHeaderText(result ? "Nastavení bylo uloženo." : "Nastavení se nepodařilo uložit");

                alertResult.showAndWait();
                if (result) {
                    setComboBoxs();
                    setTextFields();
                }
            }
        }
    }

    private boolean validate() {
        StringBuilder sb = new StringBuilder();
        if (tfThermoPrinterWidth.getText().length() == 0) {
            sb.append("Termo tiskárna nemá vyplňenou šířku\n");
        }
        if (tfThermoPrinterWidth.getText().length() > 0 && !isNumber(tfThermoPrinterWidth.getText())) {
            sb.append("Termo tiskárna nemá validní šířku\n");
        }
        if (tfThermoPrinterHeight.getText().length() == 0) {
            sb.append("Termo tiskárna nemá vyplňenou výšku\n");
        }
        if (tfThermoPrinterHeight.getText().length() > 0 && !isNumber(tfThermoPrinterWidth.getText())) {
            sb.append("Termo tiskárna nemá validní výšku\n");
        }
        if (tfBasicPrinterWidth.getText().length() == 0) {
            sb.append("Běžná tiskárna nemá vyplňenou šířku\n");
        }
        if (tfBasicPrinterWidth.getText().length() > 0 && !isNumber(tfBasicPrinterWidth.getText())) {
            sb.append("Běžná tiskárna nemá validní šířku\n");
        }
        if (tfBasicPrinterHeight.getText().length() == 0) {
            sb.append("Běžná tiskárna nemá vyplňenou výšku\n");
        }
        if (tfBasicPrinterHeight.getText().length() > 0 && !isNumber(tfThermoPrinterWidth.getText())) {
            sb.append("Běžná tiskárna nemá validní výšku\n");
        }
        if(cbThermoPrinterName.getValue()==null){
            sb.append("Vyberte prosím název termo tiskárny");
        }if(cbBasicPrinterName.getValue()==null){
            sb.append("Vyberte prosím název běžné tiskárny");
        }
        if (cbThermoPrinterName.getValue().equals(cbBasicPrinterName.getValue())) {
            sb.append("Jedna tiskárna nemůže být nastavena oběma typům");
        }
        if (sb.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Špatně zadaná data");
            alert.setHeaderText("Některá z dat nejsou validní");
            alert.setContentText(sb.toString());

            alert.showAndWait();
        }
        return true;

    }

    private void close() {
        Platform.exit();
    }

    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrďte odstranění");
        alert.setHeaderText("Prosím potvrďte zda chce smazat nastavení.");
        alert.setContentText("Opravdu si přejete SMAZAT všechna nastavení tiskáren?");
        Optional<ButtonType> confirm = alert.showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            api.deleteSettings();
            tfThermoPrinterHeight.setText("");
            tfThermoPrinterWidth.setText("");
            tfBasicPrinterHeight.setText("");
            tfBasicPrinterWidth.setText("");
            cbThermoPrinterName.getSelectionModel().select(null);
            cbBasicPrinterName.getSelectionModel().select(null);
        }
    }

    private boolean isNumber(String content) {
        try {
            Long.valueOf(content);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void testPrint(String type) throws URISyntaxException {
        TestPrintData data = null;

        try {
            switch (type) {
                case "basic" -> data = readFile("test_print_basic.json");
                case "thermo" -> data = readFile("test_print_thermo.json");

            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba při tisku");
            alert.setHeaderText("Nepodařilo se vytisknout zkušební stránku.");
            alert.setContentText("Nastala chyba při pokusu o tisk zkušební stránky");
            alert.showAndWait();
        }
        if (data != null) {
            boolean result = api.testPrint(data);
            Alert alert = new Alert(result ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText(result ? "Tisk proběhl" : "Tisk neproběhl");
            alert.setTitle(result ? "V pořádku" : "Chyba");
            alert.setHeaderText(result ? "Tisk v pořádku" : "Tisk není v pořádku");
            alert.showAndWait();
        }
    }

    private TestPrintData readFile(String filePath) throws IOException {
        String fileInString = Files.readString(Path.of(filePath));
        Gson gson = new Gson();
        TestPrintData data = gson.fromJson(fileInString, TestPrintData.class);
        return data;

    }


}
