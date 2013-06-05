package org.openiam.sso.validator;

import org.joda.time.DateTime;
import org.openiam.sso.constant.SSOPropertiesKey;
import org.openiam.sso.utils.SSOProperties;
import org.opensaml.saml2.core.LogoutRequest;

/**
 * Created by: Alexander Duckardt
 * Date: 25.09.12
 */
class SAMLLogoutRequestValidator  extends AbstractSamlValidator {

    public SAMLLogoutRequestValidator(SSOProperties prop) {
        super(prop);
    }

    @Override
    public boolean validate(Object data) {
        try {
            if(context==null || !context.isExists())
                throw new IllegalAccessException("SSO Validation context is not initialized");

            if(data instanceof LogoutRequest)  {
                LogoutRequest logoutRequest = (LogoutRequest)data;
                // Destination
                if(logoutRequest.getDestination()==null || !logoutRequest.getDestination().equals(context.getAttribute(SSOPropertiesKey.spServiceUrl))) {
                    throw new IllegalArgumentException("Destination attrinbute is invalid in   SAML LOGOUT response!");
                }
                // NotOnOrAfter
                DateTime currentDT = new  DateTime();
                if(logoutRequest.getNotOnOrAfter()==null || !currentDT.isBefore(logoutRequest.getNotOnOrAfter().plusSeconds(60).getMillis())){
                    throw new IllegalArgumentException("SAML Logout Request is expired!");
                }
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
