package org.openiam.elasticsearch.model;

import org.apache.commons.lang.StringUtils;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.ElasticsearchType;
import org.openiam.elasticsearch.constants.Index;

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

    public ElasticsearchFieldMetadata(boolean isId, String name, ElasticsearchType type,
                                      ElasticsearchStore store, Index index){
        this(isId, name, type, store, index, null, null, null);
    }
    public ElasticsearchFieldMetadata(boolean isId, String name, ElasticsearchType type,
                                      ElasticsearchStore store, Index index, String analyzerName,
                                      String indexAnalyzerName, String searchAnalyzerName) {
        this.isId = isId;
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
}
