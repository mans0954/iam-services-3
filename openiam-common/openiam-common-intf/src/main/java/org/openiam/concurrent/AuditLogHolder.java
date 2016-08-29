package org.openiam.concurrent;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

/**
 * Created by alexander on 22/08/16.
 */
public class AuditLogHolder {
    private static ThreadLocal<AuditLogHolder> tLocal = new ThreadLocal<>();
    private IdmAuditLogEntity event;


    private AuditLogHolder() {
        //locale = new Locale("en", "CA");
    }

    public static AuditLogHolder getInstance() {
        AuditLogHolder instance = tLocal.get();
        if (instance == null) {
            instance = new AuditLogHolder();
            tLocal.set(instance);
        }
        return instance;
    }
    public static void setInstance(AuditLogHolder instance) {
        if (instance != null) {
            tLocal.set(instance);
        }
    }

    public static AuditLogHolder getInstanceNoCreate() {
        return tLocal.get();
    }

    public static void remove(){
        tLocal.remove();
    }

    public IdmAuditLogEntity getEvent() {
        return event;
    }

    public void setEvent(IdmAuditLogEntity event) {
        this.event = event;
    }
}
