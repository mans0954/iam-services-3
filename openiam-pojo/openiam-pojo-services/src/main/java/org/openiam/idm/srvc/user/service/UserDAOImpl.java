package org.openiam.idm.srvc.user.service;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;

import org.hibernate.criterion.*;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.UserSearch;

import org.openiam.idm.srvc.user.dto.SearchAttribute;

import javax.naming.InitialContext;
import java.util.*;

import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access implementation for domain model class User and UserWS. UserWS is similar to User,
 * however, the interface has been simplified to support usage in a web service.
 *
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
@Repository("userDAO")
public class UserDAOImpl extends BaseDaoImpl<UserEntity, String> implements UserDAO {
    @Autowired
    private String dbType;

    @Override
    protected String getPKfieldName() {
        return "userId";
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
    @Deprecated
    public List<UserEntity> search(UserSearch search) {
        if (dbType != null && dbType.equalsIgnoreCase("ORACLE_INSENSITIVE")) {
            return searchOracleInsensitive(search);
        }
        return defaultSearch(search);
    }
    @Deprecated
    private List<UserEntity> searchOracleInsensitive(UserSearch search) {

        boolean firstName = false;
        boolean lastName = false;
        boolean nickName = false;
        boolean status = false;
        boolean secondaryStatus = false;
        boolean deptCd = false;
        boolean division = false;
        boolean phoneAreaCd = false;
        boolean phoneNbr = false;
        boolean employeeId = false;
        boolean groupId = false;
        boolean roleId = false;
        boolean emailAddress = false;
        boolean orgId = false;
        boolean userId = false;
        boolean principal = false;
        boolean domainId = false;
        boolean attributeName = false;
        boolean attributeValue = false;
        boolean metadataElementId = false;
        boolean showInSearch = false;
        boolean locationId = false;
        boolean createDate = false;

        boolean userTypeInd = false;
        boolean classification = false;
        boolean orgName = false;
        boolean startDate = false;
        boolean lastDate = false;

        boolean zipCode = false;
        boolean dateOfBirth = false;

        boolean bOrgIdList = false;
        boolean bDeptIdList = false;
        boolean bDivIdList = false;
        boolean bAttrIdList = false;


        List<String> nameList = new ArrayList<String>();
        List<String> valueList = new ArrayList<String>();

        String select =
                " select /*+ INDEX(IDX_USER_FIRSTNAME_UPPER) INDEX(IDX_USER_LASTNAME_UPPER) INDEX(IDX_LOGIN_PRINCIPAL_UPPER) INDEX(IDX_UA_NAME_UPPER)  */ "
                +
                " DISTINCT u.USER_ID, u.TYPE_ID, " +
                " u.TITLE, u.MIDDLE_INIT, u.LAST_NAME, u.FIRST_NAME," +
                " u.BIRTHDATE, u.STATUS, u.SECONDARY_STATUS, u.DEPT_NAME, u.DEPT_CD, " +
                " u.LAST_UPDATE, u.CREATED_BY, u.CREATE_DATE, u.SEX, " +
                " u.USER_TYPE_IND, u.SUFFIX, u.PREFIX, u.LAST_UPDATED_BY," +
                " u.LOCATION_NAME, u.LOCATION_CD, u.EMPLOYEE_TYPE, u.EMPLOYEE_ID, " +
                " u.JOB_CODE, u.MANAGER_ID, u.COMPANY_OWNER_ID, u.COMPANY_ID, " +
                " u.LAST_DATE, u.START_DATE, u.COST_CENTER, u.DIVISION," +
                " u.PASSWORD_THEME, u.NICKNAME, u.MAIDEN_NAME, u.MAIL_CODE, " +
                " u.COUNTRY, u.BLDG_NUM, u.STREET_DIRECTION, u.SUITE,  " +
                " u.ADDRESS1, u.ADDRESS2, u.ADDRESS3, u.ADDRESS4, u.ADDRESS5, u.ADDRESS6, u.ADDRESS7," +
                " u.CITY, u.STATE, u.POSTAL_CD, u.EMAIL_ADDRESS, u.ALTERNATE_ID, u.USER_OWNER_ID, u.DATE_PASSWORD_CHANGED, u.DATE_CHALLENGE_RESP_CHANGED, "
                +
                " u.PHONE_NBR, u.PHONE_EXT, u.AREA_CD, u.COUNTRY_CD, u.CLASSIFICATION, u.SHOW_IN_SEARCH, u.DEL_ADMIN " +
                " from 	USERS u " +
                "  		LEFT JOIN LOGIN lg ON ( lg.USER_ID = u.USER_ID) " +
                "  		LEFT JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID) " +
                "  		LEFT JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) " +
                "  		LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID) " +
                "  		LEFT JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) " +
                "  		LEFT JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) " +
                "	 	LEFT JOIN USER_ROLE urv on (u.USER_ID = urv.USER_ID) ";


        StringBuffer where = new StringBuffer();
        where.append(" (u.SYSTEM_FLAG is null or u.SYSTEM_FLAG<>:systemFlag) ");
        if(search.getShowInSearch() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.SHOW_IN_SEARCH = :showInSearch ");
            showInSearch = true;
        }


        if(search.getUserId() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.USER_ID = :userId ");
            userId = true;
        }

        if(search.getUserTypeInd() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.USER_TYPE_IND = :userTypeInd ");
            userTypeInd = true;
        }

        if(search.getLocationCd() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.LOCATION_CD = :locationCd ");
            locationId = true;
        }

        if(search.getClassification() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.CLASSIFICATION = :classification ");
            classification = true;
        }

        if(search.getFirstName() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER(u.FIRST_NAME) like :firstName ");
            firstName = true;
        }
        if(search.getLastName() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER( u.LAST_NAME) like :lastName ");
            lastName = true;
        }
        if(search.getNickName() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.NICKNAME like :nickName ");
            nickName = true;
        }

        if(search.getStatus() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.STATUS = :status ");
            status = true;
        }

        if(search.getSecondaryStatus() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.SECONDARY_STATUS = :secondaryStatus ");
            secondaryStatus = true;
        }


        if(search.getStartDate() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.START_DATE = :startDate ");
            startDate = true;
        }
        if(search.getLastDate() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.LAST_DATE = :lastDate ");
            lastDate = true;
        }

        if(search.getDateOfBirth() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.BIRTHDATE = :dateOfBirth ");
            dateOfBirth = true;
        }


        if(search.getZipCode() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.POSTAL_CD = :zipCode ");
            zipCode = true;
        }

        if(search.getDeptCd() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DEPT_CD = :deptCd ");
            deptCd = true;
        }
        if(search.getDivision() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DIVISION = :division ");
            division = true;
        }

        if(search.getEmployeeId() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.EMPLOYEE_ID = :employeeId ");
            employeeId = true;
        }
        if(search.getOrgId() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.COMPANY_ID = :orgId ");
            orgId = true;
        }
        if(search.getOrgName() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER(c.COMPANY_NAME) like :orgName ");
            orgName = true;
        }
        if(search.getPhoneAreaCd() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" p.AREA_CD = :phoneAreaCd ");
            phoneAreaCd = true;
        }
        if(search.getPhoneNbr() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" p.PHONE_NBR = :phoneNbr ");
            phoneNbr = true;
        }

