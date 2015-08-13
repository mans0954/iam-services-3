package org.openiam.elasticsearch.bridge;

/**
 * Created by: Alexander Duckardt
 * Date: 9/17/14.
 */
public interface ElasticsearchBrigde {
    String objectToString(Object object);
    Object stringToObject(String stringValue);
}
