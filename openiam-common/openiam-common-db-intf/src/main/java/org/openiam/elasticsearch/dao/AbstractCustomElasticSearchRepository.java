package org.openiam.elasticsearch.dao;

import java.io.Serializable;
import java.util.List;

import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractCustomElasticSearchRepository<S extends SearchBean, ID extends Serializable> {

	public List<String> findIds(final S searchBean, Pageable pageable);
	public int count(final S searchBean);
}
