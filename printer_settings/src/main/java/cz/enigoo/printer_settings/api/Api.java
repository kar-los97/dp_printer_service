package cz.enigoo.printer_settings.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class Api {


    public Printer [] getSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject("http://localhost:8080/api/v1/settings", Printer[].class);
        }catch (RestClientException ex){
            return new Printer[0];
        }
    }

    public String[] getPrinterNames(){
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject("http://localhost:8080/api/v1/settings/printer", String[].class);
        }catch (Exception ex){
            return new String[0];
        }
    }

    public boolean saveSettings(Printer[] printers){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<?> response = restTemplate.postForEntity("http://localhost:8080/api/v1/settings", printers, Printer[].class);
            return response.getStatusCode().equals(HttpStatus.OK);
        }catch (Exception ex){
            return false;
        }
    }

    public boolean deleteSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.delete("http://localhost:8080/api/v1/settings");
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    public boolean testPrint(TestPrintData data) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForLocation(new URI("http://localhost:8080/api/v1/print"), data);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
