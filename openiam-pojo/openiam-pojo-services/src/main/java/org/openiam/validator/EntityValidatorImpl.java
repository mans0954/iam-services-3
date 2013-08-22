package org.openiam.validator;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/21/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("entityValidator")
public class EntityValidatorImpl extends AbstractEntityValidator {
    @Override
    public <T> boolean isValid(T entity) throws BasicDataServiceException {
        boolean validationResult = true;
        BasicDataServiceException exception = null;

        Set<ConstraintViolation<T>> constraintViolations = this.validator.validate(entity);

        if(CollectionUtils.isNotEmpty(constraintViolations)){
            exception = new BasicDataServiceException(ResponseCode.VALIDATION_ERROR);
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                EsbErrorToken token = new  EsbErrorToken();

                Map<String, Object> attributes =  constraintViolation.getConstraintDescriptor().getAttributes();
                Object valueConstraint = attributes.get("value");
                if(valueConstraint!=null && valueConstraint instanceof Long){
                    token.setLengthConstraint((Long)valueConstraint);
                }

                token.setClassName(constraintViolation.getRootBeanClass().getSimpleName());
                token.setFieldName(constraintViolation.getPropertyPath().toString());
                token.setMessage(constraintViolation.getMessage());
                token.setValue(constraintViolation.getInvalidValue());

                log.error("Validation Error: " + token.toString());

                exception.addErrorToken(token);
            }
        }

        if(exception!=null){
            validationResult = false;
            throw exception;
        }
        return validationResult;
    }
}