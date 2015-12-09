package org.openiam.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lbornova
 * This is a HACK!
 * 
 * The whole point of this annotation is to tell Spring Data ElasticSearch
 * that we don't need to do any custom logic to map to/from JSON,
 * and we can use the Jackson ObjectMapper directly
 * 
 * Under normal circumstances, you could do this with non-Hibernate entities.
 * 
 * Unfortunately, the first use-case is <b>IdmAuditLogEntity</b>
 * which has to stay a Hibenrate Entity until all customers are off of version 3.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target({ ElementType.TYPE })
public @interface SimpleElasticSearchJSONMapping {

}
