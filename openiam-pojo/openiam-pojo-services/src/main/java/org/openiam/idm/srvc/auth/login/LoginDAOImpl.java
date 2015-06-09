package org.openiam.idm.srvc.auth.login;

// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanClause;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository("loginDAO")
public class LoginDAOImpl extends BaseDaoImpl<LoginEntity, String> implements LoginDAO {
	
	/* DO NOT MERGE INTO 4.0!!!!  Only for 3.3.1 to solve IDMAPPS-2735.  Delete this function */
	@Override
	public List<String> getUserIds(LoginSearchBean searchBean) {
		return getExampleCriteria(searchBean).setProjection(Projections.property("userId")).list();
	}
	
	/* DO NOT MERGE INTO 4.0!!!!  Only for 3.3.1 to solve IDMAPPS-2735.  Delete this function */
    @Override
	protected Criteria getExampleCriteria(final SearchBean sb) {
    	final Criteria criteria = super.getCriteria();
    	if(sb != null) {
    		if(sb instanceof LoginSearchBean) {
    			final LoginSearchBean searchBean = (LoginSearchBean)sb;
    			
    			final SearchParam param = searchBean.getLoginMatchToken();
    			if(param != null && param.isValid()) {
    				final String value = StringUtils.trimToNull(param.getValue());
    				if(value != null) {
	    				switch(param.getMatchType()) {
	    					case EXACT:
	    						criteria.add(Restrictions.eq("lowerCaseLogin", StringUtils.lowerCase(value)));
	    						break;
	    					case STARTS_WITH:
	    						criteria.add(Restrictions.ilike("lowerCaseLogin", value.toLowerCase(), MatchMode.START));
	    						break;
	    					default:
	    						break;
	    				}
    				}
    			}
    			
    			if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
    				criteria.add(Restrictions.eq("managedSysId", searchBean.getManagedSysId()));
    			}
    			
    			if(StringUtils.isNotBlank(searchBean.getUserId())) {
    				criteria.add(Restrictions.eq("userId", searchBean.getUserId()));
    			}
    		}
    	}
    	return criteria;
	}

	private static final Log log = LogFactory.getLog(LoginDAOImpl.class);
    @Override
    public int changeIdentity(String principal, String pswd, String userId,
            String managedSysId) {
        Session session = getSession();
        String hq = " UPDATE LoginEntity l " + " set l.login = :principal,  "
                + "     l.password = :pswd," + "	   l.passwordChangeCount = 0,"
                + " 	   l.isLocked = 0, " + "	   l.authFailCount = 0	 "
                + " where l.managedSysId = :managedSysId and "
                + "		 l.userId = :userId";
        Query qry = session.createQuery(hq);
        qry.setString("userId", userId);
        qry.setString("principal", principal);
        qry.setString("pswd", pswd);
        qry.setString("managedSysId", managedSysId);
        return qry.executeUpdate();
    }
    @Override
    public LoginEntity getRecord(final String login, final String managedSysId) {
        return (LoginEntity) getCriteria()
                .add(Restrictions.eq("lowerCaseLogin",
                        (login != null) ? login.toLowerCase() : null))
                .add(Restrictions.eq("managedSysId", managedSysId)).uniqueResult();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }
    @Override
    public List<LoginEntity> findAllLoginByManagedSys(String managedSysId) {
        return getCriteria().add(Restrictions.eq("managedSysId", managedSysId))
                .list();
    }
    @Override
    public List<LoginEntity> getLoginSublist(int startPos, int size) {
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(LoginEntity.class.getName()).append(" l");
        return (List<LoginEntity>) getSession().createQuery(sql.toString())
                .setFirstResult(startPos).setMaxResults(size).list();
    }
    @Override
    public Long getLoginCount() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(l.password) from ")
                .append(LoginEntity.class.getName()).append(" l");
        return (Long) getSession().createQuery(sql.toString()).uniqueResult();
    }
    @Override
    public List<LoginEntity> findUser(String userId) {
        Session session = getSession();
        Query qry = session
                .createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l "
                        + " where l.userId = :userId order by l.status desc, l.managedSysId asc ");
        qry.setString("userId", userId);
        return (List<LoginEntity>) qry.list();

    }
//    @Override
//    public List<LoginEntity> findLoginByDomain(String domain) {
//        Session session = getSession();
//        Query qry = session
//                .createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l "
//                        + " where l.domainId = :domain ");
//        qry.setString("domain", domain);
//        return (List<LoginEntity>) qry.list();
//
//    }
//    @Override
//    public LoginEntity findLoginByManagedSys(String managedSys, String userId) {
//        Session session = getSession();
//        Query qry = session
//                .createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l "
//                        + " where  l.managedSysId = :managedSys and "
//                        + "  l.userId = :userId ");
//        log.debug("managedSys=" + managedSys + " userId=" + userId);
//        qry.setString("managedSys", managedSys);
//        qry.setString("userId", userId);
//        List<LoginEntity> results = (List<LoginEntity>) qry.list();
//        if (results != null && results.size() > 0) {
//            return results.get(0);
//        }
//        return null;
//    }
    @Override
    public List<LoginEntity> findLoginByManagedSys(String principalName, String managedSysId) {
        Session session = getSession();
        Query qry = session
                .createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l "
                        + " where l.managedSysId = :managedSys and "
                        + "  l.login = :login ");

        qry.setString("managedSys", managedSysId);
        qry.setString("login", principalName);
        return (List<LoginEntity>) qry.list();

    }
    @Override
    public LoginEntity findByPasswordResetToken(String token) {
        Session session = getSession();
        Query qry = session
                .createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l "
                        + " where  l.pswdResetToken = :token  ");

        qry.setString("token", token);
        return (LoginEntity) qry.uniqueResult();
    }
    @Override
    public List<LoginEntity> findLockedUsers(Date startTime) {
        Criteria c = getCriteria().add(
                Restrictions.and(Restrictions.eq("isLocked", 1),
                        Restrictions.ge("lastAuthAttempt", startTime)));
        return c.list();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.login.LoginDAO#bulkUnlock(org.openiam.idm.srvc
     * .user.dto.UserStatusEnum)
     */
    @Override
    public int bulkUnlock(UserStatusEnum status, int autoUnlockTime) {

        log.debug("bulkUnlock operation in LoginDAO called.");
        Session session = getSession();

        String userQry = " UPDATE org.openiam.idm.srvc.user.domain.UserEntity u  "
                + " SET u.secondaryStatus = null "
                + " where u.secondaryStatus = 'LOCKED' and "
                + "       u.id in ("
                + " 	select l.userId from org.openiam.idm.srvc.auth.domain.LoginEntity as l  "
                + "       where l.isLocked = :status and "
                + "             l.lastAuthAttempt <= :policyTime" + "   )";

        String loginQry = " UPDATE org.openiam.idm.srvc.auth.domain.LoginEntity l  "
                + " SET l.isLocked = 0, "
                + "     l.authFailCount = 0 "
                + "       where l.isLocked = :status and "
                + "             l.lastAuthAttempt <= :policyTime";

        Query qry = session.createQuery(userQry);

        Date policyTime = new Date(System.currentTimeMillis());

        log.debug("Auto unlock time:" + autoUnlockTime);

        Calendar c = Calendar.getInstance();
        c.setTime(policyTime);
        c.add(Calendar.MINUTE, (-1 * autoUnlockTime));
        policyTime.setTime(c.getTimeInMillis());

        log.debug("Policy time=" + policyTime.toString());

        int statusParam = 0;
        if (status.equals(UserStatusEnum.LOCKED)) {
            statusParam = 1;
            // qry.setInteger("status", 1);
            log.debug("status=1");
        } else {
            statusParam = 2;
            // qry.setInteger("status", 2);
            log.debug("status=2");
        }

        qry.setInteger("status", statusParam);
        qry.setTimestamp("policyTime", policyTime);
        int rowCount = qry.executeUpdate();

        log.debug("Bulk unlock updated:" + rowCount);

        if (rowCount > 0) {

            Query lQry = session.createQuery(loginQry);
            lQry.setInteger("status", statusParam);
            lQry.setTimestamp("policyTime", policyTime);
            lQry.executeUpdate();

        }

        return rowCount;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.auth.login.LoginDAO#findInactiveUsers(int, int)
     */
    @Override
    public List<LoginEntity> findInactiveUsers(int startDays, int endDays,
            String managedSysId) {
        log.debug("findInactiveUsers called.");
        log.debug("Start days=" + startDays);
        log.debug("End days=" + endDays);

        boolean start = false;
        long curTimeMillis = System.currentTimeMillis();
        Date startDate = new Date(curTimeMillis);
        Date endDate = new Date(curTimeMillis);

        StringBuilder sql = new StringBuilder(
                " from org.openiam.idm.srvc.auth.domain.LoginEntity l where "
                        + " l.managedSysId = :managedSys and ");

        if (startDays != 0) {
            sql.append(" l.lastLogin <= :startDate ");
            start = true;

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DAY_OF_YEAR, (-1 * startDays));
            startDate.setTime(c.getTimeInMillis());

            log.debug("starDate = " + startDate.toString());

        }
        if (endDays != 0) {
            if (start) {
                sql.append(" and ");
            }
            sql.append(" l.lastLogin >= :endDate ");

            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DAY_OF_YEAR, (-1 * endDays));
            endDate.setTime(c.getTimeInMillis());

            log.debug("endDate = " + endDate.toString());

        }

        Session session = getSession();
        Query qry = session.createQuery(sql.toString());

        qry.setString("managedSys", managedSysId);

        if (startDays != 0) {
            qry.setDate("startDate", startDate);
        }
        if (endDays != 0) {
            qry.setDate("endDate", endDate);
        }

        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results == null) {
            return (new ArrayList<LoginEntity>());
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.auth.login.LoginDAO#findUserNearPswdExp(int)
     */
    @Override
    public List<LoginEntity> findUserNearPswdExp(int daysToExpiration) {
        log.debug("findUserNearPswdExp: findUserNearPswdExp called.");
        log.debug("days to password Expiration=" + daysToExpiration);

        Date expDate = new java.sql.Date(System.currentTimeMillis());
        Date endDate = new java.sql.Date(expDate.getTime());

        if (daysToExpiration != 0) {

            Calendar c = Calendar.getInstance();
            c.setTime(expDate);
            c.add(Calendar.DAY_OF_YEAR, (daysToExpiration));

            expDate.setTime(c.getTimeInMillis());

            c.add(Calendar.DAY_OF_YEAR, 1);
            endDate.setTime(c.getTimeInMillis());

            log.debug("dates between : " + expDate.toString() + " "
                    + endDate.toString());

        }

        String sql = new String(
                " from org.openiam.idm.srvc.auth.domain.LoginEntity l where "
                        + " l.pwdExp BETWEEN :startDate and :endDate");

        Session session = getSession();
        Query qry = session.createQuery(sql);
        qry.setDate("startDate", expDate);
        qry.setDate("endDate", endDate);

        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results == null) {
            return (new ArrayList<LoginEntity>());
        }
        return results;

    }
    @Override
    public List<LoginEntity> findUserPswdExpYesterday() {
        log.debug("findUserPswdExpToday: findUserNearPswdExp called.");

        java.sql.Date expDate = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date endDate = new java.sql.Date(expDate.getTime());

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        c.setTime(expDate);
        expDate.setTime(c.getTimeInMillis());

        c.add(Calendar.DAY_OF_YEAR, 1);
        endDate.setTime(c.getTimeInMillis());

        log.debug("dates between : " + expDate.toString() + " "
                + endDate.toString());

        String sql = new String(
                " from org.openiam.idm.srvc.auth.domain.LoginEntity l where "
                        + " l.pwdExp BETWEEN :startDate and :endDate");

        Session session = getSession();
        Query qry = session.createQuery(sql);
        qry.setDate("startDate", expDate);
        qry.setDate("endDate", endDate);

        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results == null) {
            return (new ArrayList<LoginEntity>());
        }
        return results;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.login.LoginDAO#bulkResetPasswordChangeCount()
     */
    @Override
    public int bulkResetPasswordChangeCount() {
        log.debug("bulkResetPasswordChangeCount operation in LoginDAO called.");
        Session session = getSession();

        String loginQry = " UPDATE org.openiam.idm.srvc.auth.domain.LoginEntity l  "
                + " SET l.passwordChangeCount = 0 ";

        Query qry = session.createQuery(loginQry);
        return qry.executeUpdate();

    }

    @Override
    public void save(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        super.save(entity);
    }

    @Override
    public LoginEntity add(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        return super.add(entity);
    }

    @Override
    public void update(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        super.update(entity);
    }

    @Override
    public LoginEntity merge(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        return super.merge(entity);
    }

    @Override
    public void attachDirty(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        super.attachDirty(entity);
    }

    @Override
    public void attachClean(LoginEntity entity) {
        if (entity != null) {
            entity.setLowerCaseLogin(entity.getLogin());
        }
        super.attachClean(entity);
    }

    @Override
    public void save(Collection<LoginEntity> entities) {
        if (entities != null) {
            for (final LoginEntity entity : entities) {
                if (entity != null) {
                    entity.setLowerCaseLogin(entity.getLogin());
                }
            }
        }
        super.save(entities);
    }

    @Override
    public List<LoginEntity> findByUserIds(List<String> userIds, String managedSysId){
        return getCriteria().add(Restrictions.in("userId", userIds)).add(Restrictions.eq("managedSysId", managedSysId))
                            .list();
    }
}
