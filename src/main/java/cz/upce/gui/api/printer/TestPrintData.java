package cz.upce.gui.api.printer;

/**
 * Class that represent print data to send to rest api
 */
public class TestPrintData {
    private String[] data;
    private String type;

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
