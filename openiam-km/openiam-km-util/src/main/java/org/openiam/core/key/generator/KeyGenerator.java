package org.openiam.core.key.generator;

import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;

/**
 * Created by: Alexander Duckardt
 * Date: 03.10.12
 */
public class KeyGenerator {
     private static final String providerName = "BC";

    public static void main(String[] args){
        // change this if you want another password by default
        String keypass = null;

        // change this if you want another alias by default
        String defaultalias = "openiam";

        // change this if you want another keystorefile by default
        String keystorename = null;

        String issuer = null;
        String issuerEmail=null;
        // parsing command line input
        for (String arg : args) {
            if (arg.contains("--jks-path="))
                keystorename = arg.substring("--jks-path=".length());
            else if (arg.contains("--jks-password="))
                keypass = arg.substring("--jks-password=".length());
            else if (arg.contains("--issuer="))
                issuer = arg.substring("--issuer=".length());
            else if (arg.contains("--email="))
                issuerEmail = arg.substring("--email=".length());
        }

        if (issuer == null) {
            System.out.println("Parameter issuer is required.");
            System.exit(0);
            return;
        }
        if (issuerEmail == null) {
            System.out.println("Parameter email is required.");
            System.exit(0);
            return;
        }
        if (keystorename == null){
            keystorename = System.getProperty("java.home");
            System.out.println("Parameter jks-path is not initialized. Using default value: " + keystorename);
        }
        if (keypass == null) {
            keypass = "openiam";
            System.out.println("Parameter jks-password is not initialized. Using default value.");
        }




        if (Security.getProvider(providerName) == null){
            Security.addProvider(new BouncyCastleProvider());
        }else{
            System.out.println(providerName + " is installed.");
        }

        try {
            // initializing and clearing keystore


            KeyStore ks = KeyStore.getInstance("JKS", "SUN");

            File ksFile = new File(keystorename);
            if(!ksFile.exists()){
                ks.load(null, keypass.toCharArray());
                ks.store(new FileOutputStream(ksFile), keypass.toCharArray());
            }
            System.out.println("Using keystore-file : " + keystorename);
            ks.load(new FileInputStream(ksFile), keypass.toCharArray());


            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            // loading CertificateChain

            java.security.cert.Certificate[] certs = new java.security.cert.Certificate[1];
            System.out.println("One certificate, no chain.");
            java.security.cert.Certificate cert = getCertificate(keypair, issuer, issuerEmail);
            certs[0] = cert;


            // storing keystore
            ks.setKeyEntry(defaultalias, privateKey, keypass.toCharArray(), certs);
            System.out.println("Key generated and stored in JKS: "+ keystorename);
            System.out.println("Alias:" + defaultalias);

            ks.store(new FileOutputStream(ksFile,true), keypass.toCharArray());
            // need to update user keys

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static X509Certificate getCertificate(KeyPair pair, String issuer, String email)
            throws Exception {
        // generate the certificate
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=" + issuer));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 50000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 50000));
        certGen.setSubjectDN(new X500Principal("CN="+issuer));
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));

        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));

        certGen.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, email)));

        return certGen.generate(pair.getPrivate(), providerName);
    }
}
