package org.openiam.idm.srvc.pswd.service;

// Generated Jan 23, 2010 1:06:13 AM by Hibernate Tools 3.2.2.GA

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("passwordHistoryDAO")
public class PasswordHistoryDAOImpl extends BaseDaoImpl<PasswordHistoryEntity, String> implements PasswordHistoryDAO {

	private static final Log log = LogFactory.getLog(PasswordHistoryDAOImpl.class);

	@Override
	public List<PasswordHistoryEntity> getPasswordHistoryByLoginId(final String loginId, final int from, final int size) {
		final Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("loginId", loginId));
		criteria.addOrder(Order.asc("dateCreated"));
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}

    @Override
    public List<PasswordHistoryEntity> getSublist(int startPos, int size) {
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(PasswordHistory.class.getName()).append(" pwd");
        return (List<PasswordHistoryEntity>)getSession().createQuery(sql.toString()).setFirstResult(startPos).setMaxResults(size).list();
    }

    @Override
    public Long getCount() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(pwd.pwdHistoryId) from ").append(PasswordHistory.class.getName()).append(" pwd");
        return (Long)getSession().createQuery(sql.toString()).uniqueResult();
    }

	@Override
	protected String getPKfieldName() {
		return "pwdHistoryId";
	}
}
