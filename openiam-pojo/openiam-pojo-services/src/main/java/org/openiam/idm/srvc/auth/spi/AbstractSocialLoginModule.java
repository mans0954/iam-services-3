package org.openiam.idm.srvc.auth.spi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.spi.social.AbstractSocialProfile;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by alexander on 01.04.15.
 */
public class AbstractSocialLoginModule<Profile extends AbstractSocialProfile> extends AbstractLoginModule {
    protected  static final String DEFAULT_TOKEN_LIFE = "30";
    private final Log log = LogFactory.getLog(this.getClass());

    protected Class<Profile> loginModuleClass;

    @Autowired
    @Qualifier("userManager")
    protected UserDataService userService;

    @Autowired
    protected  RoleDAO roleDao;

    @Autowired
    protected  RoleDataService roleDataService;

    @Autowired
    @Qualifier("metadataService")
    protected  MetadataService metadataService;
    @Autowired
    private MetadataTypeDAO metadataTypeDao;
    @Autowired
    @Qualifier("customJacksonMapper")
    protected  ObjectMapper jsonMapper;


    public AbstractSocialLoginModule() {
        Type t = getClass().getGenericSuperclass();
        Type arg;
        if (t instanceof ParameterizedType) {
            arg = ((ParameterizedType) t).getActualTypeArguments()[0];
        } else if (t instanceof Class) {
            arg = ((ParameterizedType) ((Class) t).getGenericSuperclass())
                    .getActualTypeArguments()[0];

        } else {
            throw new RuntimeException("Can not handle type construction for '"
                    + getClass() + "'!");
        }

        if (arg instanceof Class) {
            this.loginModuleClass = (Class<Profile>) arg;
        } else if (arg instanceof ParameterizedType) {
            this.loginModuleClass = (Class<Profile>) ((ParameterizedType) arg)
                    .getRawType();
        } else {
            throw new RuntimeException(
                    "Problem determining generic class for '" + getClass()
                            + "'! ");
        }
    }

    @Override
    protected void validate(AuthenticationContext context) throws Exception {
        final String profileInfo = context.getSocialUserProfile();
        final IdmAuditLogEntity newLoginEvent = context.getEvent();

        if (StringUtils.isBlank(profileInfo)) {
            newLoginEvent.setFailureReason("Invalid profile info");
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }

        Profile profile = jsonMapper.readValue(profileInfo, loginModuleClass);
        if(profile==null){
            newLoginEvent.setFailureReason("Invalid profile info");
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }
    }