        if(search.getEmailAddress() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            //where.append(" u.EMAIL_ADDRESS = :emailAddress ");
            where.append(
                    " ( UPPER(em.EMAIL_ADDRESS) LIKE :emailAddress  OR UPPER(u.EMAIL_ADDRESS) LIKE :emailAddress) ");

            //where.append(" em.EMAIL_ADDRESS LIKE :emailAddress ");
            emailAddress = true;
        }
        if(!search.getGroupIdList().isEmpty()) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" g.GRP_ID in (:groupList) ");
            groupId = true;
        }
        if(!search.getRoleIdList().isEmpty()) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" urv.ROLE_ID in (:roleList) ");
            where.append(" and urv.SERVICE_ID = :domainId ");
            roleId = true;
        }

        /* org list */
        if(!search.getOrgIdList().isEmpty()) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.COMPANY_ID in (:orgList)  ");
            bOrgIdList = true;
        }

        if(!search.getDeptIdList().isEmpty()) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DEPT_CD in (:deptList)  ");
            bDeptIdList = true;
        }

        /* division list  */


        if(!search.getDivisionIdList().isEmpty()) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DIVISION in (:divisionList)  ");
            bDivIdList = true;
        }

        /* Login  */
        if(search.getPrincipal() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER(lg.LOGIN) like :principal ");
            principal = true;
        }

        if(search.getDomainId() != null) {
             if(where.length() > 0) {
               where.append(" and ");
            }
            where.append(" lg.SERVICE_ID = :domainId ");
            domainId = true;
        }

        if(search.getLoggedIn() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            if(search.getLoggedIn().equalsIgnoreCase("Yes")) {
                where.append(" lg.LAST_LOGIN IS NOT NULL ");
            } else {
                where.append(" lg.LAST_LOGIN IS NULL ");
            }

        }

        /* User Attributes fields */
        if(search.getAttributeName() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER(ua.NAME) = :attributeName ");
            attributeName = true;
        }
        if(search.getAttributeValue() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" UPPER(ua.VALUE) like :attributeValue ");
            attributeValue = true;
        }
        if(search.getAttributeElementId() != null) {
            if(where.length() > 0) {
                where.append(" and ");
            }
            where.append(" ua.METADATA_ID = :elementId ");
            metadataElementId = true;
        }

        if(where.length() > 0) {
            select = select + " WHERE " + where.toString();
        }

        select = select + "  ORDER BY u.LAST_NAME, u.FIRST_NAME";
        log.debug("search select: " + select);


        Session session = getSession();

        SQLQuery qry = session.createSQLQuery(select);
        qry.addEntity(UserEntity.class);
        qry.setString("systemFlag", "1");

        if(userId) {
            qry.setString("userId", search.getUserId());
        }
        if(firstName) {
            qry.setString("firstName", search.getFirstName().toUpperCase());
        }
        if(lastName) {
            qry.setString("lastName", search.getLastName().toUpperCase());
        }
        if(nickName) {
            qry.setString("nickName", search.getNickName());
        }
        if(status) {
            qry.setString("status", search.getStatus());
        }
        if(secondaryStatus) {
            qry.setString("secondaryStatus", search.getSecondaryStatus());
        }


        if(createDate) {
            qry.setDate("createDate", search.getCreateDate());
        }
        if(startDate) {
            qry.setDate("startDate", search.getStartDate());
        }
        if (lastDate) {
            qry.setDate("lastDate", search.getLastDate());
        }
        if (dateOfBirth) {
            qry.setDate("dateOfBirth", search.getDateOfBirth());
        }

        if (zipCode) {
            qry.setString("zipCode", search.getZipCode());
        }


        if (deptCd) {
            qry.setString("deptCd", search.getDeptCd());
        }
        if (division) {
            qry.setString("division", search.getDivision());
        }
        if (locationId) {
            qry.setString("locationCd", search.getLocationCd());
        }

        if (employeeId) {
            qry.setString("employeeId", search.getEmployeeId());
        }
        if (orgId) {
            qry.setString("orgId", search.getOrgId());
        }
        if (orgName) {
            qry.setString("orgName", search.getOrgName().toUpperCase());
        }
        if (phoneAreaCd) {
            qry.setString("phoneAreaCd", search.getPhoneAreaCd());
        }
        if (phoneNbr) {
            qry.setString("phoneNbr", search.getPhoneNbr());
        }
        if (emailAddress) {
            qry.setString("emailAddress", search.getEmailAddress().toUpperCase());
        }
        if (principal) {
            qry.setString("principal", search.getPrincipal().toUpperCase());
        }
        if (domainId) {
            qry.setString("domainId", search.getDomainId());
        }
        if (attributeName) {
            qry.setString("attributeName", search.getAttributeName().toUpperCase());
        }
        if (attributeValue) {
            qry.setString("attributeValue", search.getAttributeValue().toUpperCase());
        }
        if (metadataElementId) {
            qry.setString("elementId", search.getAttributeElementId());
        }
        if (showInSearch) {
            qry.setInteger("showInSearch", search.getShowInSearch());
        }
        if (groupId) {
            qry.setParameterList("groupList", search.getGroupIdList());
            //qry.setString("groupId", search.getGroupId());
        }
        if (roleId) {
            qry.setParameterList("roleList", search.getRoleIdList());
            //qry.setString("role", search.getRoleId());
        }
        if (classification) {
            qry.setString("classification", search.getClassification());
        }
        if (userTypeInd) {
            qry.setString("userTypeInd", search.getUserTypeInd());
        }

        if (bOrgIdList) {
            qry.setParameterList("orgList", search.getOrgIdList());

        }
        if (bDeptIdList) {
            qry.setParameterList("deptList", search.getDeptIdList() );

        }

        if (bDivIdList) {
            qry.setParameterList("divisionList", search.getDivisionIdList());

        }

        if (bAttrIdList)  {
            qry.setParameterList("nameList", nameList);
            qry.setParameterList("valueList", valueList);

        }


        if (search.getMaxResultSize() != null && search.getMaxResultSize().intValue() > 0) {
            qry.setFetchSize(search.getMaxResultSize().intValue());
            qry.setMaxResults(search.getMaxResultSize().intValue());
        } else {
//            qry.setFetchSize(this.maxResultSetSize);
//            qry.setMaxResults(this.maxResultSetSize);
        }
        try {
            List<UserEntity> result = (List<UserEntity>) qry.list();
            if (result == null || result.size() == 0) {
                log.debug("search result is null");
                return null;
            }
            log.debug("search resultset size=" + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }
    @Deprecated
    private List<UserEntity> defaultSearch(UserSearch search) {

        boolean firstName = false;
        boolean lastName = false;
        boolean nickName = false;
        boolean status = false;
        boolean secondaryStatus = false;
        boolean deptCd = false;
        boolean division = false;
        boolean phoneAreaCd = false;
        boolean phoneNbr = false;
        boolean employeeId = false;
        boolean groupId = false;
        boolean roleId = false;
        boolean emailAddress = false;
        boolean orgId = false;
        boolean userId = false;
        boolean principal = false;
        boolean domainId = false;
        boolean attributeName = false;
        boolean attributeValue = false;
        boolean metadataElementId = false;
        boolean showInSearch = false;
        boolean locationCd = false;

        boolean userTypeInd = false;
        boolean classification = false;
        boolean orgName = false;
        boolean zipCode = false;

        boolean startDate = false;
        boolean lastDate = false;
        boolean dateOfBirth = false;

        boolean bOrgIdList = false;
        boolean bDeptIdList = false;
        boolean bDivIdList = false;
        boolean bAttrIdList = false;

        StringBuilder join = new StringBuilder();

        String select = " select DISTINCT u.USER_ID, u.TYPE_ID, " +
                        " u.TITLE, u.MIDDLE_INIT, u.LAST_NAME, u.FIRST_NAME," +
                        " u.BIRTHDATE, u.STATUS, u.SECONDARY_STATUS, u.DEPT_NAME, u.DEPT_CD, " +
                        " u.LAST_UPDATE, u.CREATED_BY, u.CREATE_DATE, u.SEX, " +
                        " u.USER_TYPE_IND, u.SUFFIX, u.PREFIX, u.LAST_UPDATED_BY," +
                        " u.LOCATION_NAME, u.LOCATION_CD, u.EMPLOYEE_TYPE, u.EMPLOYEE_ID, " +
                        " u.JOB_CODE, u.MANAGER_ID, u.COMPANY_OWNER_ID, u.COMPANY_ID, " +
                        " u.LAST_DATE, u.START_DATE, u.COST_CENTER, u.DIVISION," +
                        " u.PASSWORD_THEME, u.NICKNAME, u.MAIDEN_NAME, u.MAIL_CODE, " +
                        " u.COUNTRY, u.BLDG_NUM, u.STREET_DIRECTION, u.SUITE,  " +
                        " u.ADDRESS1, u.ADDRESS2, u.ADDRESS3, u.ADDRESS4, u.ADDRESS5, u.ADDRESS6, u.ADDRESS7," +
                        " u.CITY, u.STATE, u.POSTAL_CD, u.EMAIL_ADDRESS, u.ALTERNATE_ID, u.USER_OWNER_ID, u.DATE_PASSWORD_CHANGED, u.DATE_CHALLENGE_RESP_CHANGED,"
                        +
                        " u.PHONE_NBR, u.PHONE_EXT, u.AREA_CD, u.COUNTRY_CD, u.CLASSIFICATION, u.SHOW_IN_SEARCH, u.DEL_ADMIN,u.SYSTEM_FLAG "
                        +
                        " from 	USERS u ";

        // MySQL's optimizer has a hard time with the large number of outer joins
        // changing outer joins to inner-joins has a big impact on performance

        join.append("   JOIN LOGIN lg ON ( lg.USER_ID = u.USER_ID) ");
        join.append("   LEFT JOIN USER_ROLE urv on (u.USER_ID = urv.USER_ID)");


        StringBuilder where = new StringBuilder();
        where.append(" (u.SYSTEM_FLAG is null or u.SYSTEM_FLAG<>:systemFlag) ");
        if (search.getShowInSearch() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.SHOW_IN_SEARCH = :showInSearch ");
            showInSearch = true;
        }


        if (search.getUserId() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.USER_ID = :userId ");
            userId = true;
        }

        if (search.getUserTypeInd() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.USER_TYPE_IND = :userTypeInd ");
            userTypeInd = true;
        }

        if (search.getClassification() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.CLASSIFICATION = :classification ");
            classification = true;
        }

        if (search.getLocationCd() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.LOCATION_CD = :locationCd ");
            locationCd = true;
        }

        if (search.getFirstName() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.FIRST_NAME like :firstName ");
            firstName = true;
        }
        if (search.getLastName() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.LAST_NAME like :lastName ");
            lastName = true;
        }
        if (search.getNickName() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.NICKNAME like :nickName ");
            nickName = true;
        }

        if (search.getStatus() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.STATUS = :status ");
            status = true;
        }

        if (search.getSecondaryStatus() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.SECONDARY_STATUS = :secondaryStatus ");
            secondaryStatus = true;
        }

        if (search.getStartDate() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.START_DATE = :startDate ");
            startDate = true;
        }
        if (search.getLastDate() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.LAST_DATE = :lastDate ");
            lastDate = true;
        }
        if (search.getDateOfBirth() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.BIRTHDATE = :dateOfBirth ");
            dateOfBirth = true;
        }


        if (search.getZipCode() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.POSTAL_CD = :zipCode ");
            zipCode = true;
        }

        if (search.getDeptCd() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DEPT_CD = :deptCd ");
            deptCd = true;
        }
        if (search.getDivision() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.DIVISION = :division ");
            division = true;
        }

        if (search.getEmployeeId() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.EMPLOYEE_ID = :employeeId ");
            employeeId = true;
        }
        if (search.getOrgId() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.COMPANY_ID = :orgId ");
            orgId = true;
        }

        if (search.getOrgName() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" c.COMPANY_NAME like :orgName ");
            join.append("  JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) ");
            orgName = true;
        } else {

            join.append("   LEFT JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) ");

        }


        if (search.getPhoneAreaCd() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" p.AREA_CD = :phoneAreaCd ");
            phoneAreaCd = true;
        }
        if (search.getPhoneNbr() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" p.PHONE_NBR = :phoneNbr ");
            phoneNbr = true;
        }

        if (phoneNbr || phoneAreaCd) {
            join.append("   JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) ");
        }else {

            join.append("   LEFT JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) ");


        }

        if (search.getEmailAddress() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            //where.append(" u.EMAIL_ADDRESS = :emailAddress ");
            where.append(" (em.EMAIL_ADDRESS LIKE :emailAddress  OR u.EMAIL_ADDRESS LIKE :emailAddress) ");
            join.append("   JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID)");
            emailAddress = true;
        } else {

            join.append("   LEFT JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID)");

        }
        if (!search.getGroupIdList().isEmpty()) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" g.GRP_ID in (:groupList) ");
            join.append("   JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) ");
            groupId = true;
        }else {
            join.append("   LEFT JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) ");
        }

        if (!search.getRoleIdList().isEmpty()) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" urv.ROLE_ID in (:roleList) ");
           // where.append(" and urv.SERVICE_ID = :domainId ");
            roleId = true;
        }

        /* org list */
        if (!search.getOrgIdList().isEmpty()) {
            if (where.length() > 0 ) {
             where.append(" and ");
            }
            where.append(" u.COMPANY_ID in (:orgList)  ");
            bOrgIdList = true;
        }

        /* dept list */

        if (!search.getDeptIdList().isEmpty()) {
            if (where.length() > 0 ) {
             where.append(" and ");
            }
            where.append(" u.DEPT_CD in (:deptList)  ");
            bDeptIdList = true;
        }

        /* division list  */


        if (!search.getDivisionIdList().isEmpty()) {
            if (where.length() > 0 ) {
             where.append(" and ");
            }
            where.append(" u.DIVISION in (:divisionList)  ");
            bDivIdList = true;
        }


        /* attribute list */

        List<String> nameList = new ArrayList<String>() ;
        List<String> valueList = new ArrayList<String>() ;

        if (!search.getAttributeList().isEmpty()) {
            // create a list for each set of values

            log.debug("Building query parameters for attributes");

            for ( SearchAttribute atr  : search.getAttributeList()) {
                if (atr.getAttributeName() != null) {
                 nameList.add(atr.getAttributeName());
                }
                if (atr.getAttributeValue() != null) {
                    valueList.add(atr.getAttributeValue());
                }
            }

            if (where.length() > 0 ) {
             where.append(" and ");
            }
            if (nameList.size() > 0)  {
                where.append(" ua.NAME in (:nameList)  ");
            }

            if (where.length() > 0 ) {
             where.append(" and ");
            }
            if (nameList.size() > 0)  {
                where.append(" ua.VALUE in (:valueList)  ");
            }
            bAttrIdList = true;

        }

        if (bAttrIdList) {

            join.append(" JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID)" );

        }else {

            join.append(" LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID)" ) ;

        }


        /* Login  */
        if (search.getPrincipal() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" lg.LOGIN like :principal ");
            principal = true;
        }
        if (search.getDomainId() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" lg.SERVICE_ID = :domainId ");
            domainId = true;
        }


        if (search.getLoggedIn() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            if (search.getLoggedIn().equalsIgnoreCase("Yes")) {
                where.append(" lg.LAST_LOGIN IS NOT NULL");
            } else {
                where.append(" lg.LAST_LOGIN IS NULL");
            }

        }


        /* User Attributes fields */
        if (search.getAttributeName() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" ua.NAME = :attributeName ");
            attributeName = true;
        }
        if (search.getAttributeValue() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" ua.VALUE like :attributeValue ");
            attributeValue = true;
        }
        if (search.getAttributeElementId() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" ua.METADATA_ID = :elementId ");
            metadataElementId = true;
        }

        if (where.length() > 0) {
            select = select + join.toString() + " WHERE " + where.toString();
        }

        select = select + "  ORDER BY u.LAST_NAME, u.FIRST_NAME";


        log.debug("search select: " + select);


        Session session = getSession();

        SQLQuery qry = session.createSQLQuery(select);
        qry.addEntity(UserEntity.class);
        qry.setString("systemFlag", "1");
        if (userId) {
            qry.setString("userId", search.getUserId());
        }
        if (firstName) {
            qry.setString("firstName", search.getFirstName());
        }
        if (lastName) {
            qry.setString("lastName", search.getLastName());
        }
        if (nickName) {
            qry.setString("nickName", search.getNickName());
        }
        if (status) {
            qry.setString("status", search.getStatus());
        }
        if (secondaryStatus) {
            qry.setString("secondaryStatus", search.getSecondaryStatus());
        }

        if (startDate) {
            qry.setDate("startDate", search.getStartDate());
        }
        if (lastDate) {
            qry.setDate("lastDate", search.getLastDate());
        }
        if (dateOfBirth) {
            qry.setDate("dateOfBirth", search.getDateOfBirth());
        }
        if (zipCode) {
            qry.setString("zipCode", search.getZipCode());
        }

        if (deptCd) {
            qry.setString("deptCd", search.getDeptCd());
        }

        if (locationCd) {
            qry.setString("locationCd", search.getLocationCd());
        }

        if (division) {
            qry.setString("division", search.getDivision());
        }

        if (employeeId) {
            qry.setString("employeeId", search.getEmployeeId());
        }
        if (orgId) {
            qry.setString("orgId", search.getOrgId());
        }
        if (orgName) {
            qry.setString("orgName", search.getOrgName());
        }
        if (phoneAreaCd) {
            qry.setString("phoneAreaCd", search.getPhoneAreaCd());
        }
        if (phoneNbr) {
            qry.setString("phoneNbr", search.getPhoneNbr());
        }
        if (emailAddress) {
            qry.setString("emailAddress", search.getEmailAddress());
        }
        if (principal) {
            qry.setString("principal", search.getPrincipal());
        }
        if (domainId) {
            qry.setString("domainId", search.getDomainId());
        }
        if (attributeName) {
            qry.setString("attributeName", search.getAttributeName());
        }
        if (attributeValue) {
            qry.setString("attributeValue", search.getAttributeValue());
        }
        if (metadataElementId) {
            qry.setString("elementId", search.getAttributeElementId());
        }
        if (showInSearch) {
            qry.setInteger("showInSearch", search.getShowInSearch());
        }
        if (groupId) {
            qry.setParameterList("groupList", search.getGroupIdList());
            //qry.setString("groupId", search.getGroupId());
        }
        if (roleId) {
            qry.setParameterList("roleList", search.getRoleIdList());
            //qry.setString("role", search.getRoleId());
        }
        if (classification) {
            qry.setString("classification", search.getClassification());
        }
        if (userTypeInd) {
            qry.setString("userTypeInd", search.getUserTypeInd());
        }

        if (bOrgIdList) {
            qry.setParameterList("orgList", search.getOrgIdList());

        }
        if (bDeptIdList) {
            qry.setParameterList("deptList", search.getDeptIdList() );

        }

        if (bDivIdList) {
            qry.setParameterList("divisionList", search.getDivisionIdList());

        }

        if (bAttrIdList)  {
           qry.setParameterList("nameList", nameList);
           qry.setParameterList("valueList", valueList);

        }


        if (search.getMaxResultSize() != null && search.getMaxResultSize().intValue() > 0) {
            qry.setFetchSize(search.getMaxResultSize().intValue());
            qry.setMaxResults(search.getMaxResultSize().intValue());
        } else {
//            qry.setFetchSize(this.maxResultSetSize);
//            qry.setMaxResults(this.maxResultSetSize);
        }
        try {
            List<UserEntity> result = (List<UserEntity>) qry.list();
            if (result == null || result.size() == 0) {
                log.debug("search result is null");
                return null;
            }
            log.debug("search resultset size=" + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search) {
        final Criteria criteria = getCriteria();
            // check systemFlag
        Disjunction disjunction  = Restrictions.disjunction();
        disjunction.add(Restrictions.isNotNull("systemFlag")).add(Restrictions.ne("systemFlag", "1"));
        criteria.add(disjunction);
        if(StringUtils.isNotEmpty(search.getRole())){
            criteria.createAlias("userRoles", "urv");
            criteria.add(Restrictions.eq("urv.roleId", search.getRole()));
        }

        if (search.getDelAdmin() == 1) {
            criteria.add(Restrictions.eq("delAdmin", search.getDelAdmin()));
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
         return (List<String>) getCriteria().setProjection(Projections.property(getPKfieldName()))
                .setFirstResult(startPos).setMaxResults(count).list();
    }

    @Override
    public Long getUserCount() {
        return (Long)getCriteria().setProjection(Projections.count(getPKfieldName())).uniqueResult();
    }

    @Override
    public List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size) {
        Criteria criteria = getExampleCriteria(searchBean);
        if(startAt > -1) {
            criteria.setFirstResult(startAt);
        }
        if(size > -1) {
            criteria.setMaxResults(size);
        }
        return (List<UserEntity>)criteria.list();
    }

    @Override
    public Long getUserCount(UserSearchBean searchBean) {
        return ((Number)getExampleCriteria(searchBean).setProjection(rowCount())
                .uniqueResult()).longValue();
    }
    private Criterion getStringCriterion(String fieldName, String value){
        return getStringCriterion(fieldName, value, false);
    }
    private Criterion getStringCriterion(String fieldName, String value, boolean caseInsensitive){
        Criterion criterion=null;
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
                criterion = (caseInsensitive) ? Restrictions.eq(fieldName, value).ignoreCase():Restrictions.eq(fieldName, value);
            }
        }
        return criterion;
    }

    private Criteria getExampleCriteria(UserSearchBean searchBean) {
        boolean ORACLE_INSENSITIVE ="ORACLE_INSENSITIVE".equalsIgnoreCase(dbType);

        final Criteria criteria = getCriteria();
        if(StringUtils.isNotBlank(searchBean.getKey())) {
            criteria.add(Restrictions.eq(getPKfieldName(), searchBean.getKey()));
        } else {
            // check systemFlag
            Disjunction disjunction  = Restrictions.disjunction();
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
            if (searchBean.getCreateDate()!=null) {
                criteria.add(Restrictions.eq("createDate", searchBean.getCreateDate()));
            }

            if (searchBean.getStartDate()!=null) {
                criteria.add(Restrictions.eq("startDate", searchBean.getStartDate()));
            }
            if (searchBean.getLastDate()!=null) {
                criteria.add(Restrictions.eq("lastDate", searchBean.getLastDate()));
            }
            if (searchBean.getDateOfBirth()!=null) {
                criteria.add(Restrictions.eq("birthdate", searchBean.getDateOfBirth()));
            }
            if(StringUtils.isNotEmpty(searchBean.getUserTypeInd())) {
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
            if(StringUtils.isNotEmpty(searchBean.getOrganizationId())) {
                criteria.add(Restrictions.eq("companyId", searchBean.getOrganizationId()));
            }
            if(searchBean.getDeptIdList()!=null && !searchBean.getDeptIdList().isEmpty()){
                criteria.add(Restrictions.in("deptCd", searchBean.getDeptIdList()));
            }
            if(StringUtils.isNotEmpty(searchBean.getPhoneAreaCd()) || StringUtils.isNotEmpty(searchBean.getPhoneNbr())) {
                if(StringUtils.isNotEmpty(searchBean.getPhoneAreaCd())) {
                    criteria.add(Restrictions.eq("p.areaCd", searchBean.getPhoneAreaCd()));
                }
                if(StringUtils.isNotEmpty(searchBean.getPhoneNbr())) {
                    criteria.add(Restrictions.eq("p.phoneNbr", searchBean.getPhoneNbr()));
                }
                criteria.createAlias("phones", "p");
            }
            if(StringUtils.isNotEmpty(searchBean.getEmailAddress())) {
                criteria.createAlias("emailAddresses", "em");
                disjunction  = Restrictions.disjunction();
                disjunction.add(getStringCriterion("em.emailAddress", searchBean.getEmailAddress(),ORACLE_INSENSITIVE)).add(getStringCriterion("email", searchBean.getEmailAddress(),ORACLE_INSENSITIVE));
                criteria.add(disjunction);
            }
            if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())){
                 criteria.createAlias("userGroups", "g");
                 criteria.add(Restrictions.in("g.grpId", searchBean.getGroupIdSet()));
            }
            if(searchBean.getDivisionIdList()!=null && !searchBean.getDivisionIdList().isEmpty()){
                criteria.add(Restrictions.in("division", searchBean.getDivisionIdList()));
            }
            if(StringUtils.isNotEmpty(searchBean.getEmployeeId())) {
                criteria.add(Restrictions.eq("employeeId", searchBean.getEmployeeId()));
            }
            if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
                criteria.createAlias("userRoles", "urv");
                criteria.add(Restrictions.in("urv.roleId", searchBean.getRoleIdSet()));
            }

            if (StringUtils.isNotEmpty(searchBean.getAttributeName())
                    || StringUtils.isNotEmpty(searchBean.getAttributeValue())
                    || StringUtils.isNotEmpty(searchBean.getAttributeElementId())
                    || (searchBean.getAttributeList()!=null && !searchBean.getAttributeList().isEmpty())) {
                criteria.createAlias("userAttributes", "ua");
                if(searchBean.getAttributeList()!=null && !searchBean.getAttributeList().isEmpty()){
                    List<String> nameList = new ArrayList<String>() ;
                    List<String> valueList = new ArrayList<String>() ;
                    for ( SearchAttribute atr  : searchBean.getAttributeList()) {
                        if (atr.getAttributeName() != null) {
                            nameList.add(atr.getAttributeName());
                        }
                        if (atr.getAttributeValue() != null) {
                            valueList.add(atr.getAttributeValue());
                        }
                    }

                    if (nameList.size() > 0)  {
                        criteria.add(Restrictions.in("ua.name", nameList));
                    }
                    if (valueList.size() > 0)  {
                        criteria.add(Restrictions.in("ua.value", valueList));
                    }
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeName())) {
                    criteria.add( (ORACLE_INSENSITIVE) ?  Restrictions.eq("ua.name", searchBean.getAttributeName()).ignoreCase() : Restrictions.eq("ua.name", searchBean.getAttributeName()));
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeValue())) {
                    criteria.add(getStringCriterion("ua.value", searchBean.getAttributeValue(),ORACLE_INSENSITIVE));
                }
                if (StringUtils.isNotEmpty(searchBean.getAttributeElementId())) {
                    criteria.add(Restrictions.eq("ua.metadataElementId", searchBean.getAttributeElementId()));
                }
            }
            /* Login  */
            if(StringUtils.isNotEmpty(searchBean.getPrincipal()) || StringUtils.isNotEmpty(searchBean.getDomainId()) || StringUtils.isNotEmpty(searchBean.getLoggedIn())){
                criteria.createAlias("principalList", "lg");
                if(StringUtils.isNotEmpty(searchBean.getPrincipal())) {
                    criteria.add(getStringCriterion("lg.login", searchBean.getPrincipal(),ORACLE_INSENSITIVE));
                }
                if(StringUtils.isNotEmpty(searchBean.getDomainId())) {
                    criteria.add(Restrictions.eq("lg.domainId", searchBean.getDomainId()));
                }
                if(StringUtils.isNotEmpty(searchBean.getLoggedIn())) {
                    if("YES".equalsIgnoreCase(searchBean.getLoggedIn())) {
                        criteria.add(Restrictions.isNotNull("lg.lastLogin"));
                    } else{
                        criteria.add(Restrictions.isNull("lg.lastLogin"));
                    }
                }
            }
            criteria.addOrder(Order.asc("lastName")).addOrder(Order.asc("firstName"));
        }
        return criteria;
    }

    public String getDbType() {
        return dbType;
    }


    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    
    private Criteria getUsersForResourceCriteria(final String resourceId) {
    	return getCriteria()
               .createAlias("resourceUsers", "ru")
               .add(Restrictions.eq("ru.resourceId", resourceId));
    }

	@Override
	public List<UserEntity> getUsersForResource(final String resourceId, final int from, final int size) {
		final Criteria criteria = getUsersForResourceCriteria(resourceId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}

	@Override
	public int getNumOfUsersForResource(final String resourceId) {
		final Criteria criteria = getUsersForResourceCriteria(resourceId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}
	
	private Criteria getUsersForGroupCriteria(final String groupId) {
		return getCriteria()
	               .createAlias("userGroups", "ug")
	               .add(Restrictions.eq("ug.grpId", groupId));
	}

	@Override
	public List<UserEntity> getUsersForGroup(final String groupId, final int from, final int size) {
		final Criteria criteria = getUsersForGroupCriteria(groupId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}

	@Override
	public int getNumOfUsersForGroup(String groupId) {
		final Criteria criteria = getUsersForGroupCriteria(groupId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}
	
	private Criteria getUsersForRoleCriteria(final String roleId) {
		return getCriteria()
	               .createAlias("userRoles", "ur")
	               .add(Restrictions.eq("ur.roleId", roleId));
	}

	@Override
	public List<UserEntity> getUsersForRole(final String roleId, final int from, final int size) {
		final Criteria criteria = getUsersForRoleCriteria(roleId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}

	@Override
	public int getNumOfUsersForRole(final String roleId) {
		final Criteria criteria = getUsersForRoleCriteria(roleId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public void disassociateUsersFromOrganization(String organizationId) {
		final String queryString = String.format("UPDATE %s u SET u.organization = NULL WHERE u.organization.orgId = :organizationId", domainClass.getSimpleName());
		final Query query = getSession().createQuery(queryString);
		query.setParameter("organizationId", organizationId);
		query.executeUpdate();
	}
}
