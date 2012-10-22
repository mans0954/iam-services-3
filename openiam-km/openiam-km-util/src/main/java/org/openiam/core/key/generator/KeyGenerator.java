package org.openiam.core.key.generator;

import org.openiam.core.key.jks.JksManager;
import org.openiam.core.key.ws.KeyManagementWSClient;

/**
 * Created by: Alexander Duckardt
 * Date: 03.10.12
 */
public class KeyGenerator {

     private static final String providerName = "BC";

    public static void main(String[] args){
        // change this if you want another password by default
        String keypass = null;
        String jkspass = null;
        // change this if you want another alias by default
        String defaultalias = null;
        // change this if you want another keystorefile by default
        String keystorename = null;

        byte[] salt = new byte[24];
        // parsing command line input
        for (String arg : args) {
            if (arg.contains("--jks-path="))
                keystorename = arg.substring("--jks-path=".length());
            else if (arg.contains("--jks-password="))
                jkspass = arg.substring("--jks-password=".length());
            else if (arg.contains("--jks-key-password="))
                keypass = arg.substring("--jks-key-password=".length());
        }

        if (keystorename == null){
            keystorename = JksManager.KEYSTORE_DEFAULT_LOCATION+JksManager.KEYSTORE_FILE_NAME;
            System.out.println("Parameter jks-path is not initialized. Using default value: " + keystorename);
        }
        if (jkspass == null) {
            jkspass = JksManager.KEYSTORE_DEFAULT_PASSWORD;
            System.out.println("Parameter jks-password is not initialized. Using default value.");
        }
        if (keypass == null) {
            System.out.println("Parameter --jks-key-password is required");
            System.exit(0);
            return;
        }



        try {

            JksManager jksManager = new JksManager(keystorename);
            jksManager.generateKey(jkspass.toCharArray(), keypass.toCharArray());


            KeyManagementWSClient client = new  KeyManagementWSClient();
            client.refreshUserKeys();
//            KeyManager inKeyManager = new KeyManager(keystorename);
//            inKeyManager.getPrimaryKeyFromJKS(keypass.toCharArray());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
