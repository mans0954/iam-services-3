package org.openiam.elasticsearch.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.openiam.elasticsearch.annotation.*;
import org.openiam.elasticsearch.bridge.ElasticsearchBrigde;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.ElasticsearchType;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.elasticsearch.factory.ESAbstractClientFactoryBean;
import org.openiam.elasticsearch.factory.ESClientFactoryBean;
import org.openiam.elasticsearch.factory.ESNodeFactoryBean;
import org.openiam.elasticsearch.factory.ESTransportClientFactoryBean;
import org.openiam.elasticsearch.model.ElasticsearchFieldMetadata;
import org.openiam.elasticsearch.model.ElasticsearchMetadata;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 7/1/14.
 */
@Component
public class ElasticsearchProvider implements InitializingBean, DisposableBean {
    protected static Logger logger = Logger.getLogger(ElasticsearchProvider.class);

    private static Map<String, ElasticsearchMetadata> indexeMetadataMap = new HashMap<>();

    @Value("${org.openiam.es.client}")
    private String clientType;
    @Value("${org.openiam.es.external.nodes}")
    private String esNodes;
    @Value("${org.openiam.es.default.cluster.name}")
    private String clusterName;

//    @Autowired
    // TODO: need to refactor to spring bean. it should depend on what kind of ES engine (embedded or external) we're going to use
    private ESAbstractClientFactoryBean clientFactory;

    private Properties hibernateProperties;

    @Resource(name = "hibernateProperties")
    public void setHibernateProperties(final Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }


    private Client getClient() throws Exception {
        return this.clientFactory.getObject();
    }

