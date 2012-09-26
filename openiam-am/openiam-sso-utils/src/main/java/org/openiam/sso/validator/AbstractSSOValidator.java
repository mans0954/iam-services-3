package org.openiam.sso.validator;

import org.openiam.sso.constant.SSOPropertiesKey;
import org.openiam.sso.utils.SSOProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by: Alexander Duckardt
 * Date: 18.09.12
 */
abstract class AbstractSSOValidator implements SSOValidator {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected SSOProperties       context;

    public AbstractSSOValidator(SSOProperties context){
	this.context=context;
    }

    protected X509Certificate getCertificate() throws Exception{
        return getCertificate((String)context.getAttribute(SSOPropertiesKey.certificateFileName));
    }
    protected  X509Certificate getCertificate(String fileName) throws Exception{
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream in = new FileInputStream(fileName);
        X509Certificate cert = (X509Certificate) cf.generateCertificate(in);
        return cert;
    }
}
