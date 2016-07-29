package org.openiam.authmanager.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationGroup;
import org.openiam.authmanager.common.model.AuthorizationOrganization;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.common.model.AuthorizationRole;
import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;
import org.openiam.authmanager.dao.MembershipDAO;
import org.openiam.core.dao.AbstractJDBCDao;
import org.openiam.membership.MembershipDTO;
import org.openiam.membership.MembershipRightDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository("jdbcMembershipDAO")
public class JdbcMembershipDAO extends AbstractJDBCDao implements MembershipDAO {

    private static final Log log = LogFactory.getLog(JdbcMembershipDAO.class);

    private static final RowMapper<MembershipDTO> memberMapper = new MembershipDTOMapper();
    private static final RowMapper<MembershipRightDTO> rightMapper = new MembershipRightDTOMapper();
    private static final UserRowMapper urm = new UserRowMapper();

    private static final String DATE_CONDITION = "(START_DATE IS NULL AND END_DATE IS NULL) OR "
            + "(START_DATE <= ? AND END_DATE IS NULL) OR "
            + "(START_DATE IS NULL AND END_DATE >= ?) OR "
            + "(START_DATE <= ? AND END_DATE >= ?)";
    private static final String DATE_CONDITION_PREFIXED = "(%s.START_DATE IS NULL AND %s.END_DATE IS NULL) OR "
            + "(%s.START_DATE <= ? AND %s.END_DATE IS NULL) OR "
            + "(%s.START_DATE IS NULL AND %s.END_DATE >= ?) OR "
            + "(%s.START_DATE <= ? AND %s.END_DATE >= ?)";

    private static final String GET_MEMBERSHIP_ALL = "SELECT %s AS MEMBER_ENTITY_ID, %s AS ENTITY_ID, MEMBERSHIP_ID AS MEMBERSHIP_ID, START_DATE AS START_DATE, END_DATE AS END_DATE FROM %s.%s";
    private static final String GET_MEMBERSHIP_RANGE = "SELECT %s AS MEMBER_ENTITY_ID, %s AS ENTITY_ID, MEMBERSHIP_ID AS MEMBERSHIP_ID, START_DATE AS START_DATE, END_DATE AS END_DATE FROM %s.%s WHERE " + DATE_CONDITION;
    private static final String GET_RIGHTS = "SELECT MEMBERSHIP_ID AS MEMBERSHIP_ID, ACCESS_RIGHT_ID AS ACCESS_RIGHT_ID FROM %s.%s";
    private static final String GET_ENTITY = "SELECT %S AS ID, %s AS NAME, %s AS DESCRIPTION, %s AS STATUS, %s AS MANAGED_SYS_ID, %s AS TYPE_ID  FROM %s.%s";

    private static String getDateCondition(final String prefix) {
        return String.format(DATE_CONDITION_PREFIXED, prefix, prefix, prefix, prefix, prefix, prefix, prefix, prefix);
    }

