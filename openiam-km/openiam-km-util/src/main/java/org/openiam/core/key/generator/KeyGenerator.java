package org.openiam.core.key.generator;

import org.openiam.core.key.ws.KeyManagementWSClient;
import org.openiam.idm.srvc.res.service.Response;
import org.openiam.idm.srvc.res.service.ResponseStatus;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by: Alexander Duckardt
 * Date: 03.10.12
 */
public class KeyGenerator {

    public static void main(String[] args){
        String keyPass = null;
        String jksPass = null;
        String jksFile = null;
        String wsdlLocation = null;
        Scanner scanner = new Scanner(System.in);

        try {

            File f = new File("km-util.properties");
//            File f = new File("/home/alexander/km-util.properties");
            if(f!=null && f.exists()){
                System.out.println("Properties are found. loading...");
                Properties jksProperties = new Properties();
                jksProperties.load(new FileInputStream(f));

                wsdlLocation = jksProperties.getProperty("km.ws.wsdl.location", "http://localhost:8080/openiam-esb/idmsrvc/KeyManagementWS?wsdl");
            } else{
                System.out.println("Properties not found. Prompt required data...");

                wsdlLocation =  promtParameter(scanner, "Please enter a valid KeyManagementWS url (wsdl location):", "http://localhost:8080/openiam-esb/idmsrvc/KeyManagementWS?wsdl");
            }
            if(wsdlLocation==null || wsdlLocation.trim().isEmpty()){
                wsdlLocation="http://localhost:8080/openiam-esb/idmsrvc/KeyManagementWS?wsdl";
            }
            System.out.println("Generating master key...");
            KeyManagementWSClient client = new  KeyManagementWSClient(wsdlLocation);
            Response response = client.generateMasterKey();

            if(response.getStatus()== ResponseStatus.SUCCESS)
                System.out.println("Generating master key successfully finished");
            else
                System.out.println("Generating master key finished with error: " + response.getErrorText());
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            scanner.close();
        }
    }

    private static String promtParameter(Scanner scanner, String promtString) {
         return promtParameter(scanner,promtString,null);
    }
    private static String promtParameter(Scanner scanner, String promtString, String defaultValue) {
        String value = null;
        do{
           System.out.println(promtString);
           value =scanner.nextLine();

           if(defaultValue!=null && !defaultValue.isEmpty() && (value==null || value.trim().isEmpty())){
               System.out.println("You did not set value. It will be set by default.");
               value=defaultValue;
           }
           if(value==null || value.trim().isEmpty()){
             System.out.println("You did not set value. Please enter not empty value.");
             value=null;
           }
        } while(value == null);
        return value;
    }

}
