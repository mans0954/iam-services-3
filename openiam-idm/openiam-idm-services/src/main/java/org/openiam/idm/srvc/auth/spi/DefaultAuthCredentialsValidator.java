package org.openiam.idm.srvc.auth.spi;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.service.AuthCredentialsValidator;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;


@Component("defaultAuthCredentialsValidator")
public class DefaultAuthCredentialsValidator implements AuthCredentialsValidator {

    @Autowired
    protected PasswordService passwordManager;

    private static final Log log = LogFactory.getLog(DefaultAuthCredentialsValidator.class);

    public void execute(UserEntity user, Login lg, int operation, Map<String, Object> bindingMap) throws AuthenticationException {

        Date curDate = new Date(System.currentTimeMillis());

        if (UserStatusEnum.PENDING_START_DATE.equals(user.getStatus())) {
            if (!pendingInitialStartDateCheck(user, curDate)) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
        }
        if (!UserStatusEnum.ACTIVE.equals(user.getStatus())
                && !UserStatusEnum.PENDING_INITIAL_LOGIN.equals(user.getStatus())) {
            // invalid status
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_USER_STATUS);
        }

        // check the secondary status
        log.debug("-Secondary status=" + user.getSecondaryStatus());
        if (user.getSecondaryStatus() != null) {
            if (UserStatusEnum.LOCKED.equals(user.getSecondaryStatus())
                    || UserStatusEnum.LOCKED_ADMIN.equals(user.getSecondaryStatus())) {
                log.debug("User is locked. throw exception.");
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_LOGIN_LOCKED);
            }
            if (UserStatusEnum.DISABLED.equals(user.getSecondaryStatus())) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_LOGIN_DISABLED);
            }
        }

        // checking if User Password is valid
        // validate the password expiration rules
        log.debug("Validating the state of the password - expired or not");
        int pswdResult = passwordExpired(lg, curDate);
        if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
            throw new AuthenticationException(AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
        }

        // check password policy if it is necessary to change it after reset
        if (lg.getResetPassword() > 0) {
            Policy passwordPolicy = passwordManager.getPasswordPolicy(lg.getLogin(), lg.getManagedSysId());
            String chngPwdAttr = getPolicyAttribute(passwordPolicy.getPolicyAttributes(),"CHNG_PSWD_ON_RESET");
            if (StringUtils.isNotBlank(chngPwdAttr) && StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), chngPwdAttr)) {
                throw new AuthenticationException(AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET);
            }
        }
    }

    private boolean pendingInitialStartDateCheck(UserEntity user, Date curDate) {
        if (UserStatusEnum.PENDING_START_DATE.equals(user.getStatus())) {
            if (user.getStartDate() != null
                    && curDate.before(user.getStartDate())) {
                log.debug("UserStatus= PENDING_START_DATE and user start date="
                        + user.getStartDate());
                return false;
            } else {
                log.debug("UserStatus= PENDING_START_DATE and user start date=null");
                return false;
            }
        }
        return true;
    }

    /**
     * If the password has expired, but its before the grace period then its a good login
     * If the password has expired and after the grace period, then its an exception.
     * You should also set the days to expiration
     *
     * @param lg
     * @return
     */
    private int passwordExpired(Login lg, Date curDate) {
        log.debug("passwordExpired Called.");
        log.debug("- Password Exp =" + lg.getPwdExp());
        log.debug("- Password Grace Period =" + lg.getGracePeriod());

        if (lg.getGracePeriod() == null) {
            // set an early date
            Date gracePeriodDate = getGracePeriodDate(lg, curDate);
            log.debug("Calculated the gracePeriod Date to be: "
                    + gracePeriodDate);

            if (gracePeriodDate == null) {
                lg.setGracePeriod(new Date(0));
            } else {
                lg.setGracePeriod(gracePeriodDate);
            }
        }
        if (lg.getPwdExp() != null) {
            if (curDate.after(lg.getPwdExp())
                    && curDate.after(lg.getGracePeriod())) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_PASSWORD_EXPIRED;
            }
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg
                    .getGracePeriod()))) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
            }
        }
        return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
    }

    private Date getGracePeriodDate(Login lg, Date curDate) {

        Date pwdExpDate = lg.getPwdExp();

        if (pwdExpDate == null) {
            return null;
        }

        Policy plcy = passwordManager.getPasswordPolicy(lg.getLogin(), lg.getManagedSysId());
        if (plcy == null) {
            return null;
        }

        /*
        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION");
        String changePswdOnReset = getPolicyAttribute(
                plcy.getPolicyAttributes(), "CHNG_PSWD_ON_RESET");
		*/
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        log.debug("Grace period policy value= " + gracePeriod);

        Calendar cal = Calendar.getInstance();
        cal.setTime(pwdExpDate);

        log.debug("Password Expiration date =" + pwdExpDate);

        if (gracePeriod != null && !gracePeriod.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
            log.debug("Calculated grace period date=" + cal.getTime());
            return cal.getTime();
        }
        return null;

    }

    private String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";
        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;
    }

}
