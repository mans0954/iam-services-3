package org.openiam.core.key.migrate;

import org.bouncycastle.util.encoders.Hex;
import org.openiam.core.key.util.KmUtil;
import org.openiam.core.key.ws.KeyManagementWSClient;
import org.openiam.idm.srvc.key.service.Response;
import org.openiam.idm.srvc.key.service.ResponseStatus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by: Alexander Duckardt
 * Date: 29.10.12
 */
public class MigrateTool {
    public static void main(String[] args){
        String keyFile = null;
        String wsdlLocation = null;
        Scanner scanner = new Scanner(System.in);

        try {

            File f = new File("km.properties");
            if(f!=null && f.exists()){
                System.out.println("Properties are found. loading...");
                Properties jksProperties = new Properties();
                jksProperties.load(new FileInputStream(f));

                keyFile = jksProperties.getProperty("ms.location");
                wsdlLocation = jksProperties.getProperty("km.ws.wsdl.location", KmUtil.DEFAULT_WSDL_LOCATION);

            } else{
                System.out.println("Properties not found. Prompt required data...");
                keyFile =  KmUtil.promtParameter(scanner, "Please enter a valid path and file name with secret key:");
                wsdlLocation =  KmUtil.promtParameter(scanner,
                                                      "Please enter a valid KeyManagementWS url (wsdl location):",
                                                      KmUtil.DEFAULT_WSDL_LOCATION);
            }
            if(wsdlLocation==null || wsdlLocation.trim().isEmpty()){
                wsdlLocation=KmUtil.DEFAULT_WSDL_LOCATION;
            }
            if(keyFile==null || keyFile.trim().isEmpty()){
                System.out.println("Secret key file is not specified. Please enter secret key file path");
                System.exit(0);
                return;
            }

            System.out.println("Start migrating process...");
            KeyManagementWSClient client = new  KeyManagementWSClient(wsdlLocation);

            String secretKey  =  readKey(keyFile);
            if(secretKey==null || secretKey.isEmpty()) {
                System.out.println("Cannot read secret key from specified file.");
                System.exit(0);
                return;
            }


            Response response = client.migrateData(secretKey);

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


    private static String readKey(String keyFile) {
        try {
            BufferedInputStream stream =  new BufferedInputStream(new FileInputStream(keyFile));
            int len = stream.available();
            byte[] key = new byte[len];
            stream.read(key, 0,len);
            stream.close();

            return new String(Hex.encode(key));
        }catch(IOException io) {
            io.printStackTrace();
        }
        return null;
    }


}