    @Override
    protected LoginEntity getLogin(AuthenticationContext context) throws Exception {
        final IdmAuditLogEntity newLoginEvent = context.getEvent();
        final String profileInfo = context.getSocialUserProfile();
        Profile profile = jsonMapper.readValue(profileInfo, loginModuleClass);
        String principal = profile.getEmail();

        final ManagedSysEntity managedSystem = getManagedSystem(context);
        LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());
        if (lg == null) {
            // register new user
            userRegisteration(context, profile, principal);

            lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());
            if (lg == null) {
                newLoginEvent.setFailureReason(String.format("Cannot find login for principal '%s' and managedSystem '%s'", principal, managedSystem.getId()));
                throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN);
            }
        }
        return lg;
    }

    @Override
    protected UserEntity getUser(AuthenticationContext context, LoginEntity login) throws Exception {
        final IdmAuditLogEntity newLoginEvent = context.getEvent();
        final String userId = login.getUserId();
        newLoginEvent.setRequestorUserId(userId);
        newLoginEvent.setTargetUser(userId, login.getLogin());
        final UserEntity user = userDAO.findById(userId);
        return user;
    }

    @Override
    protected Subject doLogin(AuthenticationContext context, UserEntity user, LoginEntity login) throws Exception {
        final String profileInfo = context.getSocialUserProfile();
        Profile profile = jsonMapper.readValue(profileInfo, loginModuleClass);
        String principal = profile.getEmail();

        final IdmAuditLogEntity newLoginEvent = context.getEvent();

        final Subject sub = new Subject();

        final String clientIP = context.getClientIP();
        final String nodeIP = context.getNodeIP();

        // current date
        final Date curDate = new Date();

        if (user != null && user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
                    throw new BasicDataServiceException(ResponseCode.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                throw new BasicDataServiceException(
                        ResponseCode.RESULT_INVALID_USER_STATUS);
            }
            // check the secondary status
            checkSecondaryStatus(user);

        }

        final PolicyEntity policy = getAuthPolicy(context);
        final String tokenType = getPolicyAttribute(policy, "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(policy, "TOKEN_LIFE");
        final String tokenIssuer = getPolicyAttribute(policy, "TOKEN_ISSUER");

        if(StringUtils.isBlank(tokenType)) {
            final String warning = String.format("Property %s not valid for policy key %s for policy %s", tokenType, "TOKEN_TYPE", policy);
            newLoginEvent.addWarning(warning);
            log.warn(warning);
        }

        if(StringUtils.isBlank(tokenLife) || !NumberUtils.isNumber(tokenLife)) {
            final String warning = String.format("Property %s not valid for policy key %s for policy %s.  Defaulting to %s", tokenLife, "TOKEN_LIFE", policy, DEFAULT_TOKEN_LIFE);
            newLoginEvent.addWarning(warning);
            log.error(warning);
            tokenLife = DEFAULT_TOKEN_LIFE;
        }

        final Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);


        // Successful login
        log.debug("-Populating subject after authentication");

        sub.setUserId(login.getUserId());
        sub.setPrincipal(principal);
        sub.setSsoToken(token(login.getUserId(), tokenType, tokenLife, tokenParam));
        setResultCode(login, sub, curDate, policy);

        newLoginEvent.setSuccessReason("Successful authentication into Default Login Module");
        return sub;
    }



    protected void userRegisteration(AuthenticationContext context, Profile profile, String principal) throws Exception {
        AuthProviderEntity authProvider = getAuthProvider(context);
        final ManagedSysEntity managedSystem = getManagedSystem(context, authProvider);
        RoleEntity role = null;

        Map<String, AuthResourceAttributeMapEntity> attributeMapMap = authProvider.getResourceAttributeMap();

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(profile.getFirstName());
        userEntity.setLastName(profile.getLastName());
        userEntity.setSex( ("male".equals(profile.getGender())) ? "M" : "F" );


        Set<EmailAddressEntity> emailAddressList = new HashSet<>();

        final MetadataTypeSearchBean searchBean = new MetadataTypeSearchBean();
        searchBean.setGrouping(MetadataTypeGrouping.EMAIL);
        searchBean.setActive(true);
        final List<MetadataType> types = metadataService.findBeans(searchBean, 0, Integer.MAX_VALUE, null);

        EmailAddressEntity email = new EmailAddressEntity();
        email.setEmailAddress(profile.getEmail());
        email.setIsActive(true);
        email.setIsDefault(true);
        email.setType(metadataTypeDao.findById(types.get(0).getId()));

        emailAddressList.add(email);
        userEntity.setEmailAddresses(emailAddressList);


        List<LoginEntity> principalList =  new LinkedList<>();
        LoginEntity lg = new LoginEntity();
        lg.setLogin(principal);
        lg.setManagedSysId(managedSystem.getId());
        principalList.add(lg);

        userEntity.setPrincipalList(principalList);

        userService.saveUserInfo(userEntity, null);

        if(attributeMapMap!=null && !attributeMapMap.isEmpty()){
            String roleName = attributeMapMap.get("DEFAULT_ROLE").getAttributeValue();
            RoleSearchBean roleSearchBean = new RoleSearchBean();
            searchBean.setName(roleName);

            List<RoleEntity> roleList = roleDao.getByExample(roleSearchBean);
            if(CollectionUtils.isNotEmpty(roleList)){
            	userEntity.addRole(roleList.get(0), null, null, null);
            }
        }
    }

    protected SSOToken token(final String userId, final String tokenType, final String tokenLife, final Map tokenParam) throws Exception {

        log.debug("Generating Security Token");

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        return tkModule.createToken(tokenParam);
    }
}
