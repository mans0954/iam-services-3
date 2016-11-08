package org.openiam.elasticsearch.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OpeniamElasticSearchRepository<T, ID extends Serializable> extends ElasticsearchRepository<T, ID> {

	Class<T> getDocumentClass();
}
