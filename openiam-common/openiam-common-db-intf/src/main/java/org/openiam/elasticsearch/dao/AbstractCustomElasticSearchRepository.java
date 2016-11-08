package org.openiam.elasticsearch.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.openiam.base.BaseIdentity;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractCustomElasticSearchRepository<T extends BaseIdentity, S extends AbstractSearchBean, ID extends Serializable> {

	public List<T> findBeans(S searchBean, int from, int size);
	public List<T> findByIds(Collection<String> ids, Pageable pageable);
	public List<String> findIds(final S searchBean, int from, int size);
	public int count(final S searchBean);
	public boolean isValidSearchBean(final S searchBean);
	public Pageable getPageable(final S searchBean, final int from, final int size);
	public boolean allowReindex(final ElasticsearchRepository repo);
	public abstract Class<T> getDocumentClass();
	public void prepare(final T entity);
}
