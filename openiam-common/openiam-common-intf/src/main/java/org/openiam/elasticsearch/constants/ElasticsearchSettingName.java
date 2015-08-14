package org.openiam.elasticsearch.constants;

/**
 * Created by: Alexander Duckardt
 * Date: 7/3/14.
 */
public enum ElasticsearchSettingName {
    ShardNumber("number_of_shards"), ReplicaNumber("number_of_replicas");

    private String value;

    ElasticsearchSettingName(String value){
        this.value=value;
    }

    public String getValue(){
        return this.value;
    }
}
