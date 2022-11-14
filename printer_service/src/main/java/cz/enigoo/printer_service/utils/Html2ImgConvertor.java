package cz.enigoo.printer_service.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Util to convert html from string to image in PNG
 */
public class Html2ImgConvertor {
    /**
     * Static path to IMG file
     */
    private static final String IMG_PATH = "./print/print.png";
    /***
     * Static path to HTML file
     */
    private static final String HTML_PATH = "./print/print.html";
    /**
     * Instance of this class
     */
    private static Html2ImgConvertor instance = null;

    /**
     * Get instance method of Html2ImgConvertor
     * @return instance of Html2ImgConvertor
     */
    public static Html2ImgConvertor getInstance() {
        if(instance==null){
            instance = new Html2ImgConvertor();
        }
        return instance;
    }

    /**
     * Method to convert html data from string to image
     * @param html html data to convert
     * @param height height of image
     * @param width width of image
     * @return opened FileInputStream with converted image
     */
    public FileInputStream convert(String html,Long height,Long width){
        saveHtmlToFile(html);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe");
        processBuilder.command("./wkhtmltopdf/bin/wkhtmltoimage.exe","--width",String.valueOf(width),"--zoom","2","--images","--quality","100", HTML_PATH ,IMG_PATH);

        try{
            Process process = processBuilder.start();
            int exitVal = process.waitFor();
            if(exitVal==0){
                return new FileInputStream(IMG_PATH);
            }else{
                return null;
            }
        }catch (IOException e){
            System.out.println(e);
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Save html data from string to file.html
     * @param htmlData html data in String
     */
    private static void saveHtmlToFile(String htmlData){
        try{
            FileWriter fileWriter = new FileWriter(HTML_PATH);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(htmlData);
            bufferedWriter.close();
        }catch(Exception ex){
            System.out.println("SOME ERROR WITH SAVING HTML FILE");
        }
    }



}
