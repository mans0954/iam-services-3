package org.openiam.idm.srvc.pswd.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
	protected Criteria getExampleCriteria(final UserIdentityAnswerEntity example) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(example.getUserId())) {
			criteria.add(Restrictions.eq("userId", example.getUserId()));
		}
		if(example.getIdentityQuestion() != null) {
			criteria.add(Restrictions.eq("identityQuestion.id", example.getIdentityQuestion().getId()));
		}
		return criteria.addOrder(Order.asc("identityQuestion.id"));
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

	@Override
	public List<UserEntity> findUsersWithoutAnswers(){
		String sql = new String(" from org.openiam.idm.srvc.user.domain.UserEntity u "
				+ "where u.id not in (select uia.userId from org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity uia)");
		Session session = getSession();
		Query qry = session.createQuery(sql);

		List<UserEntity> results = (List<UserEntity>) qry.setCacheable(this.cachable()).list();
		if (results == null) {
			return (new ArrayList<UserEntity>());
		}
		return results;

	}

	@Override
	public List<String> findUsersIdWithoutAnswers(){
		String sql = new String("select u.id from org.openiam.idm.srvc.user.domain.UserEntity u "
				+ "where u.id not in (select uia.userId from org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity uia)");
		Session session = getSession();
		Query qry = session.createQuery(sql);

		List<String> results = (List<String>) qry.setCacheable(this.cachable()).list();
		if (results == null) {
			return (new ArrayList<String>());
		}
		return results;

	}

    @Override
	public List<Map<String,Object>> findUsersWithoutAnswersOnDate(Date fromDate, Date toDate, boolean hasAnswer){

		String dateFilter = "";
		if (fromDate != null && toDate != null ) {
			dateFilter = "and u.createDate > :fromDate and u.createDate < :toDate";
		} else if (fromDate != null ) {
			dateFilter = "and u.createDate >= :fromDate";
		} else if (toDate != null ) {
			dateFilter = "and u.createDate <= :toDate";
		}

		String sql = new String("SELECT new map(u.id as id, u.firstName as firstName, u.lastName as lastName, u.status as status, l.login as login) "
				+ "from org.openiam.idm.srvc.user.domain.UserEntity u, org.openiam.idm.srvc.auth.domain.LoginEntity l "
				+ "where u.id=l.userId and l.managedSysId=:defMan and u.id " + (hasAnswer?"":"not") + " in (select uia.userId from org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity uia) "
				+ dateFilter);
		Session session = getSession();
		Query qry = session.createQuery(sql);
		if (fromDate != null && toDate != null ) {
			qry.setDate("fromDate", fromDate);
			qry.setDate("toDate", toDate);
		} else if (fromDate != null ) {
			qry.setDate("fromDate", fromDate);
		} else if (toDate != null ) {
			qry.setDate("toDate", toDate);
		}
		qry.setString("defMan","0");

		List<Map<String,Object>> results = (List<Map<String,Object>>) qry.setCacheable(this.cachable()).list();
		if (results == null) {
			return (new ArrayList<Map<String,Object>>());
		}
		return results;
	}

}
