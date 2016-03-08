package org.openiam.idm.srvc.membership;

import org.openiam.elasticsearch.dao.impl.AbstractElasticSearchRepository;
import org.openiam.idm.searchbeans.MembershipSearchBean;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

public abstract class AbstractMembershipElasticSearchRepository<T extends AbstractMembershipXrefEntity> extends AbstractElasticSearchRepository<T, String, MembershipSearchBean> {

	@Override
	protected CriteriaQuery getCriteria(final MembershipSearchBean searchBean) {
		CriteriaQuery query = null;
		if(searchBean != null) {
			Criteria criteria = exactCriteria("entityId", searchBean.getEntityId());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = exactCriteria("memberEntityId", searchBean.getMemberEntityId());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
			
			criteria = inCriteria("rightIds", searchBean.getRightIds());
			if(criteria != null) {
				query = (query != null) ? query.addCriteria(criteria) : new CriteriaQuery(criteria);
			}
		}		
		return query;
	}
}
