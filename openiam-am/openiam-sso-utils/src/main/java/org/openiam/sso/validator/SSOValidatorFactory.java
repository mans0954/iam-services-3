package org.openiam.sso.validator;


import org.openiam.sso.constant.SSOType;
import org.openiam.sso.utils.SSOProperties;

/**
 * Created by: Alexander Duckardt
 * Date: 18.09.12
 */
public class SSOValidatorFactory {

    public static SSOValidator getSAMLAuthResponseValidator(SSOProperties context){
        return   getSSOValidator(context, SSOType.SAMLAuthResponse);
    }
    public static SSOValidator getSAMLLogoutRequestValidator(SSOProperties context) {
        return   getSSOValidator(context, SSOType.SAMLLogoutRequest);
    }
    public static SSOValidator getSAMLLogoutResponseValidator(SSOProperties context) {
        return   getSSOValidator(context, SSOType.SAMLLogoutResponse);
    }
    private static SSOValidator getSSOValidator(SSOProperties context, SSOType type) throws IllegalArgumentException{
       switch (type){
           case SAMLAuthResponse:
               return new SamlAuthResponseValidator(context);
           case SAMLLogoutRequest:
               return new  SAMLLogoutRequestValidator(context);
           case SAMLLogoutResponse:
               return new  SAMLLogoutResponseValidator(context);
           default:
               throw new IllegalArgumentException("Unknown or unimplemented SSOValidator type: " + type.name());
       }
    }



}
