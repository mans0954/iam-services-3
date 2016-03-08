package org.openiam.idm.srvc.pswd.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("identityAnswerDAO")
public class UserIdentityAnswerDAOImpl extends BaseDaoImpl<UserIdentityAnswerEntity, String> implements UserIdentityAnswerDAO {

	private static final Log log = LogFactory.getLog(UserIdentityAnswerDAOImpl.class);
	
	private static String DELETE_BY_QUESTION_ID = "DELETE FROM %s ans WHERE ans.identityQuestion.id = :questionId";
    private static String DELETE_BY_USER_ID = "DELETE FROM %s ans WHERE ans.userId = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_QUESTION_ID = String.format(DELETE_BY_QUESTION_ID, domainClass.getSimpleName());
        DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
	}
	
	

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof IdentityAnswerSearchBean) {
			final IdentityAnswerSearchBean sb = (IdentityAnswerSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getUserId())) {
				criteria.add(Restrictions.eq("userId", sb.getUserId()));
			}
			if(sb.getQuestionId() != null) {
				criteria.add(Restrictions.eq("identityQuestion.id", sb.getQuestionId()));
			}
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public void deleteAnswersByQuestionId(String questionId) {
		final Query query = getSession().createQuery(DELETE_BY_QUESTION_ID);
		query.setParameter("questionId", questionId);
		query.executeUpdate();
	}
    @Override
    @Transactional
    public void deleteByUser(String userId){
        final Query query = getSession().createQuery(DELETE_BY_USER_ID);
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}
