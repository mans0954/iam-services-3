package org.openiam.validator;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 8/21/13 Time: 9:07 PM To
 * change this template use File | Settings | File Templates.
 */
@Service("entityValidator")
public class EntityValidatorImpl extends AbstractEntityValidator {
    protected static final Log LOG = LogFactory.getLog(AbstractEntityValidator.class);
    protected Validator validator;

    @PostConstruct
    public void init(){
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new IgnoreTraversableResolver()).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public <T> boolean isValid(T entity) throws BasicDataServiceException {
        boolean validationResult = true;
        BasicDataServiceException exception = null;

        Set<ConstraintViolation<T>> constraintViolations = this.validator.validate(entity);

        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            exception = new BasicDataServiceException(ResponseCode.VALIDATION_ERROR);
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                EsbErrorToken token = new EsbErrorToken();

                Map<String, Object> attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                Object valueConstraint = attributes.get("max");
                if (valueConstraint != null) {
                    if(valueConstraint instanceof Integer) {
                        token.setLengthConstraint(Long.valueOf((Integer) valueConstraint).longValue());
                    } else if(valueConstraint instanceof Long) {
                        token.setLengthConstraint((Long)valueConstraint);
                    }
                }

                token.setClassName(constraintViolation.getRootBeanClass().getSimpleName());
                token.setFieldName(constraintViolation.getPropertyPath().toString());
                token.setMessage(constraintViolation.getMessage());
                token.setValue(constraintViolation.getInvalidValue());

                LOG.error("Validation Error: " + token.toString());

                exception.addErrorToken(token);
            }
        }

        if (exception != null) {
            validationResult = false;
            throw exception;
        }
        return validationResult;
    }
}
