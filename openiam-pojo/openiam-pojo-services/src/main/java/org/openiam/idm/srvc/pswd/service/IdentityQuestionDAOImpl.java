package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation object for the domain model class IdentityQuestion.
 */
@Repository("identityQuestDAO")
public class IdentityQuestionDAOImpl extends BaseDaoImpl<IdentityQuestionEntity, String> implements IdentityQuestionDAO {

	private static final Log log = LogFactory.getLog(IdentityQuestionDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(final IdentityQuestionEntity example) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotEmpty(example.getUserId())) {
			criteria.add(Restrictions.eq("userId", example.getUserId()));
		}
		if(example.getIdentityQuestGrp() != null) {
			criteria.add(Restrictions.eq("identityQuestGrp.identityQuestGrpId", example.getIdentityQuestGrp().getId()));
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
