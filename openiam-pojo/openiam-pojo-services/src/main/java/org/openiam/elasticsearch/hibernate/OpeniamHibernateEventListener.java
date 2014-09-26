package org.openiam.elasticsearch.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.event.spi.*;

/**
 * Created by: Alexander Duckardt
 * Date: 9/25/14.
 */
public class OpeniamHibernateEventListener implements PostDeleteEventListener,
                                                      PostUpdateEventListener,
                                                      PostInsertEventListener {

    private static Logger log = Logger.getLogger(OpeniamHibernateEventListener.class);

    private EventType eventType;

    public OpeniamHibernateEventListener(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if(event!=null && event.getEntity()!=null)
            log.info(String.format("==== Hibernate Event: %s for Entity: %s =====", eventType.toString(), event.getEntity().getClass().getSimpleName()));
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if(event!=null && event.getEntity()!=null)
            log.info(String.format("==== Hibernate Event: %s for Entity: %s =====", eventType.toString(), event.getEntity().getClass().getSimpleName()));
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event!=null && event.getEntity()!=null)
            log.info(String.format("==== Hibernate Event: %s for Entity: %s =====", eventType.toString(), event.getEntity().getClass().getSimpleName()));
    }

}
