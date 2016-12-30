package org.openiam.idm.srvc.cert.service;

import org.bouncycastle.cert.X509CRLHolder;
import org.openiam.exception.BasicDataServiceException;

import java.security.cert.X509Certificate;
import java.util.List;

public interface CertDataService {

    public List<String> getCrlDistributionPoints(X509Certificate cert) throws BasicDataServiceException;

    public List<X509CRLHolder> getAllCRLs(X509Certificate cert) throws BasicDataServiceException;

    public X509CRLHolder loadCRLFromFile(String fileName) throws BasicDataServiceException;

    public X509CRLHolder loadCRLFromWeb(String crlURL) throws BasicDataServiceException;

    public X509CRLHolder downloadCRL(String crlURL) throws BasicDataServiceException;

    public boolean isCertificateRevoked(X509CRLHolder crlHolder, X509Certificate ca, X509Certificate certificate) throws BasicDataServiceException;

    public void verifyCertificateNotRevoked(X509Certificate ca, X509Certificate certificate) throws BasicDataServiceException;

    public void verifyCertificateNotRevoked(List<X509CRLHolder> crlHolders, X509Certificate ca, X509Certificate certificate) throws BasicDataServiceException;


}