package cz.upce.gui;

import com.google.gson.Gson;
import cz.upce.gui.api.printer.ApiPrinter;
import cz.upce.gui.api.printer.Printer;
import cz.upce.gui.api.printer.TestPrintData;
import cz.upce.gui.api.terminal.ApiTerminal;
import cz.upce.gui.api.terminal.Terminal;
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
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.Enumeration;
import java.util.Optional;

/**
 * FXML Controller for settings GUI
 */
public class SettingsController {

    //Fields for printers
    @FXML
    public ComboBox<String> cbThermoPrinterName;
    @FXML
    public TextField tfThermoPrinterWidth;
    @FXML
    public TextField tfThermoPrinterHeight;
    @FXML
    public ComboBox<String> cbOtherPrinterName;
    @FXML
    public TextField tfOtherPrinterWidth;
    @FXML
    public TextField tfOtherPrinterHeight;

    //Fields for terminal
    @FXML
    public TextField tfTerminalIp;
    @FXML
    public TextField tfTerminalPort;
    @FXML
    public TextField tfTerminalId;
    @FXML
    public TextField tfPcIp;

    @FXML
    public TextField tfPcMac;


    /**
     * Init method, init components
     */
    @FXML
    protected void initialize() {
        tfOtherPrinterHeight.textProperty().addListener(getListener(tfOtherPrinterHeight));
        tfOtherPrinterWidth.textProperty().addListener(getListener(tfOtherPrinterWidth));
        tfThermoPrinterHeight.textProperty().addListener(getListener(tfThermoPrinterHeight));
        tfThermoPrinterWidth.textProperty().addListener(getListener(tfThermoPrinterWidth));
        setPrinterComboBoxes();
        setPrinterTextFields();
        setTerminalSettings();

    }

    /**
     * On action method for button Save Printers settings
     *
     * @param actionEvent
     */
    @FXML
    public void btnSavePrinterOnAction(ActionEvent actionEvent) {
        savePrinter();
    }

    /**
     * On action method for buttons Close
     *
     * @param actionEvent
     */
    @FXML
    public void btnCloseOnAction(ActionEvent actionEvent) {
        close();
    }

    /**
     * On action method for menu save button
     *
     * @param actionEvent
     */
    @FXML
    public void menuSaveOnAction(ActionEvent actionEvent) {
        savePrinter();
    }

    /**
     * On action method for close menu button
     *
     * @param actionEvent
     */
    @FXML
    public void menuCloseOnAction(ActionEvent actionEvent) {
        close();
    }

    /**
     * On action method for exit menu button
     *
     * @param actionEvent
     */
    @FXML
    public void menuExitOnAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * On action method for delete settings menu button
     *
     * @param actionEvent
     */
    @FXML
    public void menuDelSettingsOnAction(ActionEvent actionEvent) {
        delete();
    }

    /**
     * On action method for about menu button
     *
     * @param actionEvent
     */
    @FXML
    public void menuAboutOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("O aplikaci");
        alert.setHeaderText("Aplikace Cashier service");
        alert.setContentText("Tato aplikace slouží pro komunikaci mezi pokladní webovou aplikací a platebním terminálem nebo tiskárnami." +
                "Pokud je tato aplikace spuštěna je umožněno z pokladní aplikace přímo odesílat vstupenky/faktury do tiskárny" +
                " a je umožněno odesílat přímo platby/vratky do platebního terminálu.\n\n" +
                "©"+Year.now().getValue() + " Bc. Karel Andres ");

