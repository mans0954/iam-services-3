package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation object for the domain model class IdentityQuestion.
 */
@Repository("identityQuestDAO")
public class IdentityQuestionDAOImpl extends OrderDaoImpl<IdentityQuestionEntity, String> implements IdentityQuestionDAO {

	private static final Log log = LogFactory.getLog(IdentityQuestionDAOImpl.class);

/*
	@Autowired
	private IdentityQuestionSearchBeanConverter questionSearchBeanConverter;
*/
@Override
protected Criteria getExampleCriteria(SearchBean searchBean) {
	final Criteria criteria = getCriteria();
	if(searchBean != null && searchBean instanceof IdentityQuestionSearchBean) {
		final IdentityQuestionSearchBean sb = (IdentityQuestionSearchBean)searchBean;
		if (sb.getActive() !=null){
			criteria.add(Restrictions.eq("active", sb.getActive()));
		}
		if(StringUtils.isNotBlank(sb.getGroupId())) {
			criteria.add(Restrictions.eq("identityQuestGrp.id", sb.getGroupId()));
		}
	}
	return criteria;
}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	protected String getReferenceType() {
		return "IdentityQuestionEntity.displayNameMap";
	}
}
