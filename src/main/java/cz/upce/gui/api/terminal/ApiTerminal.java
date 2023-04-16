package cz.upce.gui.api.terminal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


/**
 * Class to communicate from GUI with Rest-Api for terminal
 */
public class ApiTerminal {

    /**
     * Api URL
     */
    private final String URL = "http://localhost:8082/api/v1/terminal/settings";

    /**
     * Singleton instance of ApiTerminal
     */
    private static ApiTerminal instance = null;

    /**
     * Private constructor, you can access to ApiTerminal only view getInstance method
     */
    private ApiTerminal(){

    }

    /**
     * Method to access to ApiTerminal and their method
     * @return instance of ApiTerminal
     */
    public static ApiTerminal getInstance() {
        if(instance==null){
            instance = new ApiTerminal();
        }
        return instance;
    }

    /**
     * Get actual settings of terminal
     * @return Terminal instance of that is actually saved in config
     */
    public Terminal getSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.getForObject(URL, Terminal.class);
        }catch (RestClientException ex){
            return new Terminal();
        }
    }

    /**
     * Save settings from GUI to api
     * @param terminal terminal settings
     * @return true if OK, false if not OK
     */
    public boolean saveSettings(Terminal terminal){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<?> response = restTemplate.postForEntity(URL, terminal, Terminal.class);
            return response.getStatusCode().equals(HttpStatus.OK);
        }catch (Exception ex){
            return false;
        }
    }

    /**
     * Delete all settings of terminal
     * @return true if OK, false if not OK
     */
    public boolean deleteSettings() {
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.delete(URL);
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    /**
     * Test connection with terminal
     * @return true if connected, false if not connected
     */
    public boolean testConnection() {
            RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<?> response = restTemplate.getForEntity("http://localhost:8082/api/v1/terminal/test",String.class);
            return response.getStatusCode().equals(HttpStatus.OK)&& Objects.equals(response.getBody(), "OK");
        }catch (Exception ex){
            return false;
        }
    }
}
