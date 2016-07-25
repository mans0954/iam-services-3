package org.openiam.message.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexander on 06/07/16.
 */
public enum OpenIAMQueue {
//    MetaElementQueue("metaElementQueue"),
    MetadataQueue("metadataQueue", OpenIAMAPI.MetadataTypeGet);

    private String queueName;
    private OpenIAMAPI[] openIAMAPIs;

    private static Map<OpenIAMQueue, HashMap<OpenIAMAPI, Integer>> map = new HashMap<OpenIAMQueue, HashMap<OpenIAMAPI, Integer>>();

    static {
        for (OpenIAMQueue pEnum : OpenIAMQueue.values()) {
            if(!map.containsKey(pEnum)){
                map.put(pEnum, new HashMap<OpenIAMAPI, Integer>());
            }
            HashMap<OpenIAMAPI, Integer> indexMap = map.get(pEnum);
            for(int i=0; i< pEnum.openIAMAPIs.length;i++){
                indexMap.put(pEnum.openIAMAPIs[i], i);
            }
        }
    }

    private OpenIAMQueue(String queueName, OpenIAMAPI... openIAMAPIs){
        this.queueName=queueName;
        this.openIAMAPIs=openIAMAPIs;
    }

    public String getQueueName(){
        return this.queueName;
    }

    public int getPartitionId(OpenIAMAPI api){
        return map.get(this).get(api);
    }

    public int getPartitionNumber(){
        return (this.openIAMAPIs==null || this.openIAMAPIs.length==0)? 1: this.openIAMAPIs.length;
    }
}
