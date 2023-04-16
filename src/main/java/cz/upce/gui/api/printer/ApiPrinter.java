package cz.upce.gui.api.printer;

import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Class to communicate from GUI with Rest-Api for printer
 */
public class ApiPrinter {

    /**
     * Api URL
     */
    private final String URL = "http://localhost:8082/api/v1/print";

    /**
     * Signleton instance of ApiPrinter
     */
    private static ApiPrinter instance = null;

    /**
     * Private constructor, you can access to Api only view getInstance method
     */
    private ApiPrinter(){

    }

    /**
     * Method to access to Api class and their method
     * @return instance of Api
     */
    public static ApiPrinter getInstance() {
        if(instance==null){
            instance = new ApiPrinter();
        }
        return instance;
    }

    /**
     * Get actual setting of printers
     * @return Array of Printers that is saved in settings
     */
    public Printer [] getSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject(URL+"/settings", Printer[].class);
        }catch (RestClientException ex){
            return new Printer[0];
        }
    }

    /**
     * Return all printers names, that be installed on windows
     * @return Array of printers name
     */
    public String[] getPrinterNames(){
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject(URL+"/settings/printer", String[].class);
        }catch (Exception ex){
            return new String[0];
        }
    }

    /**
     * Save settings from GUI to config file
     * @param printers array of printers settings
     * @return true if OK, false if not OK
     */
    public boolean saveSettings(Printer[] printers){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(URL+"/settings", printers, Printer[].class);
            return response.getStatusCode().equals(HttpStatus.OK);
        }catch (Exception ex){
            return false;
        }
    }

    /**
     * Delete or settings
     * @return true if OK, false if not OK
     */
    public boolean deleteSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.delete(URL+"/settings");
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    /**
     * Send request to print test data
     * @param data data to print
     * @return true if OK, false if not OK
     */
    public boolean testPrint(TestPrintData data) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers=new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity requestEntity = new HttpEntity(data,headers);
            restTemplate.exchange(new URI(URL), HttpMethod.POST,requestEntity,Boolean.class);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
