package org.openiam.idm.srvc.membership;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.idm.searchbeans.MembershipSearchBean;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;

public abstract class AbstractMembershipHibernateDAO<T extends AbstractMembershipXrefEntity> extends AbstractHibernateSearchDao<T, MembershipSearchBean, String> {

	@Override
	protected QueryBuilder parse(MembershipSearchBean searchBean) {
		BoolQueryBuilder luceneQuery = QueryBuilders.boolQuery();
		
		QueryBuilder clause = buildExactClause("entityId", searchBean.getEntityId());
		if(clause != null) {
			luceneQuery.must(clause);
		}

		clause = buildExactClause("memberEntityId", searchBean.getMemberEntityId());
		if(clause != null) {
			luceneQuery.must(clause);
		}
		
		clause = buildInClause("rightIds", searchBean.getRightIds());
		if(clause != null) {
			luceneQuery.must(clause);
		}
		
		return luceneQuery;
	}
}
