package org.openiam.sso.validator;

/**
 * Created by: Alexander Duckardt
 * Date: 18.09.12
 */
public interface SSOValidator {

    boolean validate(Object data);
}
