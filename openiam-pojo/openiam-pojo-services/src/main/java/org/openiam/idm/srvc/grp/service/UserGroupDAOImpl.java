package org.openiam.idm.srvc.grp.service;

import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.springframework.stereotype.Repository;

@Repository("userGroupDAO")
public class UserGroupDAOImpl extends BaseDaoImpl<UserGroupEntity, String> implements UserGroupDAO {

	private static final Log log = LogFactory.getLog(UserGroupDAOImpl.class);
	
	private static String DELETE_BY_GROUP_ID = "DELETE FROM %s ug WHERE ug.grpId = :groupId";
	private static String DELETE_BY_USER_ID = "DELETE FROM %s ug WHERE ug.userId = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
		DELETE_BY_GROUP_ID = String.format(DELETE_BY_GROUP_ID, domainClass.getSimpleName());
	}

    @Override
    protected String getPKfieldName() {
        return "userGrpId";
    }

	@Override
	public void deleteByGroupId(String groupId) {
		final Query query = getSession().createQuery(DELETE_BY_GROUP_ID);
		query.setParameter("groupId", groupId);
		query.executeUpdate();
	}

	@Override
	public void deleteByUserId(String userId) {
		final Query query = getSession().createQuery(DELETE_BY_USER_ID);
		query.setParameter("userId", userId);
		query.executeUpdate();
	}

	@Override
	public UserGroupEntity getRecord(String groupId, String userId) {
		final Criteria criteria = getCriteria().add(Restrictions.eq("grpId", groupId)).add(Restrictions.eq("userId", userId));
		return (UserGroupEntity)criteria.uniqueResult();
	}
}
