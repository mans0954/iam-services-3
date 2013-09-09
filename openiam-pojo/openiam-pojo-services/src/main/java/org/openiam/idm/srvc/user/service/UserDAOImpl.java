package org.openiam.idm.srvc.user.service;

import static org.hibernate.criterion.Projections.rowCount;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.SearchAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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

    @Override
    protected String getPKfieldName() {
        return "userId";
    }

    @Override
    public UserEntity findByIdDelFlt(String userId, DelegationFilterSearchBean delegationFilter) {
        Criteria criteria = getCriteria();
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.isNull("systemFlag")).add(Restrictions.ne("systemFlag", "1"));
        criteria.add(disjunction);

        if(delegationFilter != null) {
            if (CollectionUtils.isNotEmpty(delegationFilter.getOrganizationIdSet())) {
                criteria.createAlias("affiliations", "aff").add(
                        Restrictions.in("aff.id", delegationFilter.getOrganizationIdSet()));
            }

            if (CollectionUtils.isNotEmpty(delegationFilter.getGroupIdSet())) {
                criteria.createAlias("groups", "g");
                criteria.add(Restrictions.in("g.grpId", delegationFilter.getGroupIdSet()));
            }

            if (CollectionUtils.isNotEmpty(delegationFilter.getRoleIdSet())) {
                criteria.createAlias("roles", "r");
                criteria.add(Restrictions.in("r.roleId", delegationFilter.getRoleIdSet()));
            }
        }

        criteria.add(Restrictions.eq(getPKfieldName(), userId));

        return (UserEntity) criteria.uniqueResult();
    }

    public List<UserEntity> findByLastUpdateRange(Date startDate, Date endDate) {
        log.debug("finding User based on the lastUpdate date range that is provided");
        try {
            return getCriteria().add(Restrictions.between("lastUpdate", startDate, endDate)).list();
        } catch (HibernateException re) {
            re.printStackTrace();
            log.error("findByLastUpdateRange failed.", re);
            throw re;
        }
    }

    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search) {
        final Criteria criteria = getCriteria();
        // check systemFlag
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.isNotNull("systemFlag")).add(Restrictions.ne("systemFlag", "1"));
        criteria.add(disjunction);
        if (StringUtils.isNotEmpty(search.getRole())) {
            criteria.createAlias("roles", "r");
            criteria.add(Restrictions.eq("r.roleId", search.getRole()));
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

    private Criteria getExampleCriteria(UserSearchBean searchBean) {
        boolean ORACLE_INSENSITIVE = "ORACLE_INSENSITIVE".equalsIgnoreCase(dbType);

        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            criteria.add(Restrictions.eq(getPKfieldName(), searchBean.getKey()));
        } else {
            // check systemFlag
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.isNull("systemFlag")).add(Restrictions.ne("systemFlag", "1"));
            criteria.add(disjunction);
            if (searchBean.getShowInSearch() != null) {
                criteria.add(Restrictions.eq("showInSearch", searchBean.getShowInSearch()));
            }
            if (StringUtils.isNotEmpty(searchBean.getFirstName())) {
                criteria.add(getStringCriterion("firstName", searchBean.getFirstName(), ORACLE_INSENSITIVE));
            }
            if (StringUtils.isNotEmpty(searchBean.getLastName())) {
                criteria.add(getStringCriterion("lastName", searchBean.getLastName(), ORACLE_INSENSITIVE));
            }
            if (StringUtils.isNotEmpty(searchBean.getNickName())) {
                criteria.add(getStringCriterion("nickname", searchBean.getNickName()));
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
            if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdList())) {
                criteria.createAlias("affiliations", "aff").add(
                        Restrictions.in("aff.id", searchBean.getOrganizationIdList()));
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
            if (StringUtils.isNotEmpty(searchBean.getEmailAddress())) {
                criteria.createAlias("emailAddresses", "em");
                disjunction = Restrictions.disjunction();
                disjunction.add(getStringCriterion("em.emailAddress", searchBean.getEmailAddress(), ORACLE_INSENSITIVE))
                        .add(getStringCriterion("email", searchBean.getEmailAddress(), ORACLE_INSENSITIVE));
                criteria.add(disjunction);
            }
            if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
                criteria.createAlias("groups", "g");
                criteria.add(Restrictions.in("g.grpId", searchBean.getGroupIdSet()));
            }
            if (StringUtils.isNotEmpty(searchBean.getEmployeeId())) {
                criteria.add(Restrictions.eq("employeeId", searchBean.getEmployeeId()));
            }
            if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
                criteria.createAlias("roles", "urv");
                criteria.add(Restrictions.in("r.roleId", searchBean.getRoleIdSet()));
            }

            if (StringUtils.isNotEmpty(searchBean.getAttributeName()) || StringUtils.isNotEmpty(searchBean.getAttributeValue())
                    || StringUtils.isNotEmpty(searchBean.getAttributeElementId())
                    || (searchBean.getAttributeList() != null && !searchBean.getAttributeList().isEmpty())) {
                criteria.createAlias("userAttributes", "ua");
                if (searchBean.getAttributeList() != null && !searchBean.getAttributeList().isEmpty()) {
                    List<String> nameList = new ArrayList<String>();
                    List<String> valueList = new ArrayList<String>();
                    for (SearchAttribute atr : searchBean.getAttributeList()) {
                        if (atr.getAttributeName() != null) {
                            nameList.add(atr.getAttributeName());
                        }
                        if (atr.getAttributeValue() != null) {
                            valueList.add(atr.getAttributeValue());
                        }
                    }

                    if (nameList.size() > 0) {
                        criteria.add(Restrictions.in("ua.name", nameList));
                    }
                    if (valueList.size() > 0) {
                        criteria.add(Restrictions.in("ua.value", valueList));
                    }
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeName())) {
                    criteria.add((ORACLE_INSENSITIVE) ? Restrictions.eq("ua.name", searchBean.getAttributeName()).ignoreCase() : Restrictions
                            .eq("ua.name", searchBean.getAttributeName()));
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeValue())) {
                    criteria.add(getStringCriterion("ua.value", searchBean.getAttributeValue(), ORACLE_INSENSITIVE));
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeElementId())) {
                    criteria.add(Restrictions.eq("ua.metadataElementId", searchBean.getAttributeElementId()));
                }
            }
            /* Login */
            if (StringUtils.isNotEmpty(searchBean.getPrincipal()) || StringUtils.isNotEmpty(searchBean.getDomainId())
                    || StringUtils.isNotEmpty(searchBean.getLoggedIn())) {
                criteria.createAlias("principalList", "lg");
                if (StringUtils.isNotEmpty(searchBean.getPrincipal())) {
                    criteria.add(getStringCriterion("lg.login", searchBean.getPrincipal(), ORACLE_INSENSITIVE));
                }
                if (StringUtils.isNotEmpty(searchBean.getDomainId())) {
                    criteria.add(Restrictions.eq("lg.domainId", searchBean.getDomainId()));
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

    // private Criteria getUsersForResourceCriteria(final String resourceId) {
    // return getCriteria().createAlias("resourceUsers",
    // "ru").add(Restrictions.eq("ru.resourceId", resourceId));
    // }

    @Override
    public List<UserEntity> getUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter, final int from, final int size) {
        final Criteria criteria = getUsersEntitlementCriteria(null, null, resourceId, delegationFilter);

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
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

    // private Criteria getUsersForRoleCriteria(final String roleId,
    // DelegationFilterSearchBean delegationFilter) {
    // return getCriteria().createAlias("userRoles",
    // "ur").add(Restrictions.eq("ur.roleId", roleId));
    // }

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
            criteria.add(Restrictions.eq("g.grpId", groupId));
        } else if (delegationFilter != null && CollectionUtils.isNotEmpty(delegationFilter.getGroupIdSet())) {
            criteria.createAlias("groups", "g");
            criteria.add(Restrictions.in("g.grpId", delegationFilter.getGroupIdSet()));
        }

        if (StringUtils.isNotEmpty(roleId)) {
            criteria.createAlias("roles", "r");
            criteria.add(Restrictions.eq("r.roleId", roleId));
        } else if (delegationFilter != null && CollectionUtils.isNotEmpty(delegationFilter.getRoleIdSet())) {
            criteria.createAlias("roles", "r");
            criteria.add(Restrictions.in("r.roleId", delegationFilter.getRoleIdSet()));
        }

        if (StringUtils.isNotEmpty(resourceId)) {
            criteria.createAlias("resources", "r").add(Restrictions.eq("r.resourceId", resourceId));
        }

        if (delegationFilter != null) {
            if (CollectionUtils.isNotEmpty(delegationFilter.getOrganizationIdSet())) {
                criteria.createAlias("affiliations", "aff").add(
                        Restrictions.in("aff.id", delegationFilter.getOrganizationIdSet()));
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
        return ((Number)getSuperiorsCriteria(userId)
                .setProjection(rowCount()).uniqueResult()).intValue();
    }

    public int getSubordinatesCount(String userId) {
        return ((Number)getSubordinatesCriteria(userId)
                .setProjection(rowCount()).uniqueResult()).intValue();
    }

    private Criteria getSuperiorsCriteria(String userId) {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class)
                .setProjection(Projections.property("supervisor"))
                .createAlias("employee","employee")
                .add(Restrictions.eq("employee.userId", userId));
        return criteria;
    }

    private Criteria getSubordinatesCriteria(String userId) {
        Criteria criteria = getSession().createCriteria(SupervisorEntity.class)
                .setProjection(Projections.property("employee"))
                .createAlias("supervisor","supervisor")
                .add(Restrictions.eq("supervisor.userId", userId));
        return criteria;
    }

    public List<String> getAllAttachedSupSubIds(String userId) {

        DetachedCriteria superiors = DetachedCriteria.forClass(SupervisorEntity.class)
                .setProjection(Projections.property("supervisor.userId"))
                .add(Restrictions.eq("employee.userId", userId));

        DetachedCriteria subordinates = DetachedCriteria.forClass(SupervisorEntity.class)
                .setProjection(Projections.property("employee.userId"))
                .add(Restrictions.eq("supervisor.userId", userId));

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Subqueries.propertyIn("userId", superiors)); // exclude existing superiors
        disjunction.add(Subqueries.propertyIn("userId", subordinates)); // exclude existing subordinates
        disjunction.add(Restrictions.eq("userId", userId)); // exclude itself
        disjunction.add(Restrictions.eq("systemFlag", "1")); // exclude system users

        Criteria criteria = getSession().createCriteria(UserEntity.class)
                .setProjection(Projections.property("userId"))
                .add(disjunction)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
