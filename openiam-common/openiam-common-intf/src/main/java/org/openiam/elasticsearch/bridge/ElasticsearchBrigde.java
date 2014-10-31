package org.openiam.elasticsearch.bridge;

/**
 * Created by: Alexander Duckardt
 * Date: 9/17/14.
 */
public interface ElasticsearchBrigde {
    public String objectToString(Object object);
    public Object stringToObject(String stringValue);
}