        alert.showAndWait();

    }

    /**
     * On action method for test thermo print button
     *
     * @param actionEvent
     */
    @FXML
    public void btnTestThermoPrintOnAction(ActionEvent actionEvent) {
        testPrint("THERMO");
    }

    /**
     * On action method for test other print button
     *
     * @param actionEvent
     */
    @FXML
    public void btnTestOtherPrintOnAction(ActionEvent actionEvent) {
        testPrint("OTHER");
    }

    /**
     * On action method for terminal test connection button
     *
     * @param actionEvent
     */
    @FXML
    public void btnTerminalTestOnAction(ActionEvent actionEvent) {
        testTerminal();
    }

    /**
     * On action method for terminal save settings button
     *
     * @param actionEvent
     */
    @FXML
    public void btnTerminalSaveOnAction(ActionEvent actionEvent) {
        saveTerminal();
    }

    /**
     * Create listner for number TextField
     *
     * @param textField
     * @return Listener (accept only numbers)
     */
    private ChangeListener<String> getListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    /**
     * Help method to set printer comboboxes (get names from api and set it to combo boxes)
     */
    private void setPrinterComboBoxes() {
        String[] printerName = ApiPrinter.getInstance().getPrinterNames();
        ObservableList<String> printerNames = FXCollections.observableArrayList(printerName);
        cbOtherPrinterName.setItems(printerNames);
        cbThermoPrinterName.setItems(printerNames);
    }

    /**
     * Help method to set printer text fields (get actual settings from api and set it to text boxes)
     */
    private void setPrinterTextFields() {
        Printer[] printers = ApiPrinter.getInstance().getSettings();
        for (Printer printer : printers) {
            switch (printer.getType()) {
                case "THERMO" -> {
                    tfThermoPrinterHeight.setText(String.valueOf(printer.getPageHeight()));
                    tfThermoPrinterWidth.setText(String.valueOf(printer.getPageWidth()));
                    cbThermoPrinterName.getSelectionModel().select(printer.getName());
                }
                case "OTHER" -> {
                    tfOtherPrinterHeight.setText(String.valueOf(printer.getPageHeight()));
                    tfOtherPrinterWidth.setText(String.valueOf(printer.getPageWidth()));
                    cbOtherPrinterName.getSelectionModel().select(printer.getName());
                }
            }
        }

    }

    /**
     * Help method to save printer settings (get settings from fields and send it to api)
     */
    private void savePrinter() {
        if (validatePrinter()) {
            Printer[] printers = new Printer[2];
            Printer thermo = new Printer();
            thermo.setType("THERMO");
            thermo.setName(cbThermoPrinterName.getValue());
            thermo.setPageHeight(Long.valueOf(tfThermoPrinterHeight.getText()));
            thermo.setPageWidth(Long.valueOf(tfThermoPrinterWidth.getText()));


            Printer other = new Printer();
            other.setType("OTHER");
            other.setName(cbOtherPrinterName.getValue());
            other.setPageHeight(Long.valueOf(tfOtherPrinterHeight.getText()));
            other.setPageWidth(Long.valueOf(tfOtherPrinterWidth.getText()));

            printers[0] = thermo;
            printers[1] = other;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrďte uložení");
            alert.setHeaderText("Prosím potvrďte uložení.");
            alert.setContentText("Opravdu si přejete uložit toto nastavení tiskáren?");
            Optional<ButtonType> confirm = alert.showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                boolean result = ApiPrinter.getInstance().saveSettings(printers);
                Alert alertResult = new Alert(result ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alertResult.setTitle("Uložení " + (result ? "proběhlo" : "neproběhlo"));
                alertResult.setHeaderText(result ? "Nastavení bylo uloženo." : "Nastavení se nepodařilo uložit");

                alertResult.showAndWait();
                if (result) {
                    setPrinterComboBoxes();
                    setPrinterTextFields();
                }
            }
        }
    }

    /**
     * Helpful method for validate printers fields
     *
     * @return true if valid, false if not valid
     */
    private boolean validatePrinter() {
        StringBuilder sb = new StringBuilder();
        if (tfThermoPrinterWidth.getText().length() > 0 || tfThermoPrinterHeight.getText().length()>0 || cbThermoPrinterName.getValue()!=null) {
            if(tfThermoPrinterWidth.getText().length()==0) {
                sb.append("Termo tiskárna nemá vyplňenou šířku\n");
            }
            if (tfThermoPrinterWidth.getText().length() > 0 && isNotNumber(tfThermoPrinterWidth.getText())) {
                sb.append("Termo tiskárna nemá validní šířku\n");
            }
            if (tfThermoPrinterHeight.getText().length() == 0) {
                sb.append("Termo tiskárna nemá vyplňenou výšku\n");
            }
            if (tfThermoPrinterHeight.getText().length() > 0 && isNotNumber(tfThermoPrinterWidth.getText())) {
                sb.append("Termo tiskárna nemá validní výšku\n");
            }
            if (cbThermoPrinterName.getValue() == null) {
                sb.append("Vyberte prosím název termo tiskárny");
            }
        }

        if (tfOtherPrinterWidth.getText().length() > 0 || tfOtherPrinterHeight.getText().length()>0 || cbOtherPrinterName.getValue()!=null) {
            if (tfOtherPrinterWidth.getText().length() == 0) {
                sb.append("Běžná tiskárna nemá vyplňenou šířku\n");
            }
            if (tfOtherPrinterWidth.getText().length() > 0 && isNotNumber(tfOtherPrinterWidth.getText())) {
                sb.append("Běžná tiskárna nemá validní šířku\n");
            }
            if (tfOtherPrinterHeight.getText().length() == 0) {
                sb.append("Běžná tiskárna nemá vyplňenou výšku\n");
            }
            if (tfOtherPrinterHeight.getText().length() > 0 && isNotNumber(tfThermoPrinterWidth.getText())) {
                sb.append("Běžná tiskárna nemá validní výšku\n");
            }
            if (cbOtherPrinterName.getValue() == null) {
                sb.append("Vyberte prosím název běžné tiskárny");
            }
        }
        if (cbThermoPrinterName.getValue().equals(cbOtherPrinterName.getValue())) {
            sb.append("Jedna tiskárna nemůže být nastavena oběma typům");
        }
        if (sb.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Špatně zadaná data");
            alert.setHeaderText("Některá z dat nejsou validní");
            alert.setContentText(sb.toString());

            alert.showAndWait();
            return false;
        }
        return true;

    }

    /**
     * Method to close the application
     */
    private void close() {
        Platform.exit();
    }

    /**
     * Method to delete all settings (for printers and for terminal)
     */
    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrďte odstranění");
        alert.setHeaderText("Prosím potvrďte zda chce smazat nastavení.");
        alert.setContentText("Opravdu si přejete SMAZAT všechna nastavení tiskáren i terminálu?");
        Optional<ButtonType> confirm = alert.showAndWait();
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            if (ApiPrinter.getInstance().deleteSettings() && ApiTerminal.getInstance().deleteSettings()) {
                tfThermoPrinterHeight.setText("");
                tfThermoPrinterWidth.setText("");
                tfOtherPrinterHeight.setText("");
                tfOtherPrinterWidth.setText("");
                cbThermoPrinterName.getSelectionModel().select(null);
                cbOtherPrinterName.getSelectionModel().select(null);

                tfTerminalIp.setText("");
                tfTerminalId.setText("");
                tfTerminalPort.setText("");
            } else {
                Alert alertErr = new Alert(Alert.AlertType.ERROR);
                alertErr.setTitle("Chyba při odstraňování");
                alertErr.setHeaderText("Nastala chyba při odstraňování nastavení");
                alertErr.setContentText("Zkuste to znovu");
                alertErr.showAndWait();
            }
        }
    }

    /**
     * Help method to test if string is number
     *
     * @param content string to test
     * @return true if string is number, false if not a number
     */
    private boolean isNotNumber(String content) {
        try {
            Long.valueOf(content);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    /**
     * Method to send test print to api
     *
     * @param type type of printer (OTHER, THERMO)
     */
    private void testPrint(String type) {
        TestPrintData data = null;

        try {
            switch (type) {
                case "OTHER" -> data = readFile("cfg/test_print_basic.json");
                case "THERMO" -> data = readFile("cfg/test_print_thermo.json");

            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba při tisku");
            alert.setHeaderText("Nepodařilo se vytisknout zkušební stránku.");
            alert.setContentText("Nastala chyba při pokusu o tisk zkušební stránky");
            alert.showAndWait();
        }
        if (data != null) {
            boolean result = ApiPrinter.getInstance().testPrint(data);
            Alert alert = new Alert(result ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setContentText(result ? "Tisk proběhl" : "Tisk neproběhl");
            alert.setTitle(result ? "V pořádku" : "Chyba");
            alert.setHeaderText(result ? "Tisk v pořádku" : "Tisk není v pořádku");
            alert.showAndWait();
        }
    }

    /**
     * Method to read a file
     *
     * @param filePath path to file
     * @return read data from file
     * @throws IOException if some error with reading file
     */
    private TestPrintData readFile(String filePath) throws IOException {
        String fileInString = Files.readString(Path.of(filePath));
        Gson gson = new Gson();
        TestPrintData data = gson.fromJson(fileInString, TestPrintData.class);
        return data;
    }

    /**
     * Help method to set terminal settings
     */
    private void setTerminalSettings() {
        Terminal terminal = ApiTerminal.getInstance().getSettings();
        tfTerminalPort.setText(String.valueOf(terminal.getPort()));
        tfTerminalId.setText(terminal.getId());
        tfTerminalIp.setText(terminal.getIp());
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while(ifaces.hasMoreElements()){
                NetworkInterface element = ifaces.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();
                while(addresses.hasMoreElements() && element.getHardwareAddress()!=null && element.getHardwareAddress().length>0){
                    InetAddress ip = addresses.nextElement();
                    if(ip instanceof Inet4Address && ip.isSiteLocalAddress()){
                        tfPcIp.setText(ip.getHostAddress());
                        tfPcMac.setText(getMacAddress(ip));
                    }
                }
            }
        }catch (Exception ex){
        }
    }

    private String getMacAddress(InetAddress ip) {
        String address = null;
        try {

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            address = sb.toString();

        } catch (SocketException ex) {
        }

        return address;
    }

    /**
     * Method to save terminal setting (get attributes from fields and send it to api)
     */
    private void saveTerminal() {
        if (validateTerminal()) {
            Terminal terminal = new Terminal();
            terminal.setType("default");
            terminal.setIp(tfTerminalIp.getText());
            terminal.setPort(Integer.parseInt(tfTerminalPort.getText()));
            terminal.setId(tfTerminalId.getText());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrďte uložení");
            alert.setHeaderText("Prosím potvrďte uložení.");
            alert.setContentText("Opravdu si přejete uložit toto nastavení platebního terminálu?");
            Optional<ButtonType> confirm = alert.showAndWait();
            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                boolean result = ApiTerminal.getInstance().saveSettings(terminal);
                Alert alertResult = new Alert(result ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alertResult.setTitle("Uložení " + (result ? "proběhlo" : "neproběhlo"));
                alertResult.setHeaderText(result ? "Nastavení bylo uloženo." : "Nastavení se nepodařilo uložit");

                alertResult.showAndWait();
                if (result) {
                    setPrinterComboBoxes();
                    setPrinterTextFields();
                }
            }
        }
    }

    /**
     * Validate terminal settings fields
     *
     * @return true if valid, false if not valid
     */
    private boolean validateTerminal() {
        StringBuilder sb = new StringBuilder();
        if (tfTerminalIp.getText().equals("")) {
            sb.append("Není vyplněna IP adresa terminálu\n");
        } else {
            if (!InetAddressValidator.getInstance().isValid(tfTerminalIp.getText())) {
                sb.append("IP adresa není validní\n");
            }
        }
        if (tfTerminalPort.getText().equals("")) {
            sb.append("Není vyplněn port terminálu\n");
        } else {
            try {
                Integer.parseInt(tfTerminalPort.getText());
            } catch (NumberFormatException ex) {
                sb.append("Port terminálu není ve správném formátu\n");
            }
        }
        if (tfTerminalId.getText().equals("")) {
            sb.append("Není vyplněno ID terminálu\n");
        }

        if (sb.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Špatně zadaná data");
            alert.setHeaderText("Některá z dat nejsou validní");
            alert.setContentText(sb.toString());

            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Method to test if terminal is connected (send request to api and evaluate result)
     */
    private void testTerminal() {
        boolean isConnected = ApiTerminal.getInstance().testConnection();
        Alert alert = new Alert(isConnected ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Test spojení platebního terminálu");
        alert.setHeaderText(isConnected ? "Spojení je v pořádku" : "Spojení není navázáno");
        alert.setContentText(isConnected ? "" : "Zkontrolujte nastavení platebního terminálu");

        alert.showAndWait();
    }
}
