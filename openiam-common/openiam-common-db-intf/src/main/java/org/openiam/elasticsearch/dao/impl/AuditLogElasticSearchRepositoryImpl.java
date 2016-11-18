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
import org.hibernate.jpa.criteria.CriteriaQueryImpl;
import org.openiam.base.Tuple;
import org.openiam.elasticsearch.dao.AuditLogElasticSearchRepositoryCustom;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
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
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Repository
public class AuditLogElasticSearchRepositoryImpl extends AbstractElasticSearchRepository<IdmAuditLogEntity, String, AuditLogSearchBean> implements AuditLogElasticSearchRepositoryCustom {

	
	@Override
	protected CriteriaQuery getCriteria(final AuditLogSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {

			if(StringUtils.isNotBlank(searchBean.getContentProviderId())) {
				final Criteria criteria = eq("contentProviderId", searchBean.getContentProviderId());
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			if(StringUtils.isNotBlank(searchBean.getAuthProviderId())) {
				final Criteria criteria = eq("authProviderId", searchBean.getAuthProviderId());
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			if(StringUtils.isNotBlank(searchBean.getUriPatternId())) {
				final Criteria criteria = eq("uriPatternId", searchBean.getUriPatternId());
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
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
            	final Criteria criteria = lt("timestamp", searchBean.getTo());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
                final Criteria criteria = eq("managedSysId", searchBean.getManagedSysId());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getSource())) {
                final Criteria criteria = eq("source", searchBean.getSource());
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
            	final Criteria criteria = getAuditLogIdsForTargetIdAndType(searchBean.getTargetId(), searchBean.getTargetType());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(StringUtils.isNotBlank(searchBean.getSecondaryTargetId()) && StringUtils.isNotBlank(searchBean.getSecondaryTargetType())) {
            	final Criteria criteria = getAuditLogIdsForTargetIdAndType(searchBean.getSecondaryTargetId(), searchBean.getSecondaryTargetType());
            	query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            }
            
            if(CollectionUtils.isNotEmpty(searchBean.getAttributes())) {
            	for(final Tuple<String, String> tuple : searchBean.getAttributes()) {
            		final String key = tuple.getKey();
            		final String value = tuple.getValue();
            		if(StringUtils.isNotBlank(key)) {
            			Criteria criteria = null;
            			if(StringUtils.isNotBlank(value)) {
            				criteria = eq(String.format("attributes.%s", key), value);
            				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
            			}
            		}
            	}
            }
		}
		return query;
	}

	@Override
	public Class<IdmAuditLogEntity> getDocumentClass() {
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
		}
	}

	@Override
	public Page<IdmAuditLogEntity> find(final AuditLogSearchBean searchBean, final Pageable pageable) {
		final CriteriaQuery criteria = getCriteria(searchBean);
		Page<IdmAuditLogEntity> retval = null;
		if(criteria != null) {
			criteria.setPageable(pageable);
			retval = elasticSearchTemplate.queryForPage(criteria, getDocumentClass());
		} else {
			retval = new PageImpl<IdmAuditLogEntity>(Collections.EMPTY_LIST);
		}
		return retval;
	}
	
	private Criteria getAuditLogIdsForTargetIdAndType(final String targetId, final String targetType) {
		return eq("targets.targetId", targetId).and(eq("targets.targetType", targetType));
		/*
		final BoolQueryBuilder boolQuery = targetTypeQuery(targetId, targetType);
		if(boolQuery != null) {
	    	final NativeSearchQuery nativeQuery = new NativeSearchQuery(boolQuery);
	    	nativeQuery.addFields("id");
	    	try {
	    		elasticSearchTemplate.queryForList(nativeQuery, IdmAuditLogEntity.class);
	    	} catch(Throwable e) {
	    		e.printStackTrace();
	    	}
	    	final List<String> ids = elasticSearchTemplate.queryForList(nativeQuery, IdmAuditLogEntity.class).stream().map(e -> e.getId()).collect(Collectors.toList());
	    	return (ids != null) ? ids : Collections.EMPTY_LIST;
		} else {
			return Collections.EMPTY_LIST;
		}
		final QueryBuilder builder = nestedQuery("targets", boolQuery().must(termQuery("targets.targetId", targetId)).must(termQuery("targets.targetType", targetType)));

		final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(builder).build();
		final List<IdmAuditLogEntity> logs = elasticSearchTemplate.queryForList(searchQuery, IdmAuditLogEntity.class);
		if(CollectionUtils.isNotEmpty(logs)) {
			return logs.stream().map(e -> e.getId()).collect(Collectors.toList());
		} else {
			return Collections.EMPTY_LIST;
		}
		*/
	}
	
	/*
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
	*/
}
