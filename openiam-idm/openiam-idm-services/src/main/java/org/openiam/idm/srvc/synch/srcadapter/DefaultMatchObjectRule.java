package org.openiam.idm.srvc.synch.srcadapter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.recon.dto.MatchConfig;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.provision.type.Attribute;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("defaultMatchRule")
public class DefaultMatchObjectRule implements MatchObjectRule {
    private static final Log log = LogFactory.getLog(DefaultMatchObjectRule.class);

    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    @Qualifier("userManager")
    private UserDataService userManager;

    @Autowired
    private GroupDataService groupManager;

    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    private RoleDozerConverter roleDozerConverter;

    @Autowired
    private OrganizationService orgManager;

    @Autowired
    private RoleDataService roleManager;

    @Autowired
    private IdentityService identityService;

    private String matchAttrName = null;
    private String matchAttrValue = null;

    public User lookup(MatchConfig matchConfig, Map<String, Attribute> rowAttr) throws IllegalArgumentException {
        final UserSearchBean searchBean = new UserSearchBean();
        //UserSearch search = new UserSearch();
        //Map<String, UserAttribute> atMap = user.getUserAttributes();
        matchAttrName = matchConfig.getMatchFieldName();
        if (StringUtils.isBlank(matchAttrName)) {
            throw new IllegalArgumentException("matchAttrName can not be blank");
        }

        matchAttrValue = (StringUtils.isNotBlank(matchConfig.getCustomMatchAttr())) ? rowAttr.get(matchConfig.getCustomMatchAttr()).getValue() : rowAttr.get(matchAttrName).getValue();

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrValue can not be blank");
        }

        if (matchAttrName.equalsIgnoreCase("USERID")) {
            searchBean.setUserId(matchAttrValue);
            //search.setUserId(matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("PRINCIPAL")) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
            searchBean.setPrincipal(lsb);
            //search.setPrincipal(matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("MANAGED_SYS_PRINCIPAL")) {
            LoginSearchBean lsb = new LoginSearchBean();
            lsb.setLoginMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            lsb.setManagedSysId(matchConfig.getManagedSysId());
            searchBean.setPrincipal(lsb);
            //search.setPrincipal(matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("EMAIL")) {
            searchBean.setEmailAddressMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            //search.setEmailAddress(matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("EMPLOYEE_ID")) {
            searchBean.setEmployeeIdMatchToken(new SearchParam(matchAttrValue, MatchType.EXACT));
            //search.setEmployeeId(matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("ATTRIBUTE")) {
            System.out.println("- cofiguring search by attribute..");
            System.out.println("- match attr=.." + matchConfig.getMatchSrcFieldName());

            // get the attribute value from the data_set
            System.out.println("- src field value=.." + matchAttrValue);
            matchAttrName = matchConfig.getMatchSrcFieldName();

            searchBean.addAttribute(matchAttrName, matchAttrValue);

        }

        List<User> userList = null;
        try {
            userList = userManager.findBeansDto(searchBean, 0, Integer.MAX_VALUE);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }

