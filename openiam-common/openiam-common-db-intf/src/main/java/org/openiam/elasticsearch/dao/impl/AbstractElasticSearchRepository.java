package org.openiam.elasticsearch.dao.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.base.ws.MatchType;
import org.openiam.elasticsearch.dao.AbstractCustomElasticSearchRepository;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

public abstract class AbstractElasticSearchRepository<T extends BaseIdentity, ID extends Serializable, S extends SearchBean> 
implements AbstractCustomElasticSearchRepository<S, ID>{
	
	protected AbstractElasticSearchRepository() {
		document = getEntityClass().getAnnotation(Document.class);
		if(document == null) {
			throw new RuntimeException(String.format("No %s Annotation for %s", Document.class, getEntityClass()));
		}
	}

	@Autowired
	protected ElasticsearchTemplate elasticSearchTemplate;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	protected Document document;
	
	protected abstract CriteriaQuery getCriteria(final S searchBean);
	protected abstract Class<T> getEntityClass();
	
	public boolean allowReindex() {
		return true;
	}
	
	public void prepare(final T entity) {
		
	}
	
	protected Criteria inCriteria(final String term, final Collection<String> values) {
		return (CollectionUtils.isNotEmpty(values)) ? Criteria.where(term).in(values) : null;
	}
	
	protected Criteria exactCriteria(final String term, final String value) {
		return (StringUtils.isNotBlank(value)) ? Criteria.where(term).is(StringUtils.trimToNull(value)) : null;
	}
	
	protected Criteria between(String term, final Date from, final Date to) {
		return Criteria.where(term).between(from.getTime(), to.getTime());
	}
	
	protected Criteria gt(final String term, final Date value) {
		return Criteria.where(term).greaterThan(value.getTime());
	}
	
	protected Criteria lt(final String term, final Date value) {
		return Criteria.where(term).lessThan(value.getTime());
	}
	
	protected Criteria eq(String term, final String value) {
		return getWhereCriteria(term, value, MatchType.EXACT);
	}

	protected Criteria getWhereCriteria(String term, final String value, MatchType matchType) {
		Criteria criteria = null;
		if(matchType != null) {
			if(StringUtils.isNotBlank(value)) {
		        switch (matchType){
		            case END_WITH:
		            	criteria = Criteria.where(term).endsWith(StringUtils.trimToNull(value));
		            	break;
		            case STARTS_WITH:
		            	criteria = Criteria.where(term).startsWith(StringUtils.trimToNull(value));
		            	break;
		            case CONTAINS:
		            	criteria = Criteria.where(term).contains(StringUtils.trimToNull(value));
		            	break;
		            case EXACT:
		            	criteria = exactCriteria(term, StringUtils.trimToNull(value));
		            	break;
		        }
			}
		}
        return criteria;
    }

	@Override
	public List<String> findIds(S searchBean, Pageable pageable) {
		List<String> retval = Collections.EMPTY_LIST;
		final CriteriaQuery criteria = getCriteria(searchBean);
		if(criteria != null) {
			criteria.addIndices(document.indexName());
			criteria.addTypes(document.type());
			criteria.addFields("id");
			criteria.setPageable(pageable);
			retval = elasticSearchTemplate.queryForList(criteria, getEntityClass()).stream().map(e -> e.getId()).collect(Collectors.toList());
		}
		return retval;
	}
	
	@Override
	public int count(final S searchBean) {
		final CriteriaQuery criteria = getCriteria(searchBean);
		int count = 0;
		if(criteria != null) {
			criteria.addIndices(document.indexName());
			criteria.addTypes(document.type());
			count = Long.valueOf(elasticSearchTemplate.count(criteria)).intValue();
		}
		return count;
	}
}