    private String GET_FULLY_POPULATED_USER_RS_RANGE = "SELECT " +
            "	l.USER_ID AS ID, " +
            "	gm.GRP_ID AS GROUP_ID, " +
            "	gm.START_DATE AS GROUP_START_DATE, " +
            "	gm.END_DATE AS GROUP_END_DATE, " +
            " rm.ROLE_ID AS ROLE_ID, " +
            " rm.START_DATE AS ROLE_START_DATE, " +
            " rm.END_DATE AS ROLE_END_DATE, " +
            "	resm.RESOURCE_ID AS RESOURCE_ID, " +
            "	resm.START_DATE AS RESOURCE_START_DATE, " +
            "	resm.END_DATE AS RESOURCE_END_DATE, " +
            "	orgm.COMPANY_ID AS COMPANY_ID, " +
            "	orgm.START_DATE AS COMPANY_START_DATE, " +
            "	orgm.END_DATE AS COMPANY_END_DATE, " +
            "	gmr.ACCESS_RIGHT_ID AS GROUP_ID_RIGHT, " +
            "	rmr.ACCESS_RIGHT_ID AS ROLE_ID_RIGHT, " +
            "	resmr.ACCESS_RIGHT_ID AS RESOURCE_ID_RIGHT, " +
            "	orgmr.ACCESS_RIGHT_ID AS COMPANY_ID_RIGHT " +
            "		FROM " +
            "	%s.LOGIN l " +
            "	LEFT JOIN %s.USER_GRP gm " +
            "		ON l.USER_ID=gm.USER_ID " +
            "		AND (" + getDateCondition("gm") + ")" +
            "	LEFT JOIN %s.USER_ROLE rm " +
            "		ON l.USER_ID=rm.USER_ID " +
            "		AND (" + getDateCondition("rm") + ")" +
            "	LEFT JOIN %s.RESOURCE_USER resm " +
            "		ON l.USER_ID=resm.USER_ID " +
            "		AND (" + getDateCondition("resm") + ")" +
            "	LEFT JOIN %s.USER_AFFILIATION orgm " +
            "		ON l.USER_ID=orgm.USER_ID " +
            "		AND (" + getDateCondition("orgm") + ")" +
            "	LEFT JOIN %s.USER_GRP_MEMBERSHIP_RIGHTS gmr " +
            "		ON gm.MEMBERSHIP_ID=gmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_ROLE_MEMBERSHIP_RIGHTS rmr " +
            "		ON rm.MEMBERSHIP_ID=rmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_RES_MEMBERSHIP_RIGHTS resmr " +
            "		ON resm.MEMBERSHIP_ID=resmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_AFFILIATION_RIGHTS orgmr " +
            "		ON orgm.MEMBERSHIP_ID=orgmr.MEMBERSHIP_ID " +
            "	WHERE l.USER_ID=?;";

    private String GET_FULLY_POPULATED_USER_RS_LIST = "SELECT " +
            "	l.USER_ID AS ID, " +
            "	gm.GRP_ID AS GROUP_ID, " +
            " rm.ROLE_ID AS ROLE_ID, " +
            "	resm.RESOURCE_ID AS RESOURCE_ID, " +
            "	orgm.COMPANY_ID AS COMPANY_ID, " +
            "	gmr.ACCESS_RIGHT_ID AS GROUP_ID_RIGHT, " +
            "	rmr.ACCESS_RIGHT_ID AS ROLE_ID_RIGHT, " +
            "	resmr.ACCESS_RIGHT_ID AS RESOURCE_ID_RIGHT, " +
            "	orgmr.ACCESS_RIGHT_ID AS COMPANY_ID_RIGHT " +
            "		FROM " +
            "	%s.LOGIN l " +
            "	LEFT JOIN %s.USER_GRP gm " +
            "		ON l.USER_ID=gm.USER_ID " +
            "	LEFT JOIN %s.USER_ROLE rm " +
            "		ON l.USER_ID=rm.USER_ID " +
            "	LEFT JOIN %s.RESOURCE_USER resm " +
            "		ON l.USER_ID=resm.USER_ID " +
            "	LEFT JOIN %s.USER_AFFILIATION orgm " +
            "		ON l.USER_ID=orgm.USER_ID " +
            "	LEFT JOIN %s.USER_GRP_MEMBERSHIP_RIGHTS gmr " +
            "		ON gm.MEMBERSHIP_ID=gmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_ROLE_MEMBERSHIP_RIGHTS rmr " +
            "		ON rm.MEMBERSHIP_ID=rmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_RES_MEMBERSHIP_RIGHTS resmr " +
            "		ON resm.MEMBERSHIP_ID=resmr.MEMBERSHIP_ID " +
            "	LEFT JOIN %s.USER_AFFILIATION_RIGHTS orgmr " +
            "		ON orgm.MEMBERSHIP_ID=orgmr.MEMBERSHIP_ID " +
            "	WHERE l.USER_ID=?;";