    public <T> void doIndex(List<T> entityList, Class<T> clazz) throws Exception {
        if(CollectionUtils.isNotEmpty(entityList)){
            BulkRequestBuilder bulkRequest = getClient().prepareBulk();

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\n=== Bulk Index Request for %s entity:\n", clazz.getSimpleName()));
            for(T entity: entityList){
                IndexRequestBuilder request = prepareIndexRequest(entity, clazz);
                if(request!=null){
                    sb.append("\t").append(request).append("\n");
                    bulkRequest.add(request);
                }
            }

            logger.warn(sb.toString());

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                throw new Exception("Can't index");
                // process failures by iterating through each bulk response item
            }
        }
    }

    private <T> IndexRequestBuilder prepareIndexRequest(T entity, Class<T> clazz) throws Exception{
        ElasticsearchMetadata metadata = indexeMetadataMap.get(clazz.getSimpleName());
        String entityId=null;
        String parentId=null;
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        for(ElasticsearchFieldMetadata fieldMetadata :metadata.getIndexedFields()){
            Field f = fieldMetadata.getField();
            f.setAccessible(true);
            Object value = (fieldMetadata.getBridge()!=null) ? fieldMetadata.getBridge().objectToString(f.get(entity)) : f.get(entity);

            if(fieldMetadata.isId()){
                entityId = (String)value;
                if(StringUtils.isBlank(entityId)){
                    logger.warn("Skipping indexing the entity due to entityId is empty");
                    return null;
                }

            }
            builder.field(fieldMetadata.getName(), value);

            if(fieldMetadata.isMapToParent()){
                parentId = (String)value;
//                builder.field("_parentId", value);
            }

        }
        builder.endObject();
        IndexRequestBuilder request = getClient().prepareIndex(metadata.getIndex().indexName(), metadata.getTypeMapping().typeName(), entityId)
                                                    .setSource(builder);

        if(StringUtils.isNotBlank(parentId)){
            request.setParent(parentId);
        }

        return request;
    }

    public void buildIndex(Class<?> entityClass, boolean purgeAll) throws Exception {
        this.buildIndex(this.getIndexMetadata(entityClass), purgeAll);
    }

    public void buildIndex(ElasticsearchMetadata indexMetadata, boolean purgeAll) throws Exception {
        if(!isIndexExists(indexMetadata.getIndex().indexName())){
            createIndex(indexMetadata.getIndex().indexName(), buildIndexSettings(indexMetadata));
        }

        if(isMappingExists(indexMetadata.getIndex().indexName(), indexMetadata.getTypeMapping().typeName())){
            if(purgeAll){
                deleteMapping(indexMetadata.getIndex().indexName(), indexMetadata.getTypeMapping().typeName());
                // Build & update fields mappings
                putIndexMapping(indexMetadata.getIndex().indexName(), indexMetadata.getTypeMapping().typeName(), buildMapping(indexMetadata));
            }
        }else{
            // Build & update fields mappings
            putIndexMapping(indexMetadata.getIndex().indexName(), indexMetadata.getTypeMapping().typeName(), buildMapping(indexMetadata));
        }
    }

    private boolean isMappingExists(String indexName, String typeName) throws Exception {
        ClusterStateResponse resp = this.getClient().admin().cluster().prepareState().execute().actionGet();
        MappingMetaData mapping = resp.getState().metaData().index(indexName).mapping(typeName);
        return mapping!=null;
    }

    public String getIdFieldName(Class<?> entityClass) throws Exception {
        return this.getIndexMetadata(entityClass).getIdFieldName();
    }

    public ElasticsearchMetadata getIndexMetadata(Class<?> entityClass) throws Exception {
        ElasticsearchMetadata result = indexeMetadataMap.get(entityClass.getSimpleName());
        if(result==null){
            result = new ElasticsearchMetadata();
            // collect entity metadata
            ElasticsearchIndex index = entityClass.getAnnotation(ElasticsearchIndex.class);
            if(index==null)
                throw new IllegalArgumentException(String.format("Class %s does not have %s annotation",
                                                                 entityClass.getSimpleName(),
                                                                 ElasticsearchIndex.class.getSimpleName()));
            ElasticsearchMapping mapping = entityClass.getAnnotation(ElasticsearchMapping.class);
            if(mapping==null)
                throw new IllegalArgumentException(String.format("Class %s does not have %s annotation",
                                                                 entityClass.getSimpleName(),
                                                                 ElasticsearchMapping.class.getSimpleName()));
            result.setIndex(index);
            result.setTypeMapping(mapping);

            // collect fields metadata
            boolean hasId=false;
            List<Field> declaredFields = getDeclaredFields(entityClass);
            if(CollectionUtils.isNotEmpty(declaredFields)){
                for(Field field: declaredFields){
                    Annotation annotation =  getFieldAnnotation(field, ElasticsearchId.class);
                    if(annotation!=null){
                        hasId=true;
                        result.setIdFieldName(field.getName());
                        result.addField(new ElasticsearchFieldMetadata(true, field, field.getName(), ElasticsearchType.String, ElasticsearchStore.Yes, Index.Not_Analyzed));
                        continue;
                    }
                    annotation =  getFieldAnnotation(field, ElasticsearchFields.class);
                    if(annotation!=null){
                        ElasticsearchFields fieldsAnnotation = (ElasticsearchFields)annotation;
                        if(fieldsAnnotation.fields()!=null && fieldsAnnotation.fields().length>0){
                            for(ElasticsearchField childAnnotation :fieldsAnnotation.fields()){
                                mapFieldAnnotation(field, childAnnotation,result);
                            }
                        }


                    } else {
                        mapFieldAnnotation(field, getFieldAnnotation(field, ElasticsearchField.class),result);
//                        annotation =  getFieldAnnotation(field, ElasticsearchField.class);
//                        if(annotation!=null){
//                            ElasticsearchField esAnnotation = (ElasticsearchField)annotation;
//                            Class bridgeClazz = esAnnotation.bridge().impl();
//                            ElasticsearchBrigde bridge = null;
//                            if(void.class.isAssignableFrom(bridgeClazz)){
//                                bridge = null;
//                            } else if(!ElasticsearchBrigde.class.isAssignableFrom(bridgeClazz)){
//                                logger.warn(String.format("Elasticsearch Bridge for fiels %s should implement %s interface", field.getName(), ElasticsearchBrigde.class.getName()));
//                            }else{
//                                bridge = (ElasticsearchBrigde)bridgeClazz.newInstance();
//                            }
//
//                            result.addField(new ElasticsearchFieldMetadata(false, field, esAnnotation.name(), esAnnotation.type(), esAnnotation.store(), esAnnotation.index(),
//                                    esAnnotation.analyzerName(),esAnnotation.indexAnalyzerName(),esAnnotation.searchAnalyzerName(),
//                                    bridge,esAnnotation.mapToParent()));
//                        }
                    }
                }
            }
            if(!hasId)
                throw new NullPointerException(String.format("Class %s does not have field mapped with annotation %s.",
                                                             entityClass.getSimpleName(),
                                                             ElasticsearchId.class.getSimpleName()));
            indexeMetadataMap.put(entityClass.getSimpleName(), result);
        }
        return result;
    }

    public void deleteData(String id, Class<?> entityClass) throws Exception {
        if(StringUtils.isNotBlank(id)){
            ElasticsearchMetadata result = indexeMetadataMap.get(entityClass.getSimpleName());

            DeleteResponse response = getClient().prepareDelete(result.getIndex().indexName(), result.getTypeMapping().typeName(), id)
                                            .execute()
                                            .actionGet();

            if(!response.isFound()){
                throw new Exception(String.format("%s with %s cannot be found for deletion. Skipping", entityClass.getSimpleName(), id));
            }
        }
    }
    public void deleteData(List<String> idList, Class<?> entityClass) throws Exception {
        if(CollectionUtils.isNotEmpty(idList)){
            for (String id: idList){
                try{
                    this.deleteData(id, entityClass);
                } catch (Exception ex){
                    logger.warn(String.format("%s with %s cannot be found for deletion. Skipping", entityClass.getSimpleName(), id));
                }
            }
        }
    }

    public SearchResponse searchData(QueryBuilder query, Class<?> entityClass) {
        try {
            if(query!=null) {
                ElasticsearchMetadata result = indexeMetadataMap.get(entityClass.getSimpleName());

                SearchResponse response = getClient().prepareSearch(result.getIndex().indexName())
                            .setTypes(result.getTypeMapping().typeName())
                            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                            .setQuery(query).setExplain(true)
                            .execute()
                            .actionGet();
                return response;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public Set<String> separateTerms(final String keyword, Class<?> entityClass) {
        final Set<String> result = new HashSet<String>();
        try {
            ElasticsearchMetadata metadata = indexeMetadataMap.get(entityClass.getSimpleName());

            AnalyzeResponse analyzeResponse = getClient().admin().indices()
                    .prepareAnalyze(metadata.getIndex().indexName(), keyword).setAnalyzer("standard").execute().actionGet();

            if(analyzeResponse!=null && CollectionUtils.isNotEmpty(analyzeResponse.getTokens())){

                for(AnalyzeResponse.AnalyzeToken analyzeToken: analyzeResponse.getTokens()){
                    final String term = analyzeToken.getTerm();
                    result.add(term);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("can't parse '%s'", keyword), e);
        }
        return result;
    }

    private XContentBuilder buildMapping(ElasticsearchMetadata indexMetadata) {
        XContentBuilder builder = null;
        ElasticsearchMapping mapping = indexMetadata.getTypeMapping();
        try {
            builder = XContentFactory.contentBuilder(XContentType.JSON)
                                     .startObject()
                                     .startObject(mapping.typeName())
                                     .startObject("_source")
                                     .field("enabled", String.valueOf(mapping.source().getValue()));
            builder.endObject();

            if (StringUtils.isNotBlank(mapping.parent())) {
                builder = builder.startObject("_parent").field("type", mapping.parent())
                                 .endObject();
            }


            builder = builder.startObject("properties");

            // Manage fields
            if (CollectionUtils.isNotEmpty(indexMetadata.getIndexedFields())) {
                for (ElasticsearchFieldMetadata field : indexMetadata.getIndexedFields()) {
                    builder = buildField(field, builder);
                }
            }

            builder.endObject().endObject().endObject();

            // Prints generated mapping
            logger.info(String.format("Mapping [%s]:\r\n%s\r\n", mapping.typeName(), builder.string()));

        } catch (Exception e) {
            logger.error("Exception when building mapping for type " + mapping.typeName() + ": " + e.getMessage());
        }

        return builder;
    }

    private XContentBuilder buildField(ElasticsearchFieldMetadata field, XContentBuilder builder) throws Exception{
        builder = builder.startObject(field.getName()).field("type", field.getType().toString().toLowerCase())
                         .field("store", String.valueOf(field.getStore().getValue()));

        if (!field.getIndex().equals(Index.Undefined)) {
            builder.field("index", field.getIndex().toString().toLowerCase());
        }

        if (StringUtils.isNotBlank(field.getAnalyzerName())) {
            builder.field("analyzer", field.getAnalyzerName().toLowerCase());
        }

        if (StringUtils.isNotBlank(field.getIndexAnalyzerName())) {
            builder.field("index_analyzer", field.getIndexAnalyzerName().toLowerCase());
        }

        if (StringUtils.isNotBlank(field.getSearchAnalyzerName())) {
            builder.field("search_analyzer", field.getSearchAnalyzerName().toLowerCase());
        }
        builder = builder.endObject();
        return builder;
    }

    /**
     * Put index mapping
     *
     * @param indexName
     * @param type
     * @param mappingBuilder
     * @throws org.elasticsearch.ElasticsearchException
     * @throws Exception
     */
    private void putIndexMapping(String indexName, String type, XContentBuilder mappingBuilder) throws  Exception {
        PutMappingResponse response = this.getClient().admin().indices().preparePutMapping(indexName)
                                          .setType(type).setSource(mappingBuilder)
                                          .execute().actionGet();
        if (!response.isAcknowledged()) {
            throw new Exception("Could not put mapping [" + type + "] for index [" + indexName + "]");
        }
    }

    /**
     * Delete all documents in the index
     *
     * @param indexName
     * @throws Exception
     */
    private void clean(String indexName) throws Exception {
        this.getClient().prepareDeleteByQuery(indexName).setQuery(QueryBuilders.matchAllQuery())
            .execute().actionGet();
    }

    /**
     * Delete an index
     *
     * @param indexName
     * @throws Exception
     */
    private void deleteIndex(String indexName) throws Exception {
        DeleteIndexResponse response = this.getClient().admin().indices().prepareDelete(indexName).execute().actionGet();
        if (!response.isAcknowledged()) {
            throw new Exception("Could not delete index [" + indexName + "]");
        }
    }

    private void deleteMapping(String indexName, String typeName) throws Exception {
        DeleteMappingResponse response = this.getClient().admin().indices().prepareDeleteMapping(indexName).setType(typeName).execute().actionGet();
        if (!response.isAcknowledged()) {
            throw new Exception("Could not delete type [" + typeName + "]");
        }
    }

    /**
     * Create an index
     *
     * @param indexName
     * @param settings
     * @throws Exception
     */
    private void createIndex(String indexName, Settings settings) throws Exception {
        CreateIndexRequestBuilder builder = this.getClient().admin().indices().prepareCreate(indexName);
        if (settings != null) {
            builder.setSettings(settings);
        }
        CreateIndexResponse response = builder.execute().actionGet();
        if (!response.isAcknowledged()) {
            throw new Exception("Could not create index [" + indexName + "]");
        }
    }

    private boolean isIndexExists(String indexName) throws Exception{
        IndicesExistsResponse existResponse = this.getClient().admin().indices().prepareExists(indexName).execute()
                .actionGet();
        return existResponse!=null && existResponse.isExists();
    }

    private Settings buildIndexSettings(ElasticsearchMetadata indexMetadata) {
        // Build default settings
        ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();

        // Manage analysis filters & tokenizers
        ElasticsearchAnalysis analysis = indexMetadata.getIndex().analysis();
        if (analysis != null && (analysis.filters().length > 0 || analysis.analyzers().length > 0)) {
            for (ElasticsearchFilter filter : analysis.filters()) {
                String prefix = "index.analysis.filter." + filter.name();
                settingsBuilder.put(prefix + ".type", filter.typeName());
                for (ElasticsearchSetting setting : filter.settings()) {
                    settingsBuilder.put(prefix + "." + setting.name(), setting.value());
                }
            }
            for (ElasticsearchAnalyzer analyzer : analysis.analyzers()) {
                String prefix = "index.analysis.analyzer." + analyzer.name();
                settingsBuilder.put(prefix + ".tokenizer", analyzer.tokenizer());
                if (analyzer.filtersNames() != null && analyzer.filtersNames().length > 0) {
                    settingsBuilder.putArray(prefix + ".filter", analyzer.filtersNames());
                }
            }
        }
        // Other settings
        ElasticsearchSetting[] indexSettings = indexMetadata.getIndex().settings();
        for (ElasticsearchSetting setting : indexSettings) {
            settingsBuilder.put(setting.name(), setting.value());
        }
        // Build the settings
        return settingsBuilder.build();
    }


    private List<Field> getDeclaredFields(Class<?> entityClass) {
        List<Field> result = new ArrayList<>();
        Class<?> parentClass  = entityClass.getSuperclass();
        if(parentClass!=null){
            result.addAll(getDeclaredFields(parentClass));
        }
        Field[] thisFields=entityClass.getDeclaredFields();
        if(thisFields!=null && thisFields.length>0){
            result.addAll(Arrays.asList(thisFields));
        }
        return result;
    }

    private <A extends Annotation> boolean isFieldAnnotated(Field field, Class<A> annotationClass){
        return getFieldAnnotation(field, annotationClass)!=null;
    }


    private <A extends Annotation> A getFieldAnnotation(Field field, Class<A> annotationClass){
        return field.getAnnotation(annotationClass);
    }

    private void mapFieldAnnotation(Field field, ElasticsearchField annotation, ElasticsearchMetadata metadata) throws Exception{
        if(annotation!=null){
            Class bridgeClazz = annotation.bridge().impl();
            ElasticsearchBrigde bridge = null;
            if(void.class.isAssignableFrom(bridgeClazz)){
                bridge = null;
            } else if(!ElasticsearchBrigde.class.isAssignableFrom(bridgeClazz)){
                logger.warn(String.format("Elasticsearch Bridge for fiels %s should implement %s interface", field.getName(), ElasticsearchBrigde.class.getName()));
            }else{
                bridge = (ElasticsearchBrigde)bridgeClazz.newInstance();
            }

            metadata.addField(new ElasticsearchFieldMetadata(false, field, annotation.name(), annotation.type(), annotation.store(), annotation.index(),
                    annotation.analyzerName(),annotation.indexAnalyzerName(),annotation.searchAnalyzerName(),
                    bridge,annotation.mapToParent()));
        }
    }

    @Override
    public void destroy() throws Exception {
        if(clientFactory!=null)
            clientFactory.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if("external".equals(clientType)){
            if(StringUtils.isBlank(this.esNodes))
                throw new Exception("Elasticsearch nodes addresses not set");

            String[] esNodeArray = esNodes.split(",");
            this.clientFactory = new ESTransportClientFactoryBean(clusterName, esNodeArray);
        } else {
            // embedded
            this.clientFactory = new ESClientFactoryBean(new ESNodeFactoryBean(clusterName, this.hibernateProperties));
        }
        this.clientFactory.afterPropertiesSet();
    }
}
