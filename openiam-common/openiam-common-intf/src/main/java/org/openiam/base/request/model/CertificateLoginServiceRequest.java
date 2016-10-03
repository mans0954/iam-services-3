package org.openiam.base.request.model;


import org.openiam.base.request.AbstractFederationServiceRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by alexander on 10/08/16.
 */
public class CertificateLoginServiceRequest extends AbstractFederationServiceRequest {
    private MultipartFile certContents;

    public MultipartFile getCertContents() {
        return certContents;
    }

    public void setCertContents(MultipartFile certContents) {
        this.certContents = certContents;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("URIFederationServiceRequest{");
        sb.append(super.toString());
        sb.append(", certContents='").append(certContents).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
