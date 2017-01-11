package org.openiam.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If this annotation is present on a Class, then this class is an RBAC object
 * Basically, this means that the DAO (ES or DB) should check if a user is entitled to this
 * object or not before returning from the DAO.
 * @author lbornova
 *
 */
@Retention( RetentionPolicy.RUNTIME )
@Target({ ElementType.TYPE })
public @interface EntitlementAware {

}
