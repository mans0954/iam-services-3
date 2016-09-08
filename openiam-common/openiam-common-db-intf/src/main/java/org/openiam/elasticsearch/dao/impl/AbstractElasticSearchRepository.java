package org.openiam.elasticsearch.dao.impl;

import java.io.Serializable;
import java.util.Arrays;
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
import org.openiam.base.OrderConstants;
import org.openiam.base.domain.KeyEntity;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SortParam;
import org.openiam.elasticsearch.dao.AbstractCustomElasticSearchRepository;
import org.openiam.elasticsearch.dao.OpeniamElasticSearchRepository;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public abstract class AbstractElasticSearchRepository<T extends BaseIdentity, ID extends Serializable, S extends AbstractSearchBean> 
implements AbstractCustomElasticSearchRepository<T, S, ID>{
	
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
	
	public boolean allowReindex(final ElasticsearchRepository repo) {
		return (repo != null) ? (repo.count() <= 0) : false;
	}
	
	public void prepare(final T entity) {
		
	}
	
	@Override
	public boolean isValidSearchBean(final S searchBean) {
		return getCriteria(searchBean) != null;
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
	
	protected Criteria startsWith(final String term, final String value) {
		return getWhereCriteria(term, value, MatchType.STARTS_WITH);
	}
	
	private List<String> parse(final String str) {
		final List<String> query;
		if (str == null) {
			query = Collections.<String>emptyList();
		} else {
			query = Arrays.asList(StringUtils.split(str, " ")).stream().filter(e -> StringUtils.isNotBlank(e)).collect(Collectors.toList());
		}
		return query;
	}
	
	protected Criteria buildStartsWithCritria(final String field, final String value) {
		Criteria conditions = null;
		if (StringUtils.isNotBlank(value)) {
			if (StringUtils.contains(value, " ")) {
				final List<String> parsed = parse(value);
				for(int i = 0; i < parsed.size(); i++) {
					final String term = StringUtils.trimToNull(parsed.get(i));
					if(term != null) {
						if(conditions == null) {
							conditions = new Criteria(field).startsWith(term).boost(parsed.size() - i);
						} else {
							conditions.and(new Criteria(field).contains(term)).boost(parsed.size() - i);
						}
					}
				}
			} else {
				conditions = new Criteria(field).startsWith(value);
			}
		}
		return conditions;
	}
	
	protected Criteria buildContainsCriteria(final String field, final String value) {
		Criteria conditions = null;
		if (StringUtils.isNotBlank(value)) {
			if (StringUtils.contains(value, " ")) {
				final List<String> parsed = parse(value);
				for(int i = 0; i < parsed.size(); i++) {
					final String term = StringUtils.trimToNull(parsed.get(i));
					if(term != null) {
						if(conditions == null) {
							conditions = new Criteria(field).contains(term).boost(parsed.size() - i);
						} else {
							conditions.and(new Criteria(field).contains(term)).boost(parsed.size() - i);
						}
					}
				}
			} else {
				conditions = new Criteria(field).contains(value);
			}
		}
		return conditions;
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
		            	criteria = buildStartsWithCritria(term, value);
		            	break;
		            case CONTAINS:
		            	criteria = buildContainsCriteria(term, value);
		            	break;
		            case EXACT:
		            	criteria = exactCriteria(term, StringUtils.trimToNull(value));
		            	break;
		        }
			}
		}
        return criteria;
    }
	
	protected Sort getSort(final S searchBean) {
		Sort sortBy = null;
		final List<SortParam> sortParamList = searchBean.getSortBy();
		if(CollectionUtils.isNotEmpty(sortParamList)) {
			for (final SortParam sort : sortParamList) {
				final Direction direction = OrderConstants.ASC.equals(sort.getOrderBy()) ? Direction.ASC : Direction.DESC;
				final Sort currentSort = new Sort(direction, sort.getSortBy());
				if(sortBy == null) {
					sortBy = currentSort;
				} else {
					sortBy.and(currentSort);
				}
			}
		}
		return sortBy;
	}
	
	public Pageable getPageable(final S searchBean, final int from, final int size) {
		return new PageRequest(Math.floorDiv(from, size), size, getSort(searchBean));
	}

	@Override
	public List<String> findIds(S searchBean, int from, int size) {
		final Pageable pageable = getPageable(searchBean, from, size);
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
	public List<T> findByIds(Collection<String> ids, Pageable pageable) {
		final CriteriaQuery criteria = new CriteriaQuery(new Criteria("id").in(ids));
		criteria.addIndices(document.indexName());
		criteria.addTypes(document.type());
		criteria.setPageable(pageable);
		final List<T> retval = elasticSearchTemplate.queryForList(criteria, getEntityClass()).stream().collect(Collectors.toList());
		return retval;
	}
	
	
	
	@Override
	public List<T> findBeans(S searchBean, int from, int size) {
		final Pageable pageable = getPageable(searchBean, from, size);
		List<T> retval = Collections.EMPTY_LIST;
		CriteriaQuery criteria = getCriteria(searchBean);
		if(criteria != null) {
			criteria.addIndices(document.indexName());
			criteria.addTypes(document.type());
			criteria.setPageable(pageable);
			retval = elasticSearchTemplate.queryForList(criteria, getEntityClass()).stream().collect(Collectors.toList());
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
