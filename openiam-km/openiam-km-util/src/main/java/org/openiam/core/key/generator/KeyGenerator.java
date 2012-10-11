package org.openiam.core.key.generator;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.openiam.core.key.manager.KeyManager;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;
import java.util.Random;

/**
 * Created by: Alexander Duckardt
 * Date: 03.10.12
 */
public class KeyGenerator {
     private static final String KEYSTORE_FILE_NAME = ".openiam.jks";
    private static final String KEYSTORE_DEFAULT_PASSWORD = "openiam";

     private static final String providerName = "BC";

    public static void main(String[] args){
        // change this if you want another password by default
        String keypass = null;
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
                keypass = arg.substring("--jks-password=".length());
        }

        if (keystorename == null){
            keystorename = System.getProperty("user.home")+System.getProperty("file.separator")+KEYSTORE_FILE_NAME;
            System.out.println("Parameter jks-path is not initialized. Using default value: " + keystorename);
        }
        if (keypass == null) {
            keypass = KEYSTORE_DEFAULT_PASSWORD;
            System.out.println("Parameter jks-password is not initialized. Using default value.");
        }



        try {

            KeyManager keyManager = new KeyManager(keystorename);
            keyManager.generateKey(keypass.toCharArray());


//            KeyManager inKeyManager = new KeyManager(keystorename);
//            inKeyManager.getPrimaryKeyFromJKS(keypass.toCharArray());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