    private String GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT = "SELECT r.USER_ID FROM %s.RESOURCE_USER r JOIN %s.USER_RES_MEMBERSHIP_RIGHTS rm ON r.MEMBERSHIP_ID=rm.MEMBERSHIP_ID WHERE r.RESOURCE_ID=? AND rm.ACCESS_RIGHT_ID=?;";
    private String GET_USER_IDS_FOR_RESOURCE = "SELECT USER_ID FROM %s.RESOURCE_USER WHERE RESOURCE_ID=?";

    private String GET_USER_IDS_FOR_GROUP_WITH_RIGHT = "SELECT r.USER_ID FROM %s.USER_GRP r JOIN %s.USER_GRP_MEMBERSHIP_RIGHTS rm ON r.MEMBERSHIP_ID=rm.MEMBERSHIP_ID WHERE r.GRP_ID=? AND rm.ACCESS_RIGHT_ID=?;";
    private String GET_USER_IDS_FOR_GROUP = "SELECT USER_ID FROM %s.USER_GRP WHERE GRP_ID=?";

    private String GET_USERS = "SELECT USER_ID AS ID FROM %s.LOGIN WHERE LAST_LOGIN >= ?";
    private String GET_RESOURCES = "SELECT RESOURCE_ID AS ID, NAME AS NAME, DESCRIPTION AS DESCRIPTION, RESOURCE_TYPE_ID AS RESOURCE_TYPE_ID, RISK AS RISK, COORELATED_NAME AS COORELATED_NAME, IS_PUBLIC AS IS_PUBLIC FROM %s.RES";
    private String GET_GROUPS;
    private String GET_ROLES;
    private String GET_ORGS = "SELECT COMPANY_ID AS ID, COMPANY_NAME AS NAME, DESCRIPTION AS DESCRIPTION, STATUS AS STATUS FROM %s.COMPANY";

    private String USER_ROLE_XREFS_ALL;
    private String USER_GRP_XREFS_ALL;
    private String USER_ORG_XREFS_ALL;
    private String USER_RES_XREFS_ALL;

    private String ORG_ORG_XREFS_ALL;
    private String ORG_ROLE_XREFS_ALL;
    private String ORG_GRP_XREFS_ALL;
    private String ORG_RES_XREFS_ALL;

    private String ROLE_ROLE_XREFS_ALL;
    private String ROLE_GRP_XREFS_ALL;
    private String ROLE_RES_XREFS_ALL;

    private String GRP_GRP_XREFS_ALL;
    private String GRP_RES_XREFS_ALL;

    private String RES_RES_XREFS_ALL;

    private String USER_ROLE_XREFS_RANGE;
    private String USER_GRP_XREFS_RANGE;
    private String USER_ORG_XREFS_RANGE;
    private String USER_RES_XREFS_RANGE;

    private String ORG_ORG_XREFS_RANGE;
    private String ORG_ROLE_XREFS_RANGE;
    private String ORG_GRP_XREFS_RANGE;
    private String ORG_RES_XREFS_RANGE;

    private String ROLE_ROLE_XREFS_RANGE;
    private String ROLE_GRP_XREFS_RANGE;
    private String ROLE_RES_XREFS_RANGE;

    private String GRP_GRP_XREFS_RANGE;
    private String GRP_RES_XREFS_RANGE;

    private String RES_RES_XREFS_RANGE;

    private String USER_ROLE_XREFS_RIGHTS_ALL;
    private String USER_GRP_XREFS_RIGHTS_ALL;
    private String USER_ORG_XREFS_RIGHTS_ALL;
    private String USER_RES_XREFS_RIGHTS_ALL;

    private String ORG_ORG_XREFS_RIGHTS_ALL;
    private String ORG_ROLE_XREFS_RIGHTS_ALL;
    private String ORG_GRP_XREFS_RIGHTS_ALL;
    private String ORG_RES_XREFS_RIGHTS_ALL;

    private String ROLE_ROLE_XREFS_RIGHTS_ALL;
    private String ROLE_GRP_XREFS_RIGHTS_ALL;
    private String ROLE_RES_XREFS_RIGHTS_ALL;

