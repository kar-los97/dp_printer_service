package cz.upce.spring.printer_service.utils;

public enum MessageType {

    TRACE("trace"),DEBUG("debug"),INFO("info"),WARN("warn"),ERROR("error");
    public final String value;

    private MessageType(String value){
        this.value = value;
    }
}
