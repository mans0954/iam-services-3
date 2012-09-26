package org.openiam.sso.validator;

import org.openiam.sso.utils.SSOProperties;
import org.opensaml.saml2.core.Response;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.SignatureValidator;

/**
 * Created by: Alexander Duckardt
 * Date: 20.09.12
 */
public abstract class AbstractSamlValidator extends AbstractSSOValidator {
    public AbstractSamlValidator(SSOProperties prop) {
        super(prop);
    }

    protected boolean validateSignature(Response response) throws Exception{
        try {
            if(context==null || !context.isExists())
                throw new IllegalAccessException("SSO Validation context is not initialized");

                // check if signature conform to SAML Signature profile
                SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
                profileValidator.validate(response.getSignature());

                SignatureValidator sigValidator = new SignatureValidator(getSAMLCredential());
                sigValidator.validate(response.getSignature());
                return true;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    private Credential getSAMLCredential() throws Exception {
        BasicX509Credential result = new BasicX509Credential();
        result.setEntityCertificate(getCertificate());
        return result;
    }

}
