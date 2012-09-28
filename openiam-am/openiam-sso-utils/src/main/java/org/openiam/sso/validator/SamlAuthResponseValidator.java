package org.openiam.sso.validator;

import org.joda.time.DateTime;
import org.openiam.sso.constant.SSOPropertiesKey;
import org.openiam.sso.utils.SSOProperties;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.SubjectConfirmation;

/**
 * Created by: Alexander Duckardt
 * Date: 18.09.12
 */
class SamlAuthResponseValidator extends AbstractSamlValidator {

    public SamlAuthResponseValidator(SSOProperties prop){
        super(prop);
    }

    @Override
    public boolean validate(Object data) {
        try {
            if(context==null || !context.isExists())
                throw new IllegalAccessException("SSO Validation context is not initialized");

            if(data instanceof Response)  {
                Response authResponse =  (Response)data;
                // 0. validate response ID
                if(authResponse.getInResponseTo()==null || !authResponse.getInResponseTo().equals(context.getAttribute(SSOPropertiesKey.originalSAMLRequestId)))
                    throw new IllegalArgumentException("InResponseTo attrinbute is invalid or does not match to original SAML Request Id!");

                // 1. validate Signature
                if(!super.validateSignature(authResponse))
                    throw new IllegalArgumentException("Error in SAML Signature validation");

                if(authResponse.getAssertions()==null || authResponse.getAssertions().isEmpty())
                    throw new IllegalArgumentException("There is no any assertions in SAML Response");

                for(Assertion assertion: authResponse.getAssertions()){
                    if(assertion.getSubject()!=null){
                        if(assertion.getSubject().getSubjectConfirmations()==null || assertion.getSubject().getSubjectConfirmations().isEmpty())
                            throw new IllegalArgumentException("There is no any Subject Confirmation in SAML Assertions!");

                        for (SubjectConfirmation sc: assertion.getSubject().getSubjectConfirmations()){

                            if(sc.getSubjectConfirmationData().getRecipient()==null || !sc.getSubjectConfirmationData().getRecipient().equals(context.getAttribute(SSOPropertiesKey.spServiceUrl)))
                                throw new IllegalArgumentException("Recipient attrinbute is invalid in  SubjectConfirmationData of SAML response!");

                            if(sc.getSubjectConfirmationData().getInResponseTo()==null || !sc.getSubjectConfirmationData().getInResponseTo().equals(context.getAttribute(SSOPropertiesKey.originalSAMLRequestId)))
                                throw new IllegalArgumentException("InResponseTo attrinbute is invalid or does not match to original SAML Request Id!");

                            DateTime currentDT = new  DateTime();
                            if(sc.getSubjectConfirmationData().getNotOnOrAfter()==null || !currentDT.isBefore(sc.getSubjectConfirmationData().getNotOnOrAfter().plusSeconds(60).getMillis()))
                                throw new IllegalArgumentException("SAML Response is expired!");

                        }
                    }
                }
                return true;
            } else {
               throw  new ClassCastException("Incompatible data class type.");
            }

        } catch (Exception e) {
            // Indicates signature was not cryptographically valid, or possibly a processing error
            log.warn(e.getMessage(), e);
            return false;
        }
    }



}