    private String GRP_GRP_XREFS_RIGHTS_ALL;
    private String GRP_RES_XREFS_RIGHTS_ALL;

    private String RES_RES_XREFS_RIGHTS_ALL;

    private String getMembershipAllSQL(final String memberName, final String entity, final String tableName) {
        return String.format(GET_MEMBERSHIP_ALL, memberName, entity, getSchemaName(), tableName);
    }

    private String getMembershipRangeSQL(final String memberName, final String entity, final String tableName) {
        return String.format(GET_MEMBERSHIP_RANGE, memberName, entity, getSchemaName(), tableName);
    }

    private String getRightSQL(final String tableName) {
        return String.format(GET_RIGHTS, getSchemaName(), tableName);
    }

    private String getEntitySQL(final String idName, final String name, final String description, final String status, final String managedSysId, final String typeId, final String tableName) {
        return String.format(GET_ENTITY, idName, name, description, status, managedSysId, typeId, getSchemaName(), tableName);
    }

    @Override
    protected void initSqlStatements() {
        final String schemaName = getSchemaName();
        GET_FULLY_POPULATED_USER_RS_RANGE = String.format(GET_FULLY_POPULATED_USER_RS_RANGE, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName);
        GET_FULLY_POPULATED_USER_RS_LIST = String.format(GET_FULLY_POPULATED_USER_RS_LIST, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName, schemaName);
        GET_USERS = String.format(GET_USERS, schemaName);
        GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT = String.format(GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT, schemaName, schemaName);
        GET_USER_IDS_FOR_RESOURCE = String.format(GET_USER_IDS_FOR_RESOURCE, schemaName);
        GET_USER_IDS_FOR_GROUP_WITH_RIGHT = String.format(GET_USER_IDS_FOR_GROUP_WITH_RIGHT, schemaName, schemaName);
        GET_USER_IDS_FOR_GROUP = String.format(GET_USER_IDS_FOR_GROUP, schemaName);
        GET_RESOURCES = String.format(GET_RESOURCES, schemaName);
        GET_ORGS = String.format(GET_ORGS, schemaName);
        GET_ROLES = getEntitySQL("ROLE_ID", "ROLE_NAME", "DESCRIPTION", "STATUS", "MANAGED_SYS_ID", "TYPE_ID", "ROLE");
        GET_GROUPS = getEntitySQL("GRP_ID", "GRP_NAME", "GROUP_DESC", "STATUS", "MANAGED_SYS_ID", "TYPE_ID", "GRP");

        USER_ROLE_XREFS_ALL = getMembershipAllSQL("USER_ID", "ROLE_ID", "USER_ROLE");
        USER_GRP_XREFS_ALL = getMembershipAllSQL("USER_ID", "GRP_ID", "USER_GRP");
        USER_ORG_XREFS_ALL = getMembershipAllSQL("USER_ID", "COMPANY_ID", "USER_AFFILIATION");
        USER_RES_XREFS_ALL = getMembershipAllSQL("USER_ID", "RESOURCE_ID", "RESOURCE_USER");

        ORG_ORG_XREFS_ALL = getMembershipAllSQL("MEMBER_COMPANY_ID", "COMPANY_ID", "COMPANY_TO_COMPANY_MEMBERSHIP");
        ORG_ROLE_XREFS_ALL = getMembershipAllSQL("ROLE_ID", "COMPANY_ID", "ROLE_ORG_MEMBERSHIP");
        ORG_GRP_XREFS_ALL = getMembershipAllSQL("GRP_ID", "COMPANY_ID", "GROUP_ORGANIZATION");
        ORG_RES_XREFS_ALL = getMembershipAllSQL("RESOURCE_ID", "COMPANY_ID", "RES_ORG_MEMBERSHIP");

        ROLE_ROLE_XREFS_ALL = getMembershipAllSQL("MEMBER_ROLE_ID", "ROLE_ID", "role_to_role_membership");
        ROLE_GRP_XREFS_ALL = getMembershipAllSQL("GRP_ID", "ROLE_ID", "GRP_ROLE");
        ROLE_RES_XREFS_ALL = getMembershipAllSQL("RESOURCE_ID", "ROLE_ID", "RESOURCE_ROLE");

        GRP_GRP_XREFS_ALL = getMembershipAllSQL("MEMBER_GROUP_ID", "GROUP_ID", "grp_to_grp_membership");
        GRP_RES_XREFS_ALL = getMembershipAllSQL("RESOURCE_ID", "GRP_ID", "RESOURCE_GROUP");

        RES_RES_XREFS_ALL = getMembershipAllSQL("MEMBER_RESOURCE_ID", "RESOURCE_ID", "res_to_res_membership");

        //ranges
        USER_ROLE_XREFS_RANGE = getMembershipRangeSQL("USER_ID", "ROLE_ID", "USER_ROLE");
        USER_GRP_XREFS_RANGE = getMembershipRangeSQL("USER_ID", "GRP_ID", "USER_GRP");
        USER_ORG_XREFS_RANGE = getMembershipRangeSQL("USER_ID", "COMPANY_ID", "USER_AFFILIATION");
        USER_RES_XREFS_RANGE = getMembershipRangeSQL("USER_ID", "RESOURCE_ID", "RESOURCE_USER");

        ORG_ORG_XREFS_RANGE = getMembershipRangeSQL("MEMBER_COMPANY_ID", "COMPANY_ID", "COMPANY_TO_COMPANY_MEMBERSHIP");
        ORG_ROLE_XREFS_RANGE = getMembershipRangeSQL("ROLE_ID", "COMPANY_ID", "ROLE_ORG_MEMBERSHIP");
        ORG_GRP_XREFS_RANGE = getMembershipRangeSQL("GRP_ID", "COMPANY_ID", "GROUP_ORGANIZATION");
        ORG_RES_XREFS_RANGE = getMembershipRangeSQL("RESOURCE_ID", "COMPANY_ID", "RES_ORG_MEMBERSHIP");

        ROLE_ROLE_XREFS_RANGE = getMembershipRangeSQL("MEMBER_ROLE_ID", "ROLE_ID", "role_to_role_membership");
        ROLE_GRP_XREFS_RANGE = getMembershipRangeSQL("GRP_ID", "ROLE_ID", "GRP_ROLE");
        ROLE_RES_XREFS_RANGE = getMembershipRangeSQL("RESOURCE_ID", "ROLE_ID", "RESOURCE_ROLE");

        GRP_GRP_XREFS_RANGE = getMembershipRangeSQL("MEMBER_GROUP_ID", "GROUP_ID", "grp_to_grp_membership");
        GRP_RES_XREFS_RANGE = getMembershipRangeSQL("RESOURCE_ID", "GRP_ID", "RESOURCE_GROUP");

        RES_RES_XREFS_RANGE = getMembershipRangeSQL("MEMBER_RESOURCE_ID", "RESOURCE_ID", "res_to_res_membership");


        USER_ROLE_XREFS_RIGHTS_ALL = getRightSQL("USER_ROLE_MEMBERSHIP_RIGHTS");
        USER_GRP_XREFS_RIGHTS_ALL = getRightSQL("USER_GRP_MEMBERSHIP_RIGHTS");
        USER_ORG_XREFS_RIGHTS_ALL = getRightSQL("USER_AFFILIATION_RIGHTS");
        USER_RES_XREFS_RIGHTS_ALL = getRightSQL("USER_RES_MEMBERSHIP_RIGHTS");

        ORG_ORG_XREFS_RIGHTS_ALL = getRightSQL("ORG_TO_ORG_MEMBERSHIP_RIGHTS");
        ORG_ROLE_XREFS_RIGHTS_ALL = getRightSQL("ROLE_ORG_MEMBERSHIP_RIGHTS");
        ORG_GRP_XREFS_RIGHTS_ALL = getRightSQL("GRP_ORG_MEMBERSHIP_RIGHTS");
        ORG_RES_XREFS_RIGHTS_ALL = getRightSQL("RES_ORG_MEMBERSHIP_RIGHTS");

        ROLE_ROLE_XREFS_RIGHTS_ALL = getRightSQL("ROLE_ROLE_MEMBERSHIP_RIGHTS");
        ROLE_GRP_XREFS_RIGHTS_ALL = getRightSQL("GRP_ROLE_MEMBERSHIP_RIGHTS");
        ROLE_RES_XREFS_RIGHTS_ALL = getRightSQL("RES_ROLE_MEMBERSHIP_RIGHTS");

        GRP_GRP_XREFS_RIGHTS_ALL = getRightSQL("GRP_GRP_MEMBERSHIP_RIGHTS");
        GRP_RES_XREFS_RIGHTS_ALL = getRightSQL("RES_GRP_MEMBERSHIP_RIGHTS");

        RES_RES_XREFS_RIGHTS_ALL = getRightSQL("RES_RES_MEMBERSHIP_RIGHTS");
    }

