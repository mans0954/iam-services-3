package org.openiam.mq.constants.queue.user;

import org.openiam.mq.constants.queue.common.CommonQueue;

/**
 * Created by alexander on 17/11/16.
 */
public class LoginQueue extends UserQueue {
    public LoginQueue() {
        super(LoginQueue.class.getSimpleName());
    }
}
