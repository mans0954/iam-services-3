package org.openiam.elasticsearch.hibernate;

import org.apache.log4j.Logger;
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
    private static Logger log = Logger.getLogger(OpeniamHibernateEventRegister.class);

    @Autowired
    private SessionFactory sessionFactory;

//    @Autowired
//    private SomeHibernateListener listener;

    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry eventRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(
                EventListenerRegistry.class);

        log.info("Registering event listeners");


        eventRegistry.prependListeners(EventType.POST_COMMIT_DELETE, new OpeniamHibernateEventListener(EventType.POST_COMMIT_DELETE));
        eventRegistry.prependListeners(EventType.POST_COMMIT_INSERT, new OpeniamHibernateEventListener(EventType.POST_COMMIT_INSERT));
        eventRegistry.prependListeners(EventType.POST_COMMIT_UPDATE, new OpeniamHibernateEventListener(EventType.POST_COMMIT_UPDATE));
//        eventRegistry.prependListeners(EventType.POST_DELETE, new OpeniamHibernateEventListener(EventType.POST_DELETE));
//        eventRegistry.prependListeners(EventType.POST_UPDATE, new OpeniamHibernateEventListener(EventType.POST_UPDATE));
//        eventRegistry.prependListeners(EventType.POST_INSERT, new OpeniamHibernateEventListener(EventType.POST_INSERT));
    }

}