        if (userList != null && !userList.isEmpty()) {
            System.out.println("User matched with existing user...");
            return userManager.getUserDto(userList.get(0).getId(), null, true);
        }
        return null;
    }

    @Override
    public Group lookupGroup(MatchConfig matchConfig, Map<String, Attribute> rowAttr) {
        GroupSearchBean searchBean = null;
        matchAttrName = matchConfig.getMatchFieldName();
        matchAttrValue = (StringUtils.isNotBlank(matchConfig.getCustomMatchAttr())) ? rowAttr.get(matchConfig.getCustomMatchAttr()).getValue() : null;

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrName and matchAttrValue can not be blank");
        }

        if (matchAttrName.equalsIgnoreCase("NAME")) {
            searchBean = new GroupSearchBean();
            searchBean.setName(matchAttrValue);
        } else if (matchAttrName.equalsIgnoreCase("ATTRIBUTE")) {
            System.out.println("- cofiguring search by attribute..");
            System.out.println("- match attr=.." + matchConfig.getMatchSrcFieldName());

            // get the attribute value from the data_set
            System.out.println("- src field value=.." + matchAttrValue);
            matchAttrName = matchConfig.getMatchSrcFieldName();

            searchBean = new GroupSearchBean();
            searchBean.addAttribute(matchAttrName, matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("IDENTITY")) {
            IdentitySearchBean identitySearchBean = new IdentitySearchBean();
            identitySearchBean.setType(IdentityTypeEnum.GROUP);
            identitySearchBean.setManagedSysId(matchConfig.getManagedSysId());
            identitySearchBean.setIdentity(matchAttrValue);
            List<IdentityDto> dtos = identityService.findByExample(identitySearchBean, null, 0, Integer.MAX_VALUE);
            if (dtos != null && !dtos.isEmpty()) {
                searchBean = new GroupSearchBean();
                for (IdentityDto dto : dtos) {
                    searchBean.addKey(dto.getReferredObjectId());
                }
            }
        }
        if (searchBean != null) {
            List<GroupEntity> groupEntities = groupManager.findBeans(searchBean, null, 0, Integer.MAX_VALUE);
            if (groupEntities != null && !groupEntities.isEmpty()) {
                System.out.println("Group matched with existing group...");
                return groupDozerConverter.convertToDTO(groupEntities.get(0), true);
            }
        }
        return null;
    }

    @Override
    public Role lookupRole(MatchConfig matchConfig, Map<String, Attribute> rowAttr) {
        final RoleSearchBean searchBean = new RoleSearchBean();
        matchAttrName = matchConfig.getMatchFieldName();
        matchAttrValue = (StringUtils.isNotBlank(matchConfig.getCustomMatchAttr())) ? rowAttr.get(matchConfig.getCustomMatchAttr()).getValue() : null;

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrName and matchAttrValue can not be blank");
        }

        if (matchAttrName.equalsIgnoreCase("NAME")) {
            searchBean.setName(matchAttrValue);
        } else if (matchAttrName.equalsIgnoreCase("ATTRIBUTE")) {
            System.out.println("- cofiguring search by attribute..");
            System.out.println("- match attr=.." + matchConfig.getMatchSrcFieldName());

            // get the attribute value from the data_set
            System.out.println("- src field value=.." + matchAttrValue);
            matchAttrName = matchConfig.getMatchSrcFieldName();

            searchBean.addAttribute(matchAttrName, matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("IDENTITY")) {
            IdentitySearchBean identitySearchBean = new IdentitySearchBean();
            identitySearchBean.setType(IdentityTypeEnum.ROLE);
            identitySearchBean.setManagedSysId(matchConfig.getManagedSysId());
            identitySearchBean.setIdentity(matchAttrValue);
            List<IdentityDto> dtos = identityService.findByExample(identitySearchBean, null, 0, Integer.MAX_VALUE);
            if (dtos != null && !dtos.isEmpty()) {
                for (IdentityDto dto : dtos) {
                    searchBean.addKey(dto.getReferredObjectId());
                }
            }
        }

        List<RoleEntity> roleEntities = roleManager.findBeans(searchBean, null, 0, Integer.MAX_VALUE);
        if (roleEntities != null && !roleEntities.isEmpty()) {
            System.out.println("Role matched with existing role...");
            return roleDozerConverter.convertToDTO(roleEntities.get(0), true);
        }
        return null;
    }

    @Override
    public Organization lookupOrganization(MatchConfig matchConfig, Map<String, Attribute> rowAttr) {
        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        matchAttrName = matchConfig.getMatchFieldName();
        matchAttrValue = (StringUtils.isNotBlank(matchConfig.getCustomMatchAttr())) ? rowAttr.get(matchConfig.getCustomMatchAttr()).getValue() : null;

        if (StringUtils.isBlank(matchAttrName) || StringUtils.isBlank(matchAttrValue)) {
            throw new IllegalArgumentException("matchAttrName and matchAttrValue can not be blank");
        }

        if (matchAttrName.equalsIgnoreCase("NAME")) {
            searchBean.setName(matchAttrValue);
        } else if (matchAttrName.equalsIgnoreCase("ATTRIBUTE")) {
            System.out.println("- cofiguring search by attribute..");
            System.out.println("- match attr=.." + matchConfig.getMatchSrcFieldName());

            // get the attribute value from the data_set
            System.out.println("- src field value=.." + matchAttrValue);
            matchAttrName = matchConfig.getMatchSrcFieldName();

            searchBean.addAttribute(matchAttrName, matchAttrValue);

        } else if (matchAttrName.equalsIgnoreCase("IDENTITY")) {
            IdentitySearchBean identitySearchBean = new IdentitySearchBean();
            identitySearchBean.setType(IdentityTypeEnum.ORG);
            identitySearchBean.setManagedSysId(matchConfig.getManagedSysId());
            identitySearchBean.setIdentity(matchAttrValue);
            List<IdentityDto> dtos = identityService.findByExample(identitySearchBean, null, 0, Integer.MAX_VALUE);
            if (dtos != null && !dtos.isEmpty()) {
                for (IdentityDto dto : dtos) {
                    searchBean.addKey(dto.getReferredObjectId());
                }
            }
        }

        List<Organization> orgEntities = orgManager.findBeansDto(searchBean, null, 0, Integer.MAX_VALUE, null);
        if (orgEntities != null && !orgEntities.isEmpty()) {
            System.out.println("Organization matched with existing role...");
            return orgEntities.get(0);
        }
        return null;
    }

    public String getMatchAttrName() {
        return matchAttrName;
    }

    public void setMatchAttrName(String matchAttrName) {
        this.matchAttrName = matchAttrName;
    }

    public String getMatchAttrValue() {
        return matchAttrValue;
    }

    public void setMatchAttrValue(String matchAttrValue) {
        this.matchAttrValue = matchAttrValue;
    }

    @Override
    public String toString() {
        return "DefaultMatchObjectRule{" +
                ", matchAttrName='" + matchAttrName + '\'' +
                ", matchAttrValue='" + matchAttrValue + '\'' +
                '}';
    }
}
