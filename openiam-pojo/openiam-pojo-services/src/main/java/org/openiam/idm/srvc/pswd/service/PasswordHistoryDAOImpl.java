package org.openiam.idm.srvc.pswd.service;

// Generated Jan 23, 2010 1:06:13 AM by Hibernate Tools 3.2.2.GA

import java.util.Date;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

@Repository("passwordHistoryDAO")
public class PasswordHistoryDAOImpl extends BaseDaoImpl<PasswordHistoryEntity, String> implements PasswordHistoryDAO {

	private static final Log log = LogFactory.getLog(PasswordHistoryDAOImpl.class);


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO#findPasswordHistoryByPrincipal(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<PasswordHistoryEntity> findPasswordHistoryByPrincipal(
			String domainId, String principal, String managedSys, int versions) {
		log.debug("getting PwdHistoryByPrinciPal instance with id: " + principal);
		try {
			Session session = sessionFactory.getCurrentSession();
			Query qry = session.createQuery("from PasswordHistoryEntity ph "
					+ " where ph.serviceId = :domainId and " +
					  " ph.managedSysId = :managedSys and " +
					  " ph.login = :principal " +
					  " order by ph.dateCreated desc ");
			qry.setString("domainId", domainId);
			qry.setString("managedSys", managedSys);
			qry.setString("principal", principal);

			qry.setFetchSize(versions);
			qry.setMaxResults(versions);	
			
			List<PasswordHistoryEntity> result = (List<PasswordHistoryEntity>) qry.list();
			if (result == null || result.size() == 0)
				return null;
			return result;
			
		}catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}
	}

    /* (non-Javadoc)
	 * @see org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO#findPasswordHistoryByPrincipal(java.lang.String, java.lang.String, java.lang.String)
	 */
    public List<PasswordHistoryEntity> findAllPasswordHistoryByPrincipal(
            String domainId, String principal, String managedSys) {
        log.debug("getting PwdHistoryByPrinciPal instance with id: " + principal);
        try {
            Session session = sessionFactory.getCurrentSession();
            Query qry = session.createQuery("from PasswordHistoryEntity ph "
                                            + " where ph.serviceId = :domainId and " +
                                            " ph.managedSysId = :managedSys and " +
                                            " ph.login = :principal " +
                                            " order by ph.dateCreated desc ");
            qry.setString("domainId", domainId);
            qry.setString("managedSys", managedSys);
            qry.setString("principal", principal);

            List<PasswordHistoryEntity> result = (List<PasswordHistoryEntity>) qry.list();
            if (result == null || result.size() == 0)
                return null;
            return result;

        }catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public List<PasswordHistoryEntity> getSublist(int startPos, int size) {
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(PasswordHistory.class.getName()).append(" pwd");
        return (List<PasswordHistoryEntity>)sessionFactory.getCurrentSession().createQuery(sql.toString()).setFirstResult(startPos).setMaxResults(size).list();
    }

    @Override
    public Long getCount() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(pwd.pwdHistoryId) from ").append(PasswordHistory.class.getName()).append(" pwd");
        return (Long)sessionFactory.getCurrentSession().createQuery(sql.toString()).uniqueResult();
    }

	@Override
	protected String getPKfieldName() {
		return "pwdHistoryId";
	}
}