    @Override
    public List<MembershipRightDTO> getResource2ResourceRights() {
        return getJdbcTemplate().query(RES_RES_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getUser2ResourceRights() {
        return getJdbcTemplate().query(USER_RES_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getGroup2ResourceRights() {
        return getJdbcTemplate().query(GRP_RES_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getGroup2GroupRights() {
        return getJdbcTemplate().query(GRP_GRP_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getUser2GroupRights() {
        return getJdbcTemplate().query(USER_GRP_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getRole2ResourceRights() {
        return getJdbcTemplate().query(ROLE_RES_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getRole2GroupRights() {
        return getJdbcTemplate().query(ROLE_GRP_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getRole2RoleRights() {
        return getJdbcTemplate().query(ROLE_ROLE_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getUser2RoleRights() {
        return getJdbcTemplate().query(USER_ROLE_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getOrg2ResourceRights() {
        return getJdbcTemplate().query(ORG_RES_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getOrg2GroupRights() {
        return getJdbcTemplate().query(ORG_GRP_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getOrg2RoleRights() {
        return getJdbcTemplate().query(ORG_ROLE_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getOrg2OrgRights() {
        return getJdbcTemplate().query(ORG_ORG_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipRightDTO> getUser2OrgRights() {
        return getJdbcTemplate().query(USER_ORG_XREFS_RIGHTS_ALL, rightMapper);
    }

    @Override
    public List<MembershipDTO> getResource2ResourceMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(RES_RES_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(RES_RES_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getUser2ResourceMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(USER_RES_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(USER_RES_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getGroup2ResourceMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(GRP_RES_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(GRP_RES_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getGroup2GroupMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(GRP_GRP_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(GRP_GRP_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getUser2GroupMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(USER_GRP_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(USER_GRP_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getRole2ResourceMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ROLE_RES_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ROLE_RES_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getRole2GroupMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ROLE_GRP_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ROLE_GRP_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getRole2RoleMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ROLE_ROLE_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ROLE_ROLE_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getUser2RoleMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(USER_ROLE_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(USER_ROLE_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getOrg2ResourceMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ORG_RES_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ORG_RES_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getOrg2GroupMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ORG_GRP_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ORG_GRP_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getOrg2RoleMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ORG_ROLE_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ORG_ROLE_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getOrg2OrgMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(ORG_ORG_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(ORG_ORG_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<MembershipDTO> getUser2OrgMembership(final Date date) {
        if (date != null) {
            return getJdbcTemplate().query(USER_ORG_XREFS_RANGE, new Object[]{date, date, date, date}, memberMapper);
        } else {
            return getJdbcTemplate().query(USER_ORG_XREFS_ALL, memberMapper);
        }
    }

    @Override
    public List<AuthorizationOrganization> getOrganizations() {
        return getJdbcTemplate().query(GET_ORGS, new OrgMapper());
    }

    @Override
    public List<AuthorizationGroup> getGroups() {
        return getJdbcTemplate().query(GET_GROUPS, new GroupMapper());
    }

    @Override
    public List<AuthorizationRole> getRoles() {
        return getJdbcTemplate().query(GET_ROLES, new RoleMapper());
    }

    @Override
    public List<AuthorizationResource> getResources() {
        return getJdbcTemplate().query(GET_RESOURCES, new ResourceMapper());
    }


    @Override
    public List<AuthorizationUser> getUsers(final Date date) {
        return getJdbcTemplate().query(GET_USERS, new Object[]{date}, new UserMapper());
    }

    private static final class UserMapper implements RowMapper<AuthorizationUser> {

        @Override
        public AuthorizationUser mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final AuthorizationUser dto = new AuthorizationUser();
            dto.setId(rs.getString("ID"));
            return dto;
        }

    }

    private static final class GroupMapper implements RowMapper<AuthorizationGroup> {

        @Override
        public AuthorizationGroup mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final AuthorizationGroup dto = new AuthorizationGroup();
            dto.setDescription(rs.getString("DESCRIPTION"));
            dto.setId(rs.getString("ID"));
            dto.setName(rs.getString("NAME"));
            dto.setStatus(rs.getString("STATUS"));
            dto.setManagedSysId(rs.getString("MANAGED_SYS_ID"));
            dto.setTypeId(rs.getString("TYPE_ID"));
            return dto;
        }
    }

    @Override
    public InternalAuthroizationUser getUser(String id, final Date date) {
        final String query = (date != null) ? GET_FULLY_POPULATED_USER_RS_RANGE : GET_FULLY_POPULATED_USER_RS_LIST;
        if (log.isDebugEnabled()) {
            log.debug(String.format("Query: " + query));
            log.debug(String.format("Params: " + id));
        }
        if (date != null) {
            final List<Object> args = new ArrayList<Object>(13);
            for (int i = 0; i < 4 * 4; i++) {
                args.add(date);
            }
            args.add(id);
            return getJdbcTemplate().query(GET_FULLY_POPULATED_USER_RS_RANGE, args.toArray(), urm);
        } else {
            return getJdbcTemplate().query(GET_FULLY_POPULATED_USER_RS_LIST, new Object[]{id}, urm);
        }
    }

    private static final class UserRowMapper implements ResultSetExtractor<InternalAuthroizationUser> {

        @Override
        public InternalAuthroizationUser extractData(ResultSet rs)
                throws SQLException, DataAccessException {
            final InternalAuthroizationUser user = new InternalAuthroizationUser();
            ;
            while (rs.next()) {
                final String userId = rs.getString("ID");
                final String groupId = rs.getString("GROUP_ID");
                final String roleId = rs.getString("ROLE_ID");
                final String organizationId = rs.getString("COMPANY_ID");
                final String resourceId = rs.getString("RESOURCE_ID");

                final String groupIdRight = rs.getString("GROUP_ID_RIGHT");
                final String roleIdRight = rs.getString("ROLE_ID_RIGHT");
                final String organizationIdRight = rs.getString("COMPANY_ID_RIGHT");
                final String resourceIdRight = rs.getString("RESOURCE_ID_RIGHT");

                final Date resourceStartDate = getDate(rs.getTimestamp("RESOURCE_START_DATE"));
                final Date resourceEndDate = getDate(rs.getTimestamp("RESOURCE_END_DATE"));

                final Date roleStartDate = getDate(rs.getTimestamp("ROLE_START_DATE"));
                final Date roleEndDate = getDate(rs.getTimestamp("ROLE_END_DATE"));

                final Date groupStartDate = getDate(rs.getTimestamp("GROUP_START_DATE"));
                final Date groupEndDate = getDate(rs.getTimestamp("GROUP_END_DATE"));

                final Date orgStartDate = getDate(rs.getTimestamp("COMPANY_START_DATE"));
                final Date orgEndDate = getDate(rs.getTimestamp("COMPANY_END_DATE"));

                user.setUserId(userId);
                user.addGroupRight(groupId, groupIdRight, groupStartDate, groupEndDate);
                user.addRoleRight(roleId, roleIdRight, roleStartDate, roleEndDate);
                user.addOrganizationRight(organizationId, organizationIdRight, orgStartDate, orgEndDate);
                user.addResourceRight(resourceId, resourceIdRight, resourceStartDate, resourceEndDate);
            }
            return (user.getUserId() != null) ? user : null;
        }
    }

    private static Date getDate(final Timestamp ts) {
        return (ts != null) ? new Date(ts.getTime()) : null;
    }

    private static final class RoleMapper implements RowMapper<AuthorizationRole> {

        @Override
        public AuthorizationRole mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final AuthorizationRole dto = new AuthorizationRole();
            dto.setDescription(rs.getString("DESCRIPTION"));
            dto.setId(rs.getString("ID"));
            dto.setName(rs.getString("NAME"));
            dto.setStatus(rs.getString("STATUS"));
            dto.setManagedSysId(rs.getString("MANAGED_SYS_ID"));
            dto.setTypeId(rs.getString("TYPE_ID"));
            return dto;
        }

    }

    private static final class OrgMapper implements RowMapper<AuthorizationOrganization> {

        @Override
        public AuthorizationOrganization mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final AuthorizationOrganization dto = new AuthorizationOrganization();
            dto.setDescription(rs.getString("DESCRIPTION"));
            dto.setId(rs.getString("ID"));
            dto.setName(rs.getString("NAME"));
            dto.setStatus(rs.getString("STATUS"));
            return dto;
        }

    }

    private static final class ResourceMapper implements RowMapper<AuthorizationResource> {

        @Override
        public AuthorizationResource mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final AuthorizationResource dto = new AuthorizationResource();
            dto.setDescription(rs.getString("DESCRIPTION"));
            dto.setId(rs.getString("ID"));
            dto.setName(rs.getString("NAME"));
            dto.setResourceTypeId(rs.getString("RESOURCE_TYPE_ID"));
            dto.setRisk(rs.getString("RISK"));
            dto.setCoorelatedName(rs.getString("COORELATED_NAME"));
            dto.setPublic(StringUtils.equalsIgnoreCase("y", rs.getString("IS_PUBLIC")));
            return dto;
        }

    }

    private static final class MembershipRightDTOMapper implements RowMapper<MembershipRightDTO> {

        @Override
        public MembershipRightDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final MembershipRightDTO dto = new MembershipRightDTO();
            dto.setId(rs.getString("MEMBERSHIP_ID"));
            dto.setRightId(rs.getString("ACCESS_RIGHT_ID"));
            return dto;
        }
    }

    private static final class MembershipDTOMapper implements RowMapper<MembershipDTO> {

        @Override
        public MembershipDTO mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            final MembershipDTO dto = new MembershipDTO();
            dto.setEntityId(rs.getString("ENTITY_ID"));
            dto.setId(rs.getString("MEMBERSHIP_ID"));
            dto.setMemberEntityId(rs.getString("MEMBER_ENTITY_ID"));
            dto.setStartDate(rs.getDate("START_DATE"));
            dto.setEndDate(rs.getDate("END_DATE"));
            return dto;
        }
    }

    @Override
    public List<String> getUsersForResource(String resourceId, final Date date) {
        return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_RESOURCE, new Object[]{resourceId}, String.class);
    }

    @Override
    public List<String> getUsersForResource(String resourceId, String rightId, final Date date) {
        return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_RESOURCE_WITH_RIGHT, new Object[]{resourceId, rightId}, String.class);
    }

    @Override
    public List<String> getUsersForGroup(String groupId, final Date date) {
        return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_GROUP, new Object[]{groupId}, String.class);
    }

    @Override
    public List<String> getUsersForGroup(String groupId, String rightId, final Date date) {
        return getJdbcTemplate().queryForList(GET_USER_IDS_FOR_GROUP_WITH_RIGHT, new Object[]{groupId, rightId}, String.class);
    }
}
