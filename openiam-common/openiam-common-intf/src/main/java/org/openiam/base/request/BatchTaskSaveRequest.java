package org.openiam.base.request;

import org.openiam.idm.srvc.batch.dto.BatchTask;

/**
 * Created by alexander on 26/10/16.
 */
public class BatchTaskSaveRequest extends BaseCrudServiceRequest<BatchTask> {
    private boolean purgeNonExecutedTasks;

    public BatchTaskSaveRequest(BatchTask object) {
        super(object);
    }

    public boolean isPurgeNonExecutedTasks() {
        return purgeNonExecutedTasks;
    }

    public void setPurgeNonExecutedTasks(boolean purgeNonExecutedTasks) {
        this.purgeNonExecutedTasks = purgeNonExecutedTasks;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BatchTaskSaveRequest{");
        sb.append(super.toString());
        sb.append(", purgeNonExecutedTasks=").append(purgeNonExecutedTasks);
        sb.append('}');
        return sb.toString();
    }
}
