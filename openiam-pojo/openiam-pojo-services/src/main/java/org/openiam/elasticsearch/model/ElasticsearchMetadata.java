package org.openiam.elasticsearch.model;

import org.openiam.elasticsearch.annotation.ElasticsearchIndex;
import org.openiam.elasticsearch.annotation.ElasticsearchMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 7/1/14.
 */
public class ElasticsearchMetadata {
    private ElasticsearchIndex index;
    private ElasticsearchMapping typeMapping;
    private List<ElasticsearchFieldMetadata> indexedFields;


    public ElasticsearchIndex getIndex() {
        return index;
    }

    public void setIndex(ElasticsearchIndex index) {
        this.index = index;
    }

    public ElasticsearchMapping getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(ElasticsearchMapping typeMapping) {
        this.typeMapping = typeMapping;
    }

    public List<ElasticsearchFieldMetadata> getIndexedFields() {
        return indexedFields;
    }

    public void setIndexedFields(List<ElasticsearchFieldMetadata> indexedFields) {
        this.indexedFields = indexedFields;
    }

    public void addField(ElasticsearchFieldMetadata field){
        if(indexedFields==null)
            indexedFields=new ArrayList<>();
        indexedFields.add(field);
    }
}
