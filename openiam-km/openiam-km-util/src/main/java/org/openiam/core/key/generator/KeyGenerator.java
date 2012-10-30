package org.openiam.core.key.generator;

import org.openiam.core.key.util.KmUtil;
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
            if(f!=null && f.exists()){
                System.out.println("Properties are found. loading...");
                Properties jksProperties = new Properties();
                jksProperties.load(new FileInputStream(f));

                wsdlLocation = jksProperties.getProperty("km.ws.wsdl.location", KmUtil.DEFAULT_WSDL_LOCATION);
            } else{
                System.out.println("Properties not found. Prompt required data...");

                wsdlLocation = KmUtil.promtParameter(scanner, "Please enter a valid KeyManagementWS url (wsdl location):", KmUtil.DEFAULT_WSDL_LOCATION);
            }
            if(wsdlLocation==null || wsdlLocation.trim().isEmpty()){
                wsdlLocation=KmUtil.DEFAULT_WSDL_LOCATION;
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



}
