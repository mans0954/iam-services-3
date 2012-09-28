package org.openiam.sso.validator;

import org.openiam.sso.constant.SSOPropertiesKey;
import org.openiam.sso.utils.SSOProperties;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.StatusCode;

/**
 * Created by: Alexander Duckardt
 * Date: 25.09.12
 */
class SAMLLogoutResponseValidator extends AbstractSamlValidator {

    public SAMLLogoutResponseValidator(SSOProperties prop) {
        super(prop);
    }

    @Override
    public boolean validate(Object data) {
        try {
            if(context==null || !context.isExists())
                throw new IllegalAccessException("SSO Validation context is not initialized");

            if(data instanceof LogoutResponse)  {
                LogoutResponse logoutResponse = (LogoutResponse)data;
                // status
                if(!StatusCode.SUCCESS_URI.equals(logoutResponse.getStatus().getStatusCode().getValue())){
                    throw new IllegalArgumentException("STATUS FIELD IS NOT SUCCESS");
                }
                // Destination
                if(logoutResponse.getDestination()==null || !logoutResponse.getDestination().equals(context.getAttribute(SSOPropertiesKey.spServiceUrl)))
                    throw new IllegalArgumentException("Destination attrinbute is invalid in   SAML LOGOUT response!");
                // InResponseTo
                if(logoutResponse.getInResponseTo()==null || !logoutResponse.getInResponseTo().equals(context.getAttribute(SSOPropertiesKey.originalSAMLRequestId)))
                    throw new IllegalArgumentException("InResponseTo attrinbute is invalid or does not match to original SAML LOGOUT Request Id!");

                return true;
            } else {
                throw  new ClassCastException("Incompatible data class type.");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }
}
