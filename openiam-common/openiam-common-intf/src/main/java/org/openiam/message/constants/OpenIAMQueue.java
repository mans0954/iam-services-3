package org.openiam.message.constants;

/**
 * Created by alexander on 06/07/16.
 */
public enum OpenIAMQueue {
    MetaElementQueue("metaElementQueue"),
    MetadataQueue("metadataQueue");

    private String queueName;
    OpenIAMQueue(String queueName){
        this.queueName=queueName;
    }

    public String getQueueName(){
        return this.queueName;
    }
}
