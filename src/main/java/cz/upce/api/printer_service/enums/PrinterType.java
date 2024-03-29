package cz.upce.api.printer_service.enums;

/**
 * Enum of printer types
 */
public enum PrinterType {
    THERMO("thermo"),OTHER("other"),UNKNOWN("unknown");
    public final String value;
    private PrinterType(String value){
        this.value = value;
    }

    public static PrinterType getTypeFromString(String type){
        return switch (type.toLowerCase()) {
            case "thermo" -> THERMO;
            case "other" -> OTHER;
            default -> UNKNOWN;
        };
    }

    @Override
    public String toString() {
        return value;
    }
}
