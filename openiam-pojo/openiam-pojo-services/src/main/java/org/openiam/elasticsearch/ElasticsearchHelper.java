package org.openiam.elasticsearch;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.client.Client;
import org.openiam.elasticsearch.annotation.ElasticsearchField;
import org.openiam.elasticsearch.annotation.ElasticsearchId;
import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.ElasticsearchType;
import org.openiam.elasticsearch.constants.Index;
import org.openiam.elasticsearch.model.ElasticsearchFieldMetadata;
import org.openiam.elasticsearch.model.ElasticsearchMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by: Alexander Duckardt
 * Date: 7/1/14.
 */
@Component
public class ElasticsearchHelper {
    @Autowired
    private ESClientFactoryBean clientFactory;

    private static Map<String, ElasticsearchMetadata> indexeMetadataMap = new HashMap<>();

    private Client getClient() throws Exception {
        return this.clientFactory.getObject();
    }




    public ElasticsearchMetadata getIndexMetadata(Class<?> entityClass) throws Exception {
        ElasticsearchMetadata result = indexeMetadataMap.get(entityClass.getSimpleName());
        if(result==null){
            result = new ElasticsearchMetadata();


            // collect fields metadata
            boolean hasId=false;
            List<Field> declaredFields = getDeclaredFields(entityClass);
            if(CollectionUtils.isNotEmpty(declaredFields)){
                for(Field field: declaredFields){
                    Annotation annotation =  getFieldAnnotation(field, ElasticsearchId.class);
                    if(annotation!=null){
                        hasId=true;
                        result.addField(new ElasticsearchFieldMetadata(true, field.getName(), ElasticsearchType.String, ElasticsearchStore.Yes, Index.Not_Analyzed));
                        continue;
                    }
                    annotation =  getFieldAnnotation(field, ElasticsearchField.class);
                    if(annotation!=null){
                        ElasticsearchField esAnnotation = (ElasticsearchField)annotation;
                        result.addField(new ElasticsearchFieldMetadata(false, esAnnotation.name(), esAnnotation.type(), esAnnotation.store(), esAnnotation.index(),
                                                                       esAnnotation.analyzerName(),esAnnotation.indexAnalyzerName(),esAnnotation.searchAnalyzerName()));
                    }
                }
            }
            if(!hasId)
                throw new NullPointerException(String.format("Class %s does not have field mapped with annotation %s.", entityClass.getSimpleName(), ElasticsearchId.class.getSimpleName()));
            indexeMetadataMap.put(entityClass.getSimpleName(), result);
        }
        return result;
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
}
