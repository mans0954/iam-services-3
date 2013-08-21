package org.openiam.validator;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/21/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEntityValidator implements EntityValidator {
    protected Logger log = Logger.getLogger(this.getClass());
    protected Validator validator;

    @PostConstruct
    public void init(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
}
