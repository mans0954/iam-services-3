package org.openiam.mq.constants.queue.user;

import org.openiam.mq.constants.queue.common.CommonQueue;

/**
 * Created by alexander on 17/11/16.
 */
public class PasswordQueue extends CommonQueue {
    public PasswordQueue() {
        super(PasswordQueue.class.getSimpleName());
    }
}
