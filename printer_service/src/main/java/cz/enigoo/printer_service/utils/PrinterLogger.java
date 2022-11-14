package cz.enigoo.printer_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrinterLogger {

    private static Logger logger;

    private static PrinterLogger instance=null;

    private PrinterLogger(){
        logger = LoggerFactory.getLogger(PrinterLogger.class);
    }

    public static PrinterLogger getInstance() {
        if(instance==null){
            instance=new PrinterLogger();
        }
        return instance;
    }

    public void log(String message,MessageType type){
        switch (type) {
            case TRACE -> logger.trace(message);
            case DEBUG -> logger.debug(message);
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
        }

    }
}
