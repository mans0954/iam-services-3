package org.openiam.mq.constants.queue.am;


/**
 * Created by alexander on 15/11/16.
 */
public class AMManagerQueue extends AMQueue {
    public AMManagerQueue() {
        super(AMCacheQueue.class.getSimpleName());
    }
}
