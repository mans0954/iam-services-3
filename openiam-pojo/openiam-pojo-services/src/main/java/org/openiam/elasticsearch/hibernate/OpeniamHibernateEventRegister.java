package org.openiam.elasticsearch.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by: Alexander Duckardt
 * Date: 9/25/14.
 */
@Component
public class OpeniamHibernateEventRegister {
	private static final Log log = LogFactory.getLog(OpeniamHibernateEventRegister.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private OpeniamHibernateEventListener listener;

    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry eventRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(
                EventListenerRegistry.class);

        log.info("Registering event listeners");


        eventRegistry.prependListeners(EventType.POST_COMMIT_DELETE, listener);
        eventRegistry.prependListeners(EventType.POST_COMMIT_INSERT, listener);
        eventRegistry.prependListeners(EventType.POST_COMMIT_UPDATE, listener);
    }

}
