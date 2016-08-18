package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.hibernate.type.IntegerType;
import org.openiam.base.OrderConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.*;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;
import org.openiam.idm.srvc.sysprop.service.SystemPropertyService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.util.StringUtil;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access implementation for domain model class User and UserWS. UserWS is
 * similar to User, however, the interface has been simplified to support usage
 * in a web service.
 *
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
@Repository("userDAO")
public class UserDAOImpl extends BaseDaoImpl<UserEntity, String> implements UserDAO {
    @Value("${openiam.dbType}")
    private String dbType;
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private SystemPropertyService systemPropertyService;

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Value("${org.openiam.organization.type.id}")
    private String organizationTypeId;
    @Value("${org.openiam.department.type.id}")
    private String departmentTypeId;

    /* DO NOT MERGE INTO 4.0!!!! */
    @Override
    public List<String> getUserIds(UserSearchBean searchBean) {
        return getExampleCriteria(searchBean).setProjection(Projections.property("id")).list();
    }

    @Override
    public UserEntity findByIdDelFlt(String userId, DelegationFilterSearchBean delegationFilter) {
        Criteria criteria = getCriteria();

        if (delegationFilter != null) {
            if (CollectionUtils.isNotEmpty(delegationFilter.getOrganizationIdSet())) {
                criteria.createAlias("organizationUser", "aff").add(Restrictions.in("aff.primaryKey.organization.id", delegationFilter.getOrganizationIdSet()));
            }

            if (CollectionUtils.isNotEmpty(delegationFilter.getGroupIdSet())) {
                criteria.createAlias("groups", "g");
                criteria.add(createInClauseForIds("g", "id", "GRP_ID", new ArrayList<>(delegationFilter.getGroupIdSet())));
            }

            if (CollectionUtils.isNotEmpty(delegationFilter.getRoleIdSet())) {
                criteria.createAlias("roles", "r");
                criteria.add(createInClauseForIds("r", "id", "ROLE_ID", new ArrayList<>(delegationFilter.getRoleIdSet())));
            }
        }

        criteria.add(Restrictions.eq(getPKfieldName(), userId));

        return (UserEntity) criteria.uniqueResult();
    }

    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search) {
        final Criteria criteria = getCriteria();

        if (StringUtils.isNotEmpty(search.getRole())) {
            criteria.createAlias("roles", "r");
            criteria.add(Restrictions.eq("r.id", search.getRole()));
        }

        if (search.isDelAdmin()) {
            criteria.add(Restrictions.eq("delAdmin", search.isDelAdmin()));
            if (search.getOrgFilter() != null) {
                criteria.createAlias("userAttributes", "ua");
                criteria.add(Restrictions.eq("ua.name", "DLG_FLT_ORG"));
                criteria.add(Restrictions.ilike("ua.value", search.getOrgFilter()));
            }
        }
        criteria.addOrder(Order.asc("lastName"));
        return (List<UserEntity>) criteria.list();
    }

    @Override
    public List<String> getUserIdList(int startPos, int count) {
        return (List<String>) getCriteria().setProjection(Projections.property(getPKfieldName())).setFirstResult(startPos).setMaxResults(count)
                .list();
    }

    @Override
    public Long getUserCount() {
        return (Long) getCriteria().setProjection(Projections.count(getPKfieldName())).uniqueResult();
    }

    @Override
    public List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size) {
        Criteria criteria = getExampleCriteria(searchBean);
        if (startAt > -1) {
            criteria.setFirstResult(startAt);
        }
        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return (List<UserEntity>) criteria.list();
    }

    @Override
    public Long getUserCount(UserSearchBean searchBean) {
        return ((Number) getExampleCriteria(searchBean).setProjection(rowCount()).uniqueResult()).longValue();
    }

    private Criterion getStringCriterion(String fieldName, String value) {
        return getStringCriterion(fieldName, value, false);
    }

    private Criterion getStringCriterion(String fieldName, String value, boolean caseInsensitive) {
        Criterion criterion = null;
        MatchMode matchMode = null;
        if (StringUtils.indexOf(value, "*") == 0) {
            matchMode = MatchMode.END;
            value = value.substring(1);
        }
        if (StringUtils.isNotEmpty(value) && StringUtils.indexOf(value, "*") == value.length() - 1) {
            value = value.substring(0, value.length() - 1);
            matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
        }

        if (StringUtils.isNotEmpty(value)) {
            if (matchMode != null) {
                criterion = Restrictions.ilike(fieldName, value, matchMode);
            } else {
                criterion = (caseInsensitive) ? Restrictions.eq(fieldName, value).ignoreCase() : Restrictions.eq(fieldName, value);
            }
        }
        return criterion;
    }

    private Criterion getStringCriterionMatchType(String fieldName, String value, MatchType matchType, boolean caseInsensitive) {
        Criterion criterion = null;
        MatchMode matchMode = null;

        switch (matchType) {
            case EXACT:
                criterion = (caseInsensitive) ? Restrictions.eq(fieldName, value).ignoreCase() : Restrictions.eq(fieldName, value);
                break;
            case STARTS_WITH:
                criterion = Restrictions.ilike(fieldName, value, MatchMode.START);
                break;
            case END_WITH:
                criterion = Restrictions.ilike(fieldName, value, MatchMode.END);
                break;
            default:
                criterion = Restrictions.ilike(fieldName, value, MatchMode.ANYWHERE);
                break;
        }

        return criterion;
    }

    private Criteria getExampleCriteria(UserSearchBean searchBean) {
        boolean ORACLE_INSENSITIVE = "ORACLE_INSENSITIVE".equalsIgnoreCase(dbType);

        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            criteria.add(Restrictions.eq(getPKfieldName(), searchBean.getKey()));
        } else {
            Boolean useMatchType = false;

            List<SystemPropertyDto> propList = systemPropertyService.getByType("USER_SEARCH_PROP");
            if (CollectionUtils.isNotEmpty(propList)) {
                for (SystemPropertyDto sysProp : propList) {
                    if ("USE_MATCH_TYPE".equalsIgnoreCase(sysProp.getName())) {
                        try {
                            useMatchType = Boolean.valueOf(sysProp.getValue());
                        } catch (Exception e) {
                            log.error("Cann't parse system property : USE_DEFAULT_MATCH_TYPE = " + sysProp.getValue());
                        }
                        continue;
                    }
                }
            }

            if (searchBean.getShowInSearch() != null) {
                criteria.add(Restrictions.eq("showInSearch", searchBean.getShowInSearch()));
            }
            if (searchBean.getFirstNameMatchToken() != null && searchBean.getFirstNameMatchToken().isValid()) {
                if (useMatchType) {
                    criteria.add(getStringCriterionMatchType("firstName", searchBean.getFirstNameMatchToken().getValue(), searchBean.getFirstNameMatchToken().getMatchType(), ORACLE_INSENSITIVE));
                } else {
                    criteria.add(getStringCriterion("firstName", searchBean.getFirstNameMatchToken().getValue(), ORACLE_INSENSITIVE));
                }
            }
            if (searchBean.getLastNameMatchToken() != null && searchBean.getLastNameMatchToken().isValid()) {
                if (useMatchType) {
                    criteria.add(getStringCriterionMatchType("lastName", searchBean.getLastNameMatchToken().getValue(), searchBean.getLastNameMatchToken().getMatchType(), ORACLE_INSENSITIVE));
                } else {
                    criteria.add(getStringCriterion("lastName", searchBean.getLastNameMatchToken().getValue(), ORACLE_INSENSITIVE));
                }
            }
            if (StringUtils.isNotEmpty(searchBean.getNickName())) {
                if (useMatchType) {
                    criteria.add(getStringCriterionMatchType("nickname", searchBean.getNickName(), MatchType.STARTS_WITH, ORACLE_INSENSITIVE));
                } else {
                    criteria.add(getStringCriterion("nickname", searchBean.getNickName()));
                }
            }
            if (searchBean.getNickNameMatchToken() != null && searchBean.getNickNameMatchToken().isValid()) {
                if (useMatchType) {
                    criteria.add(getStringCriterionMatchType("nickname", searchBean.getNickNameMatchToken().getValue(), searchBean.getNickNameMatchToken().getMatchType(), ORACLE_INSENSITIVE));
                } else {
                    criteria.add(getStringCriterion("nickname", searchBean.getNickName()));
                }
            }
            if (StringUtils.isNotEmpty(searchBean.getUserStatus())) {
                criteria.add(Restrictions.eq("status", UserStatusEnum.valueOf(searchBean.getUserStatus())));
            }
            if (StringUtils.isNotEmpty(searchBean.getAccountStatus())) {
                criteria.add(Restrictions.eq("secondaryStatus", UserStatusEnum.valueOf(searchBean.getAccountStatus())));
            }
            if (searchBean.getCreateDate() != null) {
                criteria.add(Restrictions.eq("createDate", searchBean.getCreateDate()));
            }

            if (searchBean.getStartDate() != null) {
                criteria.add(Restrictions.eq("startDate", searchBean.getStartDate()));
            }
            if (searchBean.getLastDate() != null) {
                criteria.add(Restrictions.eq("lastDate", searchBean.getLastDate()));
            }
            if (searchBean.getClaimDate() != null) {
                criteria.add(Restrictions.eq("claimDate", searchBean.getClaimDate()));
            }
            if (searchBean.getDateOfBirth() != null) {
                criteria.add(Restrictions.eq("birthdate", searchBean.getDateOfBirth()));
            }
            if (StringUtils.isNotEmpty(searchBean.getUserTypeInd())) {
                criteria.add(Restrictions.eq("userTypeInd", searchBean.getUserTypeInd()));
            }
            if (StringUtils.isNotEmpty(searchBean.getClassification())) {
                criteria.add(Restrictions.eq("classification", searchBean.getClassification()));
            }
            if (StringUtils.isNotEmpty(searchBean.getLocationCd())) {
                criteria.add(Restrictions.eq("locationCd", searchBean.getLocationCd()));
            }
            if (StringUtils.isNotEmpty(searchBean.getZipCode())) {
                criteria.add(Restrictions.eq("postalCd", searchBean.getZipCode()));
            }
            if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
                criteria.createAlias("organizationUser", "aff").add(Restrictions.in("aff.primaryKey.organization.id", searchBean.getOrganizationIdSet()));
            }
            if (StringUtils.isNotEmpty(searchBean.getPhoneAreaCd()) || StringUtils.isNotEmpty(searchBean.getPhoneNbr())) {
                if (StringUtils.isNotEmpty(searchBean.getPhoneAreaCd())) {
                    criteria.add(Restrictions.eq("p.areaCd", searchBean.getPhoneAreaCd()));
                }
                if (StringUtils.isNotEmpty(searchBean.getPhoneNbr())) {
                    criteria.add(Restrictions.eq("p.phoneNbr", searchBean.getPhoneNbr()));
                }
                criteria.createAlias("phones", "p");
            }
            if (searchBean.getEmailAddressMatchToken() != null) {
                criteria.createAlias("emailAddresses", "em");
                final Disjunction disjunction = Restrictions.disjunction();
                disjunction.add(getStringCriterion("em.emailAddress", searchBean.getEmailAddressMatchToken().getValue(), ORACLE_INSENSITIVE))
                        .add(getStringCriterion("email", searchBean.getEmailAddressMatchToken().getValue(), ORACLE_INSENSITIVE));
                criteria.add(disjunction);
            }
            if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
                criteria.createAlias("groups", "g");
                criteria.add(Restrictions.in("g.id", searchBean.getGroupIdSet()));
            }

            SearchParam searchParam = searchBean.getEmployeeIdMatchToken();
            if (searchParam != null && searchParam.isValid()) {
                switch (searchParam.getMatchType()) {
                    case EXACT:
                        criteria.add(Restrictions.eq("employeeId", searchParam.getValue()));
                        break;
                    case STARTS_WITH:
                        criteria.add(Restrictions.like("employeeId", searchParam.getValue(), MatchMode.START));
                        break;
                    default:
                        break;
                }
            }
            //if (StringUtils.isNotEmpty(searchBean.getEmployeeId())) {
            //    criteria.add(Restrictions.eq("employeeId", searchBean.getEmployeeId()));
            //}
            if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
                criteria.createAlias("roles", "r");
                criteria.add(Restrictions.in("r.id", searchBean.getRoleIdSet()));
            }

            if (StringUtils.isNotEmpty(searchBean.getAttributeElementId())
                    || (searchBean.getAttributeList() != null && !searchBean.getAttributeList().isEmpty())) {
                criteria.createAlias("userAttributes", "ua");
                if (searchBean.getAttributeList() != null && !searchBean.getAttributeList().isEmpty()) {
                    for (SearchAttribute atr : searchBean.getAttributeList()) {
                        if (atr.getAttributeName() == null && atr.getAttributeValue() == null) {
                            continue;
                        } else if (atr.getAttributeName() != null && atr.getAttributeValue() == null) {
                            criteria.add(Restrictions.eq("ua.name", atr.getAttributeName()).ignoreCase());
                        } else if (atr.getAttributeName() == null && atr.getAttributeValue() != null) {
                            criteria.add(Restrictions.ilike("ua.value", atr.getAttributeValue(), this.getMatchMode(atr.getMatchType())));
                        } else if (atr.getAttributeName() != null && atr.getAttributeValue() != null) {
                            criteria.add(Restrictions.and(Restrictions.eq("ua.name", atr.getAttributeName()).ignoreCase(),
                                    Restrictions.ilike("ua.value", atr.getAttributeValue(), this.getMatchMode(atr.getMatchType()))));
                        }
                    }
//                    List<String> nameList = new ArrayList<String>();
//                    List<String> valueList = new ArrayList<String>();
//                    for (SearchAttribute atr : searchBean.getAttributeList()) {
//                        if (atr.getAttributeName() != null) {
//                            nameList.add(atr.getAttributeName());
//                        }
//                        if (atr.getAttributeValue() != null) {
//                            valueList.add(atr.getAttributeValue());
//                        }
//                    }
//
//                    if (nameList.size() > 0) {
//                        criteria.add(Restrictions.in("ua.name", nameList));
//                    }
//                    if (valueList.size() > 0) {
//                        criteria.add(Restrictions.in("ua.value", valueList));
//                    }
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeElementId())) {
                    criteria.add(Restrictions.eq("ua.metadataElementId", searchBean.getAttributeElementId()));
                }
            }
            /* Login */
            if (searchBean.getPrincipal() != null
                    || StringUtils.isNotEmpty(searchBean.getLoggedIn())) {
                criteria.createAlias("principalList", "lg");
                if (searchBean.getPrincipal() != null) {
                    final SearchParam param = searchBean.getPrincipal().getLoginMatchToken();
                    if (param != null && param.isValid()) {
                        MatchMode matchMode = MatchMode.START;
                        if (param.getMatchType() != null) {
                            matchMode = getMatchMode(param.getMatchType());
                        }
                        criteria.add(Restrictions.ilike("lg.login", param.getValue(), matchMode));
                    }
                    if (StringUtils.isNotEmpty(searchBean.getPrincipal().getManagedSysId())) {
                        criteria.add(Restrictions.eq("lg.managedSysId", searchBean.getPrincipal().getManagedSysId()));
                    }
                }
                if (StringUtils.isNotEmpty(searchBean.getLoggedIn())) {
                    if ("YES".equalsIgnoreCase(searchBean.getLoggedIn())) {
                        criteria.add(Restrictions.isNotNull("lg.lastLogin"));
                    } else {
                        criteria.add(Restrictions.isNull("lg.lastLogin"));
                    }
                }
            }
            criteria.addOrder(Order.asc("lastName")).addOrder(Order.asc("firstName"));
        }
        return criteria;
    }


    private MatchMode getMatchMode(MatchType matchType) {
        if (matchType == null) {
            return null;
        }
        MatchMode mode = null;
        switch (matchType) {
            case EXACT:
                mode = MatchMode.EXACT;
                break;
            case END_WITH:
                mode = MatchMode.END;
                break;
            case STARTS_WITH:
                mode = MatchMode.START;
                break;
            default:
                break;
        }

        return mode;
    }
    // private Criteria getUsersForResourceCriteria(final String resourceId) {
    // return getCriteria().createAlias("resourceUsers",
    // "ru").add(Restrictions.eq("ru.resourceId", resourceId));
    // }

    @Override
    public List<UserEntity> getUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter,
                                                List<SortParam> sortParamList, final int from, final int size) {
        final Criteria criteria = getUsersEntitlementCriteria(null, null, resourceId, delegationFilter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        if (CollectionUtils.isNotEmpty(sortParamList)) {
            addSorting(criteria, sortParamList);
        }

        return criteria.list();
    }

    @Override
    public List<UserEntity> getUsersForOrganization(String organizationId, DelegationFilterSearchBean delegationFilter, final int from, final int size) {
        Set<String> orgIds = new HashSet<String>();
        orgIds.add(organizationId);

        delegationFilter.setOrganizationIdSet(orgIds);
        final Criteria criteria = getUsersEntitlementCriteria(null, null, null, delegationFilter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return criteria.list();
    }

    @Override
    public int getNumOfUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter) {
        final Criteria criteria = getUsersEntitlementCriteria(null, null, resourceId, delegationFilter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public List<UserEntity> getUsersForGroup(final String groupId, DelegationFilterSearchBean delegationFilter, final int from, final int size) {
        final Criteria criteria = getUsersEntitlementCriteria(groupId, null, null, delegationFilter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return criteria.list();
    }

    @Override
    public int getNumOfUsersForGroup(String groupId, DelegationFilterSearchBean delegationFilter) {
        final Criteria criteria = getUsersEntitlementCriteria(groupId, null, null, delegationFilter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public List<UserEntity> getUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter, final int from, final int size) {
        final Criteria criteria = getUsersEntitlementCriteria(null, roleId, null, delegationFilter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return criteria.list();
    }

    @Override
    public int getNumOfUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter) {
        final Criteria criteria = getUsersEntitlementCriteria(null, roleId, null, delegationFilter).setProjection(rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    private Criteria getUsersEntitlementCriteria(final String groupId, final String roleId, final String resourceId,
                                                 DelegationFilterSearchBean delegationFilter) {
        Criteria criteria = getCriteria();

        if (StringUtils.isNotEmpty(groupId)) {
            criteria.createAlias("groups", "g");
            criteria.add(Restrictions.eq("g.id", groupId));
        } else if (delegationFilter != null && CollectionUtils.isNotEmpty(delegationFilter.getGroupIdSet())) {
            criteria.createAlias("groups", "g");
            criteria.add(createInClauseForIds("g", "id", "GRP_ID", new ArrayList<>(delegationFilter.getGroupIdSet())));
        }

        if (StringUtils.isNotEmpty(roleId)) {
            criteria.createAlias("roles", "r");
            criteria.add(Restrictions.eq("r.id", roleId));
        } else if (delegationFilter != null && CollectionUtils.isNotEmpty(delegationFilter.getRoleIdSet())) {
            criteria.createAlias("roles", "r");
            criteria.add(createInClauseForIds("r", "id", "ROLE_ID", new ArrayList<>(delegationFilter.getRoleIdSet())));
        }

        if (StringUtils.isNotEmpty(resourceId)) {
            criteria.createAlias("resources", "res").add(Restrictions.eq("res.id", resourceId));
        }

        if (delegationFilter != null) {
            if (CollectionUtils.isNotEmpty(delegationFilter.getOrganizationIdSet())) {
                criteria.createAlias("organizationUser", "aff").add(Restrictions.in("aff.primaryKey.organization.id", delegationFilter.getOrganizationIdSet()));
            }
        }
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria;
    }

    @Override
    public List<UserEntity> getUsersForMSys(final String mSysId) {
        Criteria criteria = getSession().createCriteria(UserEntity.class).createAlias("principalList", "l")
                .add(Restrictions.eq("l.managedSysId", mSysId)).setFetchMode("principalList", FetchMode.JOIN);
        return criteria.list();
    }

    public List<UserEntity> getSuperiors(String userId, final int from, final int size) {
        Criteria criteria = getSuperiorsCriteria(userId);
        if (from > -1) {
            criteria.setFirstResult(from);
        }
        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    @Override
    public List<UserEntity> getAllSuperiors(final int from, final int size) {
        Criteria criteria = getAllSuperiorsCriteria();

        if (from > -1) {
            criteria.setFirstResult(from);
        }
        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    @Override
    public int getAllSuperiorsCount() {
        return ((Number) getAllSuperiorsCriteria().setProjection(rowCount()).uniqueResult()).intValue();
    }

    public List<UserEntity> getSubordinates(String userId, final int from, final int size) {
        Criteria criteria = getSubordinatesCriteria(userId);
        if (from > -1) {
            criteria.setFirstResult(from);
        }
        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    public int getSuperiorsCount(String userId) {
        return ((Number) getSuperiorsCriteria(userId).setProjection(rowCount()).uniqueResult()).intValue();
    }

    public int getSubordinatesCount(String userId) {
        return ((Number) getSubordinatesCriteria(userId).setProjection(rowCount()).uniqueResult()).intValue();
    }

    public List<String> getSubordinatesIds(String userId) {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class).setProjection(Projections.property("id.employeeId"))
                .add(Restrictions.eq("id.supervisorId", userId));
        return criteria.list();
    }

    public UserEntity findPrimarySupervisor(String employeeId) {
        Criteria criteria = getCriteria().createAlias("supervisors", "s").add(Restrictions.eq("id", employeeId))
                .add(Restrictions.eq("s.isPrimarySuper", true)).setProjection(Projections.property("s.supervisor"));

        return (UserEntity) criteria.uniqueResult();
    }

    private Criteria getSuperiorsCriteria(String userId) {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class).setProjection(Projections.property("supervisor"))
                .createAlias("employee", "employee").add(Restrictions.eq("employee.id", userId));
        return criteria;
    }

    private Criteria getAllSuperiorsCriteria() {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class)
                .setProjection(Projections.distinct(Projections.property("supervisor")));
        return criteria;
    }

    private Criteria getSubordinatesCriteria(String userId) {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class).setProjection(Projections.property("employee"))
                .createAlias("supervisor", "supervisor").add(Restrictions.eq("supervisor.id", userId));
        return criteria;
    }

    public List<String> getAllAttachedSupSubIds(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_LIST;
        }
        DetachedCriteria superiors = DetachedCriteria.forClass(SupervisorEntity.class).setProjection(Projections.property("id.supervisorId"))
                .add(Restrictions.in("id.employeeId", userIds));

        DetachedCriteria subordinates = DetachedCriteria.forClass(SupervisorEntity.class).setProjection(Projections.property("id.employeeId"))
                .add(Restrictions.in("id.supervisorId", userIds));

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Subqueries.propertyIn("id", superiors)); // exclude
        // existing
        // superiors
        disjunction.add(Subqueries.propertyIn("id", subordinates)); // exclude
        // existing
        // subordinates
        disjunction.add(Restrictions.in("id", userIds)); // exclude itself

        final Criteria criteria = getCriteria().setProjection(Projections.property("id")).add(disjunction)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

    public List<String> getUserIdsForAttributes(final List<SearchAttribute> searchAttributeSet, final int from, final int size) {
        List<String> retVal = null;

        if (CollectionUtils.isNotEmpty(searchAttributeSet)) {
            final Criteria criteria = getCriteria().createAlias("userAttributes", "ua").setProjection(Projections.property("id"));

            List<String> elementIdList = new ArrayList<String>();
            for (SearchAttribute atr : searchAttributeSet) {
                if (atr.getAttributeName() == null && atr.getAttributeValue() == null) {
                    continue;
                } else if (atr.getAttributeName() != null && atr.getAttributeValue() == null) {
                    criteria.add(Restrictions.eq("ua.name", atr.getAttributeName()).ignoreCase());
                } else if (atr.getAttributeName() == null && atr.getAttributeValue() != null) {
                    criteria.add(Restrictions.ilike("ua.value", atr.getAttributeValue(), this.getMatchMode(atr.getMatchType())));
                } else if (atr.getAttributeName() != null && atr.getAttributeValue() != null) {
                    criteria.add(Restrictions.and(Restrictions.eq("ua.name", atr.getAttributeName()).ignoreCase(),
                            Restrictions.ilike("ua.value", atr.getAttributeValue(), this.getMatchMode(atr.getMatchType()))));
                }
                if (StringUtils.isNotBlank(atr.getAttributeElementId())) {
                    elementIdList.add(atr.getAttributeElementId());
                }
            }


            if (CollectionUtils.isNotEmpty(elementIdList)) {
                criteria.createAlias("ua.element", "mt").add(Restrictions.in("mt.id", elementIdList));
            }

            if (from > -1) {
                criteria.setFirstResult(from);
            }

            if (size > -1) {
                criteria.setMaxResults(size);
            }
            retVal = criteria.list();
        }
        return (retVal != null) ? retVal : Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getUserIdsForRoles(final Set<String> roleIds, final int from, final int size) {
        List<String> retVal = null;
        if (CollectionUtils.isNotEmpty(roleIds)) {

            final Criteria criteria = getCriteria().createAlias("roles", "role").add(createInClauseForIds("role", "id", "ROLE_ID", new ArrayList<>(roleIds)))
                    .setProjection(Projections.property("id"));
            if (from > -1) {
                criteria.setFirstResult(from);
            }

            if (size > -1) {
                criteria.setMaxResults(size);
            }
            retVal = criteria.list();
        }
        return (retVal != null) ? retVal : Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getUserIdsForGroups(final Set<String> groupIds, final int from, final int size) {
        List<String> retVal = null;
        if (CollectionUtils.isNotEmpty(groupIds)) {
            final Criteria criteria = getCriteria().createAlias("groups", "group").add(createInClauseForIds("group", "id", "GRP_ID", new ArrayList<>(groupIds)))
                    .setProjection(Projections.property("id"));
            if (from > -1) {
                criteria.setFirstResult(from);
            }

            if (size > -1) {
                criteria.setMaxResults(size);
            }
            retVal = criteria.list();
        }
        return (retVal != null) ? retVal : Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getUserIdsForOrganizations(final Set<String> organizationIds, final int from, final int size) {
        List<String> retVal = null;
        if (CollectionUtils.isNotEmpty(organizationIds)) {
            final Criteria criteria = getCriteria().createAlias("organizationUser", "af").add(Restrictions.in("af.primaryKey.organization.id", organizationIds))
                    .setProjection(Projections.property("id"));
            if (from > -1) {
                criteria.setFirstResult(from);
            }

            if (size > -1) {
                criteria.setMaxResults(size);
            }
            retVal = criteria.list();
        }
        return (retVal != null) ? retVal : Collections.EMPTY_LIST;
    }

    @Override
    public List<String> getUserIdsForResources(final Set<String> resourceIds, final int from, final int size) {
        List<String> retVal = null;
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            final Criteria criteria = getCriteria().createAlias("resources", "resource").add(createInClauseForIds("resource", "id", "RESOURCE_ID", new ArrayList<>(resourceIds)))
                    .setProjection(Projections.property("id"));
            if (from > -1) {
                criteria.setFirstResult(from);
            }

            if (size > -1) {
                criteria.setMaxResults(size);
            }
            retVal = criteria.list();
        }
        return (retVal != null) ? retVal : Collections.EMPTY_LIST;
    }

    @Override
    public boolean isUserInGroup(String userId, String groupId) {
        final Criteria criteria = getUsersEntitlementCriteria(groupId, null, null, null);
        criteria.add(Restrictions.eq(getPKfieldName(), userId));
        return (((Number) criteria.setProjection(rowCount()).uniqueResult()).intValue() > 0);
    }

    @Override
    public boolean isUserInRole(String userId, String roleId) {
        final Criteria criteria = getUsersEntitlementCriteria(null, roleId, null, null);
        criteria.add(Restrictions.eq(getPKfieldName(), userId));
        return (((Number) criteria.setProjection(rowCount()).uniqueResult()).intValue() > 0);
    }

    @Override
    public boolean isUserInOrg(String userId, String orgId) {
        final DelegationFilterSearchBean searchBean = new DelegationFilterSearchBean();
        searchBean.addOranizationId(orgId);
        final Criteria criteria = getUsersEntitlementCriteria(null, null, null, searchBean);
        criteria.add(Restrictions.eq(getPKfieldName(), userId));
        return (((Number) criteria.setProjection(rowCount()).uniqueResult()).intValue() > 0);
    }

    @Override
    public boolean isUserEntitledToResoruce(String userId, String resourceId) {
        final Criteria criteria = getUsersEntitlementCriteria(null, null, resourceId, null);
        criteria.add(Restrictions.eq(getPKfieldName(), userId));
        return (((Number) criteria.setProjection(rowCount()).uniqueResult()).intValue() > 0);
    }

    @Override
    protected Criteria getCriteria() {
        final Criteria criteria = super.getCriteria();
        final Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.isNull("systemFlag")).add(Restrictions.ne("systemFlag", "1"));
        criteria.add(disjunction);
        return criteria;
    }

    @Override
    public List<UserEntity> getUserByLastDate(Date lastDate) {
        if (lastDate != null) {
            List<UserEntity> retVal = new ArrayList<UserEntity>();
            final Criteria criteria = getCriteria().add(
                    Restrictions.lt("lastDate", lastDate));
            return criteria.list();
        } else
            return null;
    }

    public List<UserEntity> getByEmail(String email) {
        if (email != null) {
            final Criteria criteria = getCriteria();
            criteria.createAlias("emailAddresses", "em").add(Restrictions.eq("em.emailAddress", email));
            return criteria.list();
        } else
            return null;
    }

    public List<UserEntity> findByIds(List<String> idCollection, UserSearchBean searchBean, int from, int size) {
        if (CollectionUtils.isNotEmpty(idCollection)) {
            final Criteria criteria = super.getCriteria();
            criteria.add(createInClauseForIds(criteria.getAlias(), "id", "USER_ID", idCollection));
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            if (CollectionUtils.isNotEmpty(searchBean.getSortBy())) {
                addSorting(criteria, searchBean.getSortBy());
            }
            if (from > -1 && size > -1) {
                criteria.setFirstResult(from);
                criteria.setMaxResults(size);
            }
            return criteria.list();
        }
        return Collections.EMPTY_LIST;
    }

    public int countByIds(List<String> idCollection) {
        if (CollectionUtils.isNotEmpty(idCollection)) {
            final Criteria criteria = super.getCriteria();
            criteria.add(createInClauseForIds(criteria.getAlias(), "id", "USER_ID", idCollection));
            return ((Number) criteria.setProjection(rowCount())
                    .uniqueResult()).intValue();
        }
        return 0;
    }

    @Override
    public List<UserEntity> getUserBetweenCreateDate(Date fromDate, Date toDate) {
        if (log.isDebugEnabled()) {
            log.debug("--------- created createDate ----------- : " + fromDate);
            log.debug("--------- created toDate ----------- : " + toDate);
        }
        if (fromDate != null && toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("createDate", fromDate))
                    .add(Restrictions.lt("createDate", toDate));
            return criteria.list();
        } else if (fromDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("createDate", fromDate));
            return criteria.list();
        } else if (toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.lt("createDate", toDate));
            return criteria.list();
        } else
            return null;
    }


    @Override
    public List<UserEntity> getUserBetweenStartDate(Date fromDate, Date toDate) {
        if (log.isDebugEnabled()) {
            log.debug("--------- created startDate ----------- : " + fromDate);
            log.debug("--------- created toDate ----------- : " + toDate);
        }
        if (fromDate != null && toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("startDate", fromDate))
                    .add(Restrictions.lt("startDate", toDate));
            return criteria.list();
        } else if (fromDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("startDate", fromDate));
            return criteria.list();
        } else if (toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.lt("startDate", toDate));
            return criteria.list();
        } else
            return null;
    }

    @Override
    public List<UserEntity> getUserByIds(Set<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            if (ids.size() < 2000) {
                final Criteria criteria = getCriteria()
                        .add(Restrictions.in("id", ids));
                return criteria.list();
            } else {
                HibernateTemplate template = getHibernateTemplate();
                template.setCacheQueries(true);
                String sql = String.format("FROM UserEntity r where r.id in (\'%s\')", StringUtils.join(ids, "\',\'"));
                return template.find(sql);
            }
        }
        return new ArrayList<UserEntity>(0);
    }

    @Override
    public List<UserEntity> getUserBetweenLastDate(Date fromDate, Date toDate) {
        if (log.isDebugEnabled()) {
            log.debug("--------- lastDate fromDate ----------- : " + fromDate);
            log.debug("--------- lastDate toDate ----------- : " + toDate);
        }
        if (fromDate != null && toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("lastDate", fromDate))
                    .add(Restrictions.lt("lastDate", toDate));
            return criteria.list();
        } else if (fromDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.ge("lastDate", fromDate));
            return criteria.list();
        } else if (toDate != null) {
            final Criteria criteria = getCriteria()
                    .add(Restrictions.lt("lastDate", toDate));
            return criteria.list();
        } else
            return null;
    }

    @Override
    public List<UserEntity> getUserBetweenUpdatedDate(Date fromDate, Date toDate) {
        if (log.isDebugEnabled()) {
            log.debug("--------- updated user fromdate ----------- : " + fromDate);
            log.debug("--------- updated user todate ----------- : " + toDate);
        }
        if (fromDate != null && toDate != null) {
            final Criteria criteria = getCriteria().add(
                    Restrictions.lt("lastUpdate", toDate)).add(
                    Restrictions.gt("lastUpdate", fromDate));
            return criteria.list();
        } else if (fromDate != null) {
            final Criteria criteria = getCriteria().add(
                    Restrictions.gt("lastUpdate", fromDate));
            return criteria.list();
        } else if (toDate != null) {
            final Criteria criteria = getCriteria().add(
                    Restrictions.lt("lastUpdate", toDate));
            return criteria.list();
        } else
            return null;
    }

