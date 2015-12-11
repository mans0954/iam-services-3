package org.openiam.elasticsearch.dao.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.topChildrenQuery;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TopChildrenQueryBuilder;
import org.openiam.elasticsearch.dao.AuditLogElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogCustomEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<IdmAuditLogEntity, String, AuditLogSearchBean> implements AuditLogElasticSearchRepositoryCustom {

	private String targetLogType;
	
	public AuditLogElasticSearchRepositoryImpl() {
		targetLogType = IdmAuditLogEntity.class.getAnnotation(Document.class).type();
	}
	
	@Override
	protected CriteriaQuery getCriteria(final AuditLogSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			//entity.setId(StringUtils.trimToNull(searchBean.getKey()));
            if(StringUtils.isNotEmpty(searchBean.getAction())) {
            	final Criteria criteria = eq("action", searchBean.getAction());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            if(StringUtils.isNotEmpty(searchBean.getResult())) {
            	final Criteria criteria = eq("result", searchBean.getResult());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(searchBean.getFrom() != null && searchBean.getTo() != null) {
            	final Criteria criteria = between("timestamp", searchBean.getFrom(), searchBean.getTo());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            } else if(searchBean.getFrom() != null) {
            	final Criteria criteria = gt("timestamp", searchBean.getFrom());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            } else if(searchBean.getTo() != null) {
            	final Criteria criteria = lt("timestamp", searchBean.getFrom());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
                final Criteria criteria = eq("managedSysId", searchBean.getManagedSysId());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getSource())) {
                final Criteria criteria = eq("source", searchBean.getManagedSysId());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotEmpty(searchBean.getParentId()) && searchBean.isParentOnly()) {
            	final Criteria criteria = eq("parentId", "null");
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            if(StringUtils.isNotBlank(searchBean.getUserId())) {
            	final Criteria criteria = eq("userId", searchBean.getUserId());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getTargetId()) && StringUtils.isNotBlank(searchBean.getTargetType())) {
            	final List<String> ids = getAuditLogIdsForTargetIdAndType(searchBean.getTargetId(), searchBean.getTargetType());
            	if(CollectionUtils.isNotEmpty(ids)) {
            		final Criteria criteria = inCriteria("id", ids);
            		query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            	} else {
            		/* 
            		 * at this point, the ids collection is empty, so we have no overall results.
            		 * Return null so that the caller handles it
            		 */
            		return null;
            	}
            }
            
            if(StringUtils.isNotBlank(searchBean.getSecondaryTargetId()) && StringUtils.isNotBlank(searchBean.getSecondaryTargetType())) {
            	final List<String> ids = getAuditLogIdsForTargetIdAndType(searchBean.getSecondaryTargetId(), searchBean.getSecondaryTargetType());
            	if(CollectionUtils.isNotEmpty(ids)) {
            		final Criteria criteria = inCriteria("id", ids);
            		query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            	} else {
            		/* 
            		 * at this point, the ids collection is empty, so we have no overall results.
            		 * Return null so that the caller handles it
            		 */
            		return null;
            	}
            }
		}
		return query;
	}

	@Override
	protected Class<IdmAuditLogEntity> getEntityClass() {
		return IdmAuditLogEntity.class;
	}

	@Override
	public void prepare(final IdmAuditLogEntity entity) {
		if(entity != null) {
			/* backwards compatability with 3.x */
			if(CollectionUtils.isNotEmpty(entity.getCustomRecords())) {
				entity.getCustomRecords().forEach(e -> {
					entity.put(e.getKey(), e.getValue());
				});
			}
			super.prepare(entity);
		}
	}

	@Override
	public Page<IdmAuditLogEntity> find(final AuditLogSearchBean searchBean, final Pageable pageable) {
		final CriteriaQuery criteria = getCriteria(searchBean);
		Page<IdmAuditLogEntity> retval = null;
		if(criteria != null) {
			criteria.setPageable(pageable);
			retval = elasticSearchTemplate.queryForPage(criteria, getEntityClass());
		} else {
			retval = new PageImpl<IdmAuditLogEntity>(Collections.EMPTY_LIST);
		}
		return retval;
	}
	
	private List<String> getAuditLogIdsForTargetIdAndType(final String targetId, final String targetType) {
		final BoolQueryBuilder boolQuery = targetTypeQuery(targetId, targetType);
		if(boolQuery != null) {
	    	final NativeSearchQuery nativeQuery = new NativeSearchQuery(boolQuery);
	    	nativeQuery.addFields("id");
	    	final List<String> ids = elasticSearchTemplate.queryForList(nativeQuery, IdmAuditLogEntity.class).stream().map(e -> e.getId()).collect(Collectors.toList());
	    	return (ids != null) ? ids : Collections.EMPTY_LIST;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	private BoolQueryBuilder targetTypeQuery(final String targetId, final String targetType) {
		final QueryBuilder targetIdQuery = childQuery(targetLogType, "targetId", targetId);
		final QueryBuilder targetTypeQuery = childQuery(targetLogType, "targetType", targetType);
		BoolQueryBuilder booleanBuilder = null;
		if(targetIdQuery != null) {
			booleanBuilder = (booleanBuilder != null) ? booleanBuilder.must(targetIdQuery) : boolQuery().must(targetIdQuery);
		}
		if(targetTypeQuery != null) {
			booleanBuilder = (booleanBuilder != null) ? booleanBuilder.must(targetTypeQuery) : boolQuery().must(targetTypeQuery);
		}
		return booleanBuilder;
	}
	
	private QueryBuilder childQuery(final String type, final String term, final String value) {
		if(StringUtils.isNotBlank(value)) {
			final TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(term, value);
			final TopChildrenQueryBuilder childQuery = topChildrenQuery(targetLogType, termQueryBuilder);
			return new NativeSearchQueryBuilder().withQuery(childQuery).build().getQuery();
		} else {
			return null;
		}
	}
}
