package org.openiam.message.consumer;

import org.openiam.message.constants.OpenIAMQueue;

/**
 * Created by alexander on 07/07/16.
 */
public abstract class AbstractMessageListener {

    private OpenIAMQueue queueToListen;
    private boolean isInitialized=false;

    public AbstractMessageListener(OpenIAMQueue queueToListen){
        this.queueToListen=queueToListen;
    }

    public void afterPropertiesSet() {
        initListener();
        isInitialized=true;
    }

    protected void initListener(){
        if(!isInitialized){
            doStart();
        }
    }


    public OpenIAMQueue getQueueToListen() {
        return queueToListen;
    }

    protected abstract void doStart();
}
