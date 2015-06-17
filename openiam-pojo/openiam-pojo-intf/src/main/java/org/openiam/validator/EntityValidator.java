package org.openiam.validator;

import org.openiam.exception.BasicDataServiceException;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/21/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EntityValidator {
    <T> boolean isValid(T entity) throws BasicDataServiceException;
}
