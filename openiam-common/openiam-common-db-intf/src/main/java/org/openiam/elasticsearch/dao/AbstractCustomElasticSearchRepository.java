package org.openiam.elasticsearch.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.openiam.base.BaseIdentity;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractCustomElasticSearchRepository<T extends BaseIdentity, S extends SearchBean, ID extends Serializable> {

	public List<T> findBeans(S searchBena, Pageable pageable);
	public List<T> findByIds(Collection<String> ids, Pageable pageable);
	public List<String> findIds(final S searchBean, Pageable pageable);
	public int count(final S searchBean);
}
