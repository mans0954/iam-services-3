package org.openiam.validator;


import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/21/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEntityValidator implements EntityValidator {
	protected static final Log LOG = LogFactory.getLog(AbstractEntityValidator.class);
    protected Validator validator;

    @PostConstruct
    public void init(){
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new IgnoreTraversableResolver()).buildValidatorFactory();
        validator = factory.getValidator();
    }
}
