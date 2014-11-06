package org.openiam.elasticsearch.constants;

/**
 * Field "store" value.
 */
public enum ElasticsearchStore {
    Yes(true), No(false);

    private boolean value;

    private ElasticsearchStore(boolean value){
        this.value=value;
    }

    public boolean getValue(){
        return this.value;
    }

}