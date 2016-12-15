package org.openiam.idm.srvc.cert.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;



@Service("certManager")
public class CertDataServiceImpl implements CertDataService {

    private static final Log log = LogFactory.getLog(CertDataServiceImpl.class);

    @Value("${org.openiam.cert.crl.path}")
    private String crlPath;

    public CertDataServiceImpl() {}

    public String getCrlPath() {
        return this.crlPath;
    }

    public boolean isCrlPath() {
        return (this.crlPath == null && StringUtils.isEmpty(this.crlPath)) ? false : true;
    }

    public List<String> getCrlDistributionPoints(X509Certificate cert) throws BasicDataServiceException {
        try {
            byte[] crldpExt = cert.getExtensionValue(org.bouncycastle.asn1.x509.Extension.cRLDistributionPoints.getId());
            if (crldpExt == null) {
                return new ArrayList<String>();
            }
            ASN1InputStream oAsnInStream = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExt));
            ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
            DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
            byte[] crldpExtOctets = dosCrlDP.getOctets();
            ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExtOctets));
            ASN1Primitive derObj2 = oAsnInStream2.readObject();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
            List<String> crlUrls = new ArrayList<String>();
            for (DistributionPoint dp : distPoint.getDistributionPoints()) {
                DistributionPointName dpn = dp.getDistributionPoint();
                // Look for URIs in fullName
                if (dpn != null) {
                    if (dpn.getType() == DistributionPointName.FULL_NAME) {
                        GeneralName[] genNames = GeneralNames.getInstance(
                                dpn.getName()).getNames();
                        // Look for an URI
                        for (int j = 0; j < genNames.length; j++) {
                            if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                                String url = DERIA5String.getInstance(
                                        genNames[j].getName()).getString();
                                crlUrls.add(url);
                            }
                        }
                    }
                }
            }
            return crlUrls;
        } catch (IOException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("IOException : getCrlDistributionPoints"));
        } catch (Exception ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("Unknown getCrlDistributionPoints Exception"));
        }
    }

    public List<X509CRLHolder> getAllCRLs(X509Certificate cert)
            throws BasicDataServiceException {
        List<X509CRLHolder> crlHolders = new ArrayList<X509CRLHolder>();
        List<String> crlDistPoints = getCrlDistributionPoints ( cert );
        for (String crlDP : crlDistPoints) {
            X509CRLHolder crlHolder = downloadCRL ( crlDP );
            crlHolders.add(crlHolder);
        }
        return crlHolders;
    }

    public X509CRLHolder loadCRLFromFile(String fileName)
            throws BasicDataServiceException {
        FileInputStream fs = null;
        X509CRLHolder crlHolder = null;
        try {
            File file = new File(fileName);

            try {
                fs = new FileInputStream ( fileName );
                crlHolder = new X509CRLHolder(fs);
            } finally {
                if ( fs != null) {
                    fs.close();
                }
            }
            return crlHolder;
        } catch (FileNotFoundException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("FileNotFoundException : loadCRLFromFile"));
        } catch (IOException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("IOException : loadCRLFromFile"));
        } catch (Exception ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("Unknown Exception : loadCRLFromFile"));
        }
    }

    public X509CRLHolder loadCRLFromWeb(String crlURL)
            throws BasicDataServiceException {
        X509CRLHolder crlHolder = null;
        try {
            URL url = new URL(crlURL);
            InputStream crlStream = url.openStream();

            try {
                crlHolder = new X509CRLHolder(crlStream);
            } finally {
                if (crlStream != null)
                    crlStream.close();
            }
            return crlHolder;
        } catch (MalformedURLException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("MalformedURLException  loadCRLFromWeb"));
        } catch (IOException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("IOException : loadCRLFromWeb"));
        } catch (Exception ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("Unknown Exception : loadCRLFromWeb"));
        }
    }

    public X509CRLHolder downloadCRL(String crlURL)
            throws BasicDataServiceException {
        if (crlURL.startsWith("http://") || crlURL.startsWith("https://") || crlURL.startsWith("ftp://")) {
            return loadCRLFromWeb(crlURL);
        } else if (crlURL.startsWith("file://")) {
            return loadCRLFromFile(crlURL.substring (7));
        } else {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_DOWNLOAD_EXCEPTION, String.format("Can not download CRL from certificate distribution point: " + crlURL));
        }
    }

    private static X509CRL crl(X509CRLHolder crlHolder)
            throws CRLException {
        final JcaX509CRLConverter converter = new JcaX509CRLConverter();
        return converter.getCRL(crlHolder);
    }

    public boolean isCertificateRevoked(X509CRLHolder crlHolder, X509Certificate ca, X509Certificate certificate)
            throws BasicDataServiceException {
        try {
            final X509CRL crl = crl(crlHolder);
            crl.verify(ca.getPublicKey());

            final BigInteger sn = certificate.getSerialNumber();
            final X509CRLEntry entry = crl.getRevokedCertificate(sn);
            if (entry == null) {
                return false;
            } else {
                return true;
            }
        } catch (CRLException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("CRLException : isCertificateRevoked"));
        } catch (NoSuchAlgorithmException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("NoSuchAlgorithmException : isCertificateRevoked"));
        } catch (InvalidKeyException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("InvalidKeyException : isCertificateRevoked"));
        } catch (NoSuchProviderException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("NoSuchProviderException : isCertificateRevoked"));
        } catch (SignatureException ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("SignatureException : isCertificateRevoked"));
        } catch (Exception ex) {
            throw new BasicDataServiceException(ResponseCode.CERT_CRL_VERIFY_EXCEPTION, String.format("Unknown Exception : isCertificateRevoked"));
        }
    }


    public void verifyCertificateNotRevoked(X509Certificate ca, X509Certificate certificate)
            throws BasicDataServiceException {

        List<X509CRLHolder> crlHolders = getAllCRLs(ca);

        for ( X509CRLHolder crlHolder : crlHolders ) {
            if (isCertificateRevoked ( crlHolder, ca, certificate )) {
                throw new BasicDataServiceException(ResponseCode.CERT_WAS_REVOKED, String.format("Certificate was revoked in ca list"));
            }
        }

        crlHolders = getAllCRLs(certificate);

        for ( X509CRLHolder crlHolder : crlHolders ) {
            if (isCertificateRevoked ( crlHolder, ca, certificate )) {
                throw new BasicDataServiceException(ResponseCode.CERT_WAS_REVOKED, String.format("Certificate was revoked in cert list"));
            }
        }
    }

    public void verifyCertificateNotRevoked(List<X509CRLHolder> crlHolders, X509Certificate ca, X509Certificate certificate)
            throws BasicDataServiceException {

        for ( X509CRLHolder crlHolder : crlHolders ) {
            if (isCertificateRevoked ( crlHolder, ca, certificate )) {
                throw new BasicDataServiceException(ResponseCode.CERT_WAS_REVOKED, String.format("Certificate was revoked"));
            }
        }
    }
}
