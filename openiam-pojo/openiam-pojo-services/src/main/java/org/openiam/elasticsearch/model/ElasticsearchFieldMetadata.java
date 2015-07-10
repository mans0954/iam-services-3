package org.openiam.elasticsearch.model;

import org.apache.commons.lang.StringUtils;
import org.openiam.elasticsearch.bridge.ElasticsearchBrigde;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.ElasticsearchType;
import org.openiam.elasticsearch.constants.Index;

import java.lang.reflect.Field;

/**
 * Created by: Alexander Duckardt
 * Date: 7/4/14.
 */
public class ElasticsearchFieldMetadata {
    private boolean isId=false;
    private String name;
    private ElasticsearchType type;
    private ElasticsearchStore store;
    private Index index;
    private String analyzerName;
    private String indexAnalyzerName;
    private String searchAnalyzerName;
    private Field field;
    private ElasticsearchBrigde bridge;
    private boolean mapToParent;

    public ElasticsearchFieldMetadata(boolean isId, Field field, String name, ElasticsearchType type,
                                      ElasticsearchStore store, Index index){
        this(isId, field, name, type, store, index, null, null, null, null, false);
    }
    public ElasticsearchFieldMetadata(boolean isId, Field field, String name, ElasticsearchType type,
                                      ElasticsearchStore store, Index index, String analyzerName,
                                      String indexAnalyzerName, String searchAnalyzerName, ElasticsearchBrigde bridge, boolean mapToParent) {
        this.isId = isId;
        this.field = field;
        this.name = name;
        this.type = type;
        this.store = store;
        this.index = index;
        if(StringUtils.isNotBlank(analyzerName))
            this.analyzerName = analyzerName;
        if(StringUtils.isNotBlank(indexAnalyzerName))
            this.indexAnalyzerName = indexAnalyzerName;
        if(StringUtils.isNotBlank(searchAnalyzerName))
            this.searchAnalyzerName = searchAnalyzerName;

        this.bridge = bridge;
        this.mapToParent=mapToParent;
    }

    public boolean isId() {
        return isId;
    }

    public void setId(boolean isId) {
        this.isId = isId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElasticsearchType getType() {
        return type;
    }

    public void setType(ElasticsearchType type) {
        this.type = type;
    }

    public ElasticsearchStore getStore() {
        return store;
    }

    public void setStore(ElasticsearchStore store) {
        this.store = store;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public String getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(String analyzerName) {
        this.analyzerName = analyzerName;
    }

    public String getIndexAnalyzerName() {
        return indexAnalyzerName;
    }

    public void setIndexAnalyzerName(String indexAnalyzerName) {
        this.indexAnalyzerName = indexAnalyzerName;
    }

    public String getSearchAnalyzerName() {
        return searchAnalyzerName;
    }

    public void setSearchAnalyzerName(String searchAnalyzerName) {
        this.searchAnalyzerName = searchAnalyzerName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public ElasticsearchBrigde getBridge() {
        return bridge;
    }

    public void setBridge(ElasticsearchBrigde bridge) {
        this.bridge = bridge;
    }

    public boolean isMapToParent() {
        return mapToParent;
    }

    public void setMapToParent(boolean mapToParent) {
        this.mapToParent = mapToParent;
    }
}