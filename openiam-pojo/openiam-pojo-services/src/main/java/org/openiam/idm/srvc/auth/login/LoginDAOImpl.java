package org.openiam.idm.srvc.auth.login;
// Generated Feb 18, 2008 3:56:08 PM by Hibernate Tools 3.2.0.b11


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDao;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository("loginDAO")
public class LoginDAOImpl extends BaseDaoImpl<LoginEntity, String> implements LoginDAO {

    private static final Log log = LogFactory.getLog(LoginDAOImpl.class);

    @Value("${openiam.dbType}")
    private String dbType;

    public int changeIdentity(String principal, String pswd, String userId, String managedSysId) {
        Session session = sessionFactory.getCurrentSession();
        String hq = " UPDATE LoginEntity l " +
                " set l.login = :principal,  " +
                "     l.password = :pswd," +
                "	   l.passwordChangeCount = 0," +
                " 	   l.isLocked = 0, " +
                "	   l.authFailCount = 0	 " +
                " where l.managedSysId = :managedSysId and " +
                "		 l.userId = :userId";
        Query qry = session.createQuery(hq);
        qry.setString("userId", userId);
        qry.setString("principal", principal);
        qry.setString("pswd", pswd);
        qry.setString("managedSysId", managedSysId);
        return qry.executeUpdate();
    }

    public LoginEntity getRecord(final String login, final String managedSysId, final String domainId) {
    	if (dbType != null && dbType.equalsIgnoreCase("ORACLE_INSENSITIVE")) {
    		return findByIdOracleInsensitive(login, managedSysId, domainId);
    	}
            
    	return (LoginEntity)getCriteria().add(Restrictions.eq("login", login))
    								     .add(Restrictions.eq("managedSysId", managedSysId))
    								     .add(Restrictions.eq("domainId", domainId)).uniqueResult();
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    /*
      Gets the LoginID ignoring case.
     */
    private LoginEntity findByIdOracleInsensitive(final String login, final String managedSysId, final String domainid) {


        String select = " select /*+ INDEX(IDX_LOGIN_UPPER)  */ " +
                " SERVICE_ID, LOGIN, MANAGED_SYS_ID, IDENTITY_TYPE, CANONICAL_NAME, USER_ID, PASSWORD, " +
                " PWD_EQUIVALENT_TOKEN, PWD_CHANGED, PWD_EXP, RESET_PWD, FIRST_TIME_LOGIN, IS_LOCKED, STATUS, " +
                " GRACE_PERIOD, CREATE_DATE, CREATED_BY, CURRENT_LOGIN_HOST, AUTH_FAIL_COUNT, LAST_AUTH_ATTEMPT, " +
                " LAST_LOGIN, IS_DEFAULT, PWD_CHANGE_COUNT, LAST_LOGIN_IP, PREV_LOGIN, PREV_LOGIN_IP " +
                " FROM 	LOGIN  " +
                " WHERE SERVICE_ID = :serviceId AND UPPER(LOGIN) = :login AND MANAGED_SYS_ID = :managedSysId  ";


        Session session = sessionFactory.getCurrentSession();

        SQLQuery qry = session.createSQLQuery(select);
        qry.addEntity(LoginEntity.class);

        qry.setString("serviceId", domainid);
        qry.setString("login", login);
        qry.setString("managedSysId", managedSysId);


        try {
            return (LoginEntity) qry.uniqueResult();

        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;

    }

    public List<LoginEntity> findAllLoginByManagedSys(String managedSysId) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where l.managedSysId = :managedSysId order by l.login asc ");
        qry.setString("managedSysId", managedSysId);
        return (List<LoginEntity>) qry.list();

    }

    public List<LoginEntity> getLoginSublist(int startPos, int size){
        StringBuilder sql = new StringBuilder();
        sql.append("from ").append(LoginEntity.class.getName()).append(" l");
        return (List<LoginEntity>)sessionFactory.getCurrentSession().createQuery(sql.toString()).setFirstResult(startPos).setMaxResults(size).list();
    }

    public Long getLoginCount(){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(l.password) from ").append(LoginEntity.class.getName()).append(" l");
        return (Long)sessionFactory.getCurrentSession().createQuery(sql.toString()).uniqueResult();
    }

    public List<LoginEntity> findUser(String userId) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where l.userId = :userId order by l.status desc, l.managedSysId asc ");
        qry.setString("userId", userId);
        return (List<LoginEntity>) qry.list();


    }

    public List<LoginEntity> findLoginByDomain(String domain) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where l.domainId = :domain ");
        qry.setString("domain", domain);
        return (List<LoginEntity>) qry.list();

    }

    public LoginEntity findLoginByManagedSys(String domain, String managedSys, String userId) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where l.domainId = :domain and " +
                "  l.managedSysId = :managedSys and " +
                "  l.userId = :userId ");
        log.debug("domain=" + domain + " managedSys=" + managedSys + " userId=" + userId);
        qry.setString("domain", domain);
        qry.setString("managedSys", managedSys);
        qry.setString("userId", userId);
        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    public List<LoginEntity> findLoginByManagedSys(String principalName, String managedSysId) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where  " +
                "  l.managedSysId = :managedSys and " +
                "  l.login = :login ");

        qry.setString("managedSys", managedSysId);
        qry.setString("login", principalName);
        return (List<LoginEntity>) qry.list();

    }

    public LoginEntity findByPasswordResetToken(String token) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where  l.pswdResetToken = :token  ");

        qry.setString("token", token);
        return (LoginEntity) qry.uniqueResult();
    }

    public List<LoginEntity> findLockedUsers(Date startTime) {
        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery("from org.openiam.idm.srvc.auth.domain.LoginEntity l " +
                " where l.isLocked = 1 and  " +
                "  l.lastAuthAttempt >= :startTime ");
        qry.setTimestamp("startTime", startTime);
        return (List<LoginEntity>) qry.list();

    }

    String loginQry = " UPDATE org.openiam.idm.srvc.auth.domain.LoginEntity l  " +
            " SET l.isLocked = 0 " +
            "       where l.domainId = :domain and  " +
            "             l.isLocked = :status and " +
            "             l.lastAuthAttempt <= :policyTime";


    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.auth.login.LoginDAO#bulkUnlock(org.openiam.idm.srvc.user.dto.UserStatusEnum)
      */
    public int bulkUnlock(String domainId, UserStatusEnum status, int autoUnlockTime) {

        log.debug("bulkUnlock operation in LoginDAO called.");
        Session session = sessionFactory.getCurrentSession();


        String userQry = " UPDATE org.openiam.idm.srvc.user.domain.UserEntity u  " +
                " SET u.secondaryStatus = null " +
                " where u.secondaryStatus = 'LOCKED' and " +
                "       u.userId in (" +
                " 	select l.userId from org.openiam.idm.srvc.auth.domain.LoginEntity as l  " +
                "       where l.domainId = :domain and  " +
                "             l.isLocked = :status and " +
                "             l.lastAuthAttempt <= :policyTime" +
                "   )";

        String loginQry = " UPDATE org.openiam.idm.srvc.auth.domain.LoginEntity l  " +
                " SET l.isLocked = 0, " +
                "     l.authFailCount = 0 " +
                "       where l.domainId = :domain and  " +
                "             l.isLocked = :status and " +
                "             l.lastAuthAttempt <= :policyTime";


        Query qry = session.createQuery(userQry);


        Date policyTime = new Date(System.currentTimeMillis());

        log.debug("Auto unlock time:" + autoUnlockTime);

        Calendar c = Calendar.getInstance();
        c.setTime(policyTime);
        c.add(Calendar.MINUTE, (-1 * autoUnlockTime));
        policyTime.setTime(c.getTimeInMillis());

        log.debug("Policy time=" + policyTime.toString());

        qry.setString("domain", domainId);

        log.debug("DomainId=" + domainId);

        int statusParam = 0;
        if (status.equals(UserStatusEnum.LOCKED)) {
            statusParam = 1;
            //qry.setInteger("status", 1);
            log.debug("status=1");
        } else {
            statusParam = 2;
            //qry.setInteger("status", 2);
            log.debug("status=2");
        }

        qry.setInteger("status", statusParam);
        qry.setTimestamp("policyTime", policyTime);
        int rowCount = qry.executeUpdate();

        log.debug("Bulk unlock updated:" + rowCount);

        if (rowCount > 0) {

            Query lQry = session.createQuery(loginQry);
            lQry.setString("domain", domainId);
            lQry.setInteger("status", statusParam);
            lQry.setTimestamp("policyTime", policyTime);
            lQry.executeUpdate();

        }

        return rowCount;


    }


    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.auth.login.LoginDAO#findInactiveUsers(int, int)
      */
    public List<LoginEntity> findInactiveUsers(int startDays, int endDays, String managedSysId) {
        log.debug("findInactiveUsers called.");
        log.debug("Start days=" + startDays);
        log.debug("End days=" + endDays);

        boolean start = false;
        long curTimeMillis = System.currentTimeMillis();
        Date startDate = new Date(curTimeMillis);
        Date endDate = new Date(curTimeMillis);

        StringBuilder sql = new StringBuilder(" from org.openiam.idm.srvc.auth.domain.LoginEntity l where " +
                " l.managedSysId = :managedSys and ");


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


        Session session = sessionFactory.getCurrentSession();
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


    /* (non-Javadoc)
    * @see org.openiam.idm.srvc.auth.login.LoginDAO#findUserNearPswdExp(int)
    */
    public List<LoginEntity> findUserNearPswdExp(int daysToExpiration) {
        log.debug("findUserNearPswdExp: findUserNearPswdExp called.");
        log.debug("days to password Expiration=" + daysToExpiration);

        java.sql.Date expDate = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date endDate = new java.sql.Date(expDate.getTime());

        if (daysToExpiration != 0) {

            Calendar c = Calendar.getInstance();
            c.setTime(expDate);
            c.add(Calendar.DAY_OF_YEAR, (daysToExpiration));

            expDate.setTime(c.getTimeInMillis());

            c.add(Calendar.DAY_OF_YEAR, 1);
            endDate.setTime(c.getTimeInMillis());


            log.debug("dates between : " + expDate.toString() + " " + endDate.toString());

        }

        String sql = new String(" from org.openiam.idm.srvc.auth.dto.Login l where " +
                " l.pwdExp BETWEEN :startDate and :endDate");


        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery(sql);
        qry.setDate("startDate", expDate);
        qry.setDate("endDate", endDate);

        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results == null) {
            return (new ArrayList<LoginEntity>());
        }
        return results;

    }

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


        log.debug("dates between : " + expDate.toString() + " " + endDate.toString());


        String sql = new String(" from org.openiam.idm.srvc.auth.domain.LoginEntity l where " +
                " l.pwdExp BETWEEN :startDate and :endDate");


        Session session = sessionFactory.getCurrentSession();
        Query qry = session.createQuery(sql);
        qry.setDate("startDate", expDate);
        qry.setDate("endDate", endDate);

        List<LoginEntity> results = (List<LoginEntity>) qry.list();
        if (results == null) {
            return (new ArrayList<LoginEntity>());
        }
        return results;

    }


    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.auth.login.LoginDAO#bulkResetPasswordChangeCount()
      */
    public int bulkResetPasswordChangeCount() {
        log.debug("bulkResetPasswordChangeCount operation in LoginDAO called.");
        Session session = sessionFactory.getCurrentSession();


        String loginQry = " UPDATE org.openiam.idm.srvc.auth.domain.LoginEntity l  " +
                " SET l.passwordChangeCount = 0 ";

        Query qry = session.createQuery(loginQry);
        return qry.executeUpdate();

    }
}

