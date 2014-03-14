package org.openiam.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class HibernateUtils {

	
	public static <T> T unproxy(T entity) {
        if (entity == null) {
            return null;
        }
  
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
 
        return entity;
    }
}
