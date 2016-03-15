package org.openiam.redis;

public interface TaskTriggerListener {

    void taskTriggered(String taskId, Object object);
}