//    private Criterion createInClauseForIds(Criteria criteria, List<String> idCollection) {
//        if (idCollection.size() <= MAX_IN_CLAUSE) {
//            return Restrictions.in(getPKfieldName(), idCollection);
//        } else {
//            Disjunction orClause = Restrictions.disjunction();
//            int start = 0;
//            int end;
//            while (start < idCollection.size()) {
//                end = start + MAX_IN_CLAUSE;
//                if (end > idCollection.size()) {
//                    end = idCollection.size();
//                }
//                final String sql = criteria.getAlias() + "_.USER_ID in ('" + StringUtils.join(idCollection.subList(start, end), "','") + "')";
//                orClause.add(Restrictions.sqlRestriction(sql));
//                start = end;
//            }
//            return orClause;
//        }
//    }

    @Override
    public LightSearchResponse getLightSearchResult(LightSearchRequest request) {
        LightSearchResponse response = new LightSearchResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        if (StringUtils.isNotBlank(request.getEmployeeId()) || StringUtils.isNotBlank(request.getEmailAddress()) || StringUtils.isNotBlank(request.getLogin())
                || StringUtils.isNotBlank(request.getLastName())) {


            final String count = " COUNT(*) as count ";
            final String fieldNames = getBaseLigthSearchColumns();

            StringBuilder sb = this.getBaseLightSearchQuery();
            this.applyWherePart(sb, request);

            //get DelegationFilterPart
            final String delegationFilterPart = this.prepareDelagationFilterPart(
                    this.getRequesterDelegationAttributes(request.getRequesterId()), request);


            //run count command
            Integer countNum = (Integer) this.getSession().createSQLQuery(getResultLightSearchQuery(sb, count,
                    delegationFilterPart.toString())).addScalar("count", IntegerType.INSTANCE).uniqueResult();
            //if count > 0 run get data command
            if (countNum != null && countNum > 0) {
                response.setCount(countNum);
                //do to it more smart
                sb.append(this.addSorting(request.getSortParam()));
                sb.append(this.getBasePaginatorQuery(request));
                response.setLightUserSearchModels(this.getSession().createSQLQuery(getResultLightSearchQuery(sb,
                        fieldNames, delegationFilterPart.toString()))
                        .addEntity(LightUserSearchModel.class).list());
            }
        }
        return response;
    }

    private StringBuilder getBasePaginatorQuery(LightSearchRequest request) {
        StringBuilder paginationBuilder = new StringBuilder();
        if (request.getSize() != -1 && request.getFrom() != -1) {
            String dbType = this.dbType;
            if ("SQLServer".equalsIgnoreCase(dbType)) {
                paginationBuilder.append("OFFSET " + request.getFrom() + " ROWS FETCH NEXT " + request.getSize() + " ROWS ONLY");
            } else if ("ORACLE_INSENSITIVE".equalsIgnoreCase(dbType)) {
                paginationBuilder.append("OFFSET " + request.getFrom() + " ROWS FETCH NEXT " + request.getSize() + " ROWS ONLY");
            } else if ("MySQL".equalsIgnoreCase(dbType)) {
                paginationBuilder.append(" LIMIT " + request.getFrom() + "," + request.getSize());
            } else if ("PostgreSQL".equalsIgnoreCase(dbType)) {
                paginationBuilder.append(" LIMIT " + request.getSize() + " OFFSET " + request.getFrom());
            }
        }
        return paginationBuilder;
    }

    private String getResultLightSearchQuery(StringBuilder sb, String returnColumns, String delegationFilterPart) {
        return sb.toString().replace("${replace}", returnColumns).replace("${delegationFilterPart}", delegationFilterPart);
    }

    private String prepareDelagationFilterPart(Map<String, UserAttribute> requesterDelegationAttributes, LightSearchRequest request) {
        StringBuilder delegationFilterPart = new StringBuilder();
        if (requesterDelegationAttributes != null) {
            boolean isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterDelegationAttributes);
            boolean isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterDelegationAttributes);
            boolean isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterDelegationAttributes);
            boolean isMngReportFilterSet = DelegationFilterHelper.isMngRptFilterSet(requesterDelegationAttributes);
            boolean isAttributeFilterSet = DelegationFilterHelper.isAttributeFilterSet(requesterDelegationAttributes);
            if (isOrgFilterSet) {
                delegationFilterPart.append(" JOIN USER_AFFILIATION usf ON usf.USER_ID = u.USER_ID AND ");
                delegationFilterPart.append(" COMPANY_ID IN ('");
                delegationFilterPart.append(StringUtils.join(DelegationFilterHelper.getOrgIdFilterFromString(requesterDelegationAttributes), "','"));
                delegationFilterPart.append("') ");
            }

            if (isGroupFilterSet) {
                delegationFilterPart.append(" JOIN USER_GRP usgr ON usgr.USER_ID = u.USER_ID AND ");
                delegationFilterPart.append(" GRP_ID IN ('");
                delegationFilterPart.append(StringUtils.join(DelegationFilterHelper.getGroupFilterFromString(requesterDelegationAttributes), "','"));
                delegationFilterPart.append("') ");
            }

            if (isRoleFilterSet) {
                delegationFilterPart.append(" JOIN USER_ROLE usrole ON usrole.USER_ID = u.USER_ID AND ");
                delegationFilterPart.append(" ROLE_ID IN ('");
                delegationFilterPart.append(StringUtils.join(DelegationFilterHelper.getRoleFilterFromString(requesterDelegationAttributes), "','"));
                delegationFilterPart.append("') ");
            }

            if (isMngReportFilterSet) {
                delegationFilterPart.append(" JOIN ORG_STRUCTURE orgStr ON orgStr.STAFF_ID = u.USER_ID AND ");
                delegationFilterPart.append(" orgStr.SUPERVISOR_ID = '" + request.getRequesterId() + "' ");
            }
            if (isAttributeFilterSet) {
                List<String> searchParams = DelegationFilterHelper.getAttributeFilterSet(requesterDelegationAttributes);
                List<SearchAttribute> searchAttributeList = null;
                if (searchParams != null) {
                    searchAttributeList = new ArrayList<>();
                    for (String param : searchParams) {
                        searchAttributeList.add(UserUtils.parseDelegationFilterAttribute(param));
                    }
                }
                if (CollectionUtils.isNotEmpty(searchAttributeList)) {

                    List<String> contitionsList = new ArrayList<>();
                    for (SearchAttribute sa : searchAttributeList) {
                        if (StringUtils.isNotEmpty(sa.getAttributeName()) &&
                                StringUtils.isNotBlank(sa.getAttributeValue())
                                && sa.getMatchType() != null) {
                            StringBuilder clause = new StringBuilder();
                            clause.append("(uattributes.NAME='");
                            clause.append(sa.getAttributeName());
                            clause.append("' AND ");
                            clause.append("uattributes.VALUE LIKE('");

                            switch (sa.getMatchType()) {
                                case END_WITH:
                                    clause.append("%" + sa.getAttributeValue());
                                    break;
                                case EXACT:
                                    clause.append(sa.getAttributeValue());
                                    break;
                                case STARTS_WITH:
                                    clause.append(sa.getAttributeValue() + "%");
                                    break;
                                default:
                                    break;
                            }
                            clause.append("'))");
                            contitionsList.add(clause.toString());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(contitionsList)) {
                        delegationFilterPart.append(" JOIN USER_ATTRIBUTES uattributes ON uattributes.USER_ID = u.USER_ID AND (");
                        delegationFilterPart.append(StringUtils.join(contitionsList, " OR "));
                        delegationFilterPart.append(") ");
                    }
                }
            }
            //todo parf of delegation filter
        }
        return delegationFilterPart.toString();
    }


    private StringBuilder getBaseLightSearchQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ${replace} ");
        sb.append("FROM USERS u LEFT JOIN EMAIL_ADDRESS ea ON ea.IS_DEFAULT = 'Y' AND ea.EMAIL_ID = (SELECT MAX(EMAIL_ID) FROM ");
        sb.append(" EMAIL_ADDRESS WHERE PARENT_ID = u.USER_ID) LEFT JOIN PHONE p ON p.IS_DEFAULT = 'Y' AND ");
        sb.append(" p.PHONE_ID = (SELECT MAX(PHONE_ID) FROM PHONE WHERE PARENT_ID = u.USER_ID) JOIN LOGIN l ON ");
        sb.append(" l.USER_ID = u.USER_ID AND l.MANAGED_SYS_ID = '0' ${delegationFilterPart} ");
        sb.append(" WHERE ");
        return sb;
    }

    private String getBaseLigthSearchColumns() {

        String fieldNames = "u.USER_ID AS userId, u.EMPLOYEE_ID AS employeeId, u.FIRST_NAME AS firstName, " +
                " u.LAST_NAME AS lastName, u.STATUS AS status, u.SECONDARY_STATUS AS secondaryStatus, " +
                " u.NICKNAME AS nickname, ea.EMAIL_ADDRESS AS email, l.LOGIN AS defaultLogin," +
                "CONCAT(p.COUNTRY_CD, p.AREA_CD, p.PHONE_NBR,p.PHONE_EXT) AS  defaultPhone ";
        return fieldNames;

    }

    private void applyWherePart(StringBuilder sb, LightSearchRequest request) {
        if (StringUtils.isNotBlank(request.getEmployeeId())) {
            sb.append(" u.EMPLOYEE_ID LIKE ('" + request.getEmployeeId() + "%') ");
        }

        if (StringUtils.isNotBlank(request.getEmailAddress())) {
            if (StringUtils.isNotBlank(request.getEmployeeId())) {
                sb.append(" AND ");
            }
            sb.append(" ea.EMAIL_ADDRESS LIKE ('" + request.getEmailAddress() + "%') ");
        }

        if (StringUtils.isNotBlank(request.getLogin())) {
            if (StringUtils.isNotBlank(request.getEmployeeId()) || StringUtils.isNotBlank(request.getEmailAddress())) {
                sb.append(" AND ");
            }
            sb.append(" l.LOGIN LIKE ('" + request.getLogin() + "%') ");
        }

        if (StringUtils.isNotBlank(request.getLastName())) {
            if (StringUtils.isNotBlank(request.getEmployeeId()) ||
                    StringUtils.isNotBlank(request.getLogin()) ||
                    StringUtils.isNotBlank(request.getEmailAddress())) {
                sb.append(" AND ");
            }
            sb.append(" u.LAST_NAME LIKE ('" + request.getLastName() + "%') ");
        }

        if (request.getStatus() != null) {
            if (StringUtils.isNotBlank(request.getEmployeeId()) ||
                    StringUtils.isNotBlank(request.getLogin()) ||
                    StringUtils.isNotBlank(request.getEmailAddress()) ||
                    StringUtils.isNotBlank(request.getLastName())) {
                sb.append(" AND ");
            }
            sb.append(" u.STATUS='" + request.getStatus().name() + "' ");
        }
        if (request.getSecondaryStatus() != null) {
            if (StringUtils.isNotBlank(request.getEmployeeId()) ||
                    StringUtils.isNotBlank(request.getLogin()) ||
                    StringUtils.isNotBlank(request.getEmailAddress()) ||
                    StringUtils.isNotBlank(request.getLastName()) ||
                    request.getStatus() != null) {
                sb.append(" AND ");
            }
            sb.append(" u.SECONDARY_STATUS='" + request.getStatus().name() + "' ");
        }
    }


    private Map<String, UserAttribute> getRequesterDelegationAttributes(String requesterId) {
        if (StringUtils.isBlank(requesterId)) {
            return null;
        }
        String sql = "SELECT ua.ID as id," +
                "ua.VALUE as value, uav.VALUE as " + ("PostgreSQL".equals(this.dbType) ? "values" : "'values'") +
                ", ua.IS_MULTIVALUED as isMultivalued, ua.USER_ID as userId, ua.NAME as name " +
                " FROM USER_ATTRIBUTES ua LEFT JOIN  USER_ATTRIBUTE_VALUES uav ON uav.USER_ATTRIBUTE_ID" +
                "=ua.ID WHERE ua.USER_ID='%s' AND ua.NAME IN" +
                " ( 'DLG_FLT_APP', 'DLG_FLT_DEPT', 'DLG_FLT_DIV','DLG_FLT_GRP','DLG_FLT_ORG','DLG_FLT_ROLE'" +
                ",'DLG_FLT_MNG_RPT','DLG_FLT_PARAM','DLG_FLT_USE_ORG_INH') ";
        List<LightSearchDelegationAttributeModel> userAttributeEntityList =
                this.getSession().createSQLQuery(String.format(sql, requesterId)).
                        addEntity(LightSearchDelegationAttributeModel.class).list();
        if (CollectionUtils.isEmpty(userAttributeEntityList)) {
            return null;
        }
        Map<String, UserAttribute> userAttributeEntityMap = new HashMap<>();
        for (LightSearchDelegationAttributeModel userAttributeEntity : userAttributeEntityList) {
            UserAttribute userAttribute = new UserAttribute();
            userAttribute.setId(userAttributeEntity.getId());
            userAttribute.setName(userAttributeEntity.getName());
            userAttribute.setValue(userAttributeEntity.getValue());
            userAttribute.setValues(userAttributeEntity.getValues());
            userAttribute.setIsMultivalued("Y".equalsIgnoreCase(userAttributeEntity.getIsMultivalued()));
            userAttribute.setUserId(userAttributeEntity.getUserId());
            userAttributeEntityMap.put(userAttributeEntity.getName(), userAttribute);
        }
        return userAttributeEntityMap;
    }

    private StringBuilder addSorting(List<SortParam> sortParam) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ORDER BY ");
        if (sortParam == null) {
            return sb.append("u.USER_ID ");
        }
        for (SortParam sort : sortParam) {
            String orderDir = (sort.getOrderBy() == null) ? OrderConstants.ASC.getValue() : sort.getOrderBy().getValue();
            if ("name".equals(sort.getSortBy())) {
                sb.append("u.LAST_NAME " + orderDir + ", ");
                sb.append("u.FIRST_NAME " + orderDir);
            } else if ("phone".equals(sort.getSortBy())) {
                sb.append("p.COUNTRY_CD " + orderDir + ", ");
                sb.append("p.AREA_CD " + orderDir + ", ");
                sb.append("p.PHONE_NBR " + orderDir + ", ");
                sb.append("p.PHONE_EXT " + orderDir);
            } else if ("email".equals(sort.getSortBy())) {
                sb.append("ea.EMAIL_ADDRESS " + orderDir);
            } else if ("userStatus".equals(sort.getSortBy())) {
                sb.append("u.STATUS " + orderDir);
            } else if ("accountStatus".equals(sort.getSortBy())) {
                sb.append("u.SECONDARY_STATUS " + orderDir);
            } else if ("principal".equals(sort.getSortBy())) {
                sb.append("l.LOGIN " + orderDir);
            }
            sb.append(",");
        }
        if (',' == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(" ");
        return sb;
    }

    private void addSorting(Criteria criteria, List<SortParam> sortParam) {

        for (SortParam sort : sortParam) {
            OrderConstants orderDir = (sort.getOrderBy() == null) ? OrderConstants.ASC : sort.getOrderBy();

            if ("name".equals(sort.getSortBy())) {
                criteria.addOrder(createOrder("firstName", orderDir));
                criteria.addOrder(createOrder("lastName", orderDir));
            } else if ("phone".equals(sort.getSortBy())) {
                criteria.createAlias("phones", "p", Criteria.LEFT_JOIN, Restrictions.eq("p.isDefault", true));
                criteria.addOrder(createOrder("p.countryCd", orderDir));
                criteria.addOrder(createOrder("p.areaCd", orderDir));
                criteria.addOrder(createOrder("p.phoneNbr", orderDir));
                criteria.addOrder(createOrder("p.phoneExt", orderDir));
            } else if ("email".equals(sort.getSortBy())) {
                criteria.createAlias("emailAddresses", "ea", Criteria.LEFT_JOIN, Restrictions.eq("ea.isDefault", true));
                criteria.addOrder(createOrder("ea.emailAddress", orderDir));
            } else if ("userStatus".equals(sort.getSortBy())) {
                criteria.addOrder(createOrder("status", orderDir));
            } else if ("accountStatus".equals(sort.getSortBy())) {
                criteria.addOrder(createOrder("secondaryStatus", orderDir));
            } else if ("principal".equals(sort.getSortBy())) {
                criteria.createAlias("principalList", "l", Criteria.LEFT_JOIN, Restrictions.eq("l.managedSysId", sysConfiguration.getDefaultManagedSysId()));
                criteria.addOrder(createOrder("l.login", orderDir));
            } else if ("organization".equals(sort.getSortBy())) {
                criteria.createAlias("organizationUser.primaryKey.organization", "org", Criteria.LEFT_JOIN).add(
                        Restrictions.or(Restrictions.isNull("org.organizationType.id"), Restrictions.eq("org.organizationType.id", organizationTypeId)));
                criteria.addOrder(createOrder("org.name", orderDir));
            } else if ("department".equals(sort.getSortBy())) {
                criteria.createAlias("organizationUser.primaryKey.organization", "dep", Criteria.LEFT_JOIN).add(
                        Restrictions.or(Restrictions.isNull("dep.organizationType.id"), Restrictions.eq("dep.organizationType.id", departmentTypeId)));
                criteria.addOrder(createOrder("dep.name", orderDir));
            } else {
                criteria.addOrder(createOrder(sort.getSortBy(), orderDir));
            }
        }
    }

}
