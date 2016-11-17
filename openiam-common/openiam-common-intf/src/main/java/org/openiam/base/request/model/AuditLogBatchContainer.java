package org.openiam.base.request.model;

import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 17/11/16.
 */
public class AuditLogBatchContainer {
    private static final int BATCH_SIZE=1000;

    private List<IdmAuditLogEntity> eventList;

    public List<IdmAuditLogEntity> getEventList() {
        // make a copy and return it
        return new ArrayList<>(eventList);
    }

    public void addEvent(IdmAuditLogEntity event){
        if(this.eventList==null){
            this.eventList = new ArrayList<>();
        }
        this.eventList.add(event);
    }

    public boolean isFull(){
        return this.eventList!=null && BATCH_SIZE == this.eventList.size();
    }
}
