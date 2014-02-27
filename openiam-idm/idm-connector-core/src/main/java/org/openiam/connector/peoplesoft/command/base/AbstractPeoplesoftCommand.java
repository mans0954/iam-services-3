package org.openiam.connector.peoplesoft.command.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.mule.util.StringUtils;
import org.openiam.connector.common.jdbc.AbstractJDBCCommand;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * PSUSERATTR - Forgot Password Functionality record.
 * 
 * PS_ROLEXLATOPR - Peopleoft USer Profile and WorkFlow Table, it also stores
 * the supervisor of the current user
 * 
 * Table SYSADM.PS_ROLEXLATOPR@dev
 * 
 * PSUSEREMAIL - User/Operators e-mail record
 */
public abstract class AbstractPeoplesoftCommand<Request extends RequestType, Response extends ResponseType> extends
        AbstractJDBCCommand<Request, Response> {
    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;
    @Autowired
    private KeyManagementService keyManagementService;
    private static final String EMPTY_STRING = "";
    private static final String BLANK_SPACE_STRING = " ";

    private static final String SELECT_SQL = "SELECT OPRID FROM %sPSOPRDEFN WHERE OPRID=?";
    private static final String SELECT_ROLE = "SELECT ROLEUSER, ROLENAME FROM %sPSROLEUSER WHERE ROLEUSER=? AND ROLENAME = ? ";
    private static final String SELECT_SYMBOLIC_ID = "SELECT SYMBOLICID FROM %sPSACCESSPRFL";

    private static final String SELECT_VERSION = "SELECT VERSION FROM %sPSVERSION WHERE OBJECTTYPENAME = 'SYS'";

    private static final String INSERT_EMAIL = "INSERT INTO %sPSUSEREMAIL (OPRID, EMAILTYPE, EMAILID, PRIMARY_EMAIL) "
            + " VALUES (?, ?, ?, ? )";

    private static final String UPDATE_EMAIL = "UPDATE %sPSUSEREMAIL " + " SET EMAILID = ? " + " WHERE OPRID = ?";

    private static final String SELECT_EMAIL = "SELECT OPRID FROM %sPSUSEREMAIL WHERE OPRID=?  ";
    // oprid

    private static final String INSERT_ROLEXLATOPR = "INSERT INTO %sPS_ROLEXLATOPR (ROLEUSER, DESCR, OPRID, EMAILID, FORMID,"
            + " WORKLIST_USER_SW, EMAIL_USER_SW, EMPLID, FORMS_USER_SW, ROLEUSER_ALT, ROLEUSER_SUPR) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    private static final String SELECT_ROLEXLATOPR = "SELECT ROLEUSER FROM %sPS_ROLEXLATOPR WHERE ROLEUSER=?  ";

    // roleuser

    private static final String INSERT_PSUSERATTR = "INSERT INTO %sPSUSERATTR (OPRID, HINT_QUESTION, HINT_RESPONSE, NO_SYMBID_WARN,"
            + " LASTUPDOPRID, MPDEFAULMP ) " + " VALUES (?, ?, ?, ?, ?, ? )";

    private static final String SELECT_PSUSERATTR = "SELECT OPRID FROM %sPSUSERATTR WHERE OPRID=?  ";

    // OPRID

    private static final String INSERT_ADD_USER = "INSERT INTO %sPSOPRDEFN (OPRID, OPRDEFNDESC, EMPLID, EMAILID, SYMBOLICID, "
            + " VERSION,  OPRCLASS, ROWSECCLASS, OPERPSWD, ENCRYPTED, LANGUAGE_CD, MULTILANG, CURRENCY_CD, LASTPSWDCHANGE,"
            + " ACCTLOCK, PRCSPRFLCLS, DEFAULTNAVHP, FAILEDLOGINS, EXPENT, OPRTYPE, USERIDALIAS,  LASTUPDOPRID, PTALLOWSWITCHUSER, LASTUPDDTTM   ) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE )";

    private static final String UPDATE_USER = "UPDATE %sPSOPRDEFN "
            + " SET OPRDEFNDESC = ?, EMAILID = ?, ACCTLOCK = ? ,LASTUPDDTTM = SYSDATE, LASTUPDOPRID='AUTO_IDM' "
            + " WHERE OPRID = ?";

    private static final String UPDATE_LOCK = "UPDATE %sPSOPRDEFN "
            + " SET ACCTLOCK = ?, LASTUPDDTTM = SYSDATE, LASTUPDOPRID='AUTO_IDM' " + " WHERE OPRID = ?";

    private static final String INSERT_ADD_ROLE = "INSERT INTO %sPSROLEUSER (ROLEUSER, ROLENAME, DYNAMIC_SW ) VALUES (?, ?, ?)";

    private static final String INSERT_ADD_ALIAS = "INSERT INTO %sPSOPRALIAS (OPRID, OPRALIASTYPE,OPRALIASVALUE, EMPLID, SETID, "
            + " CUST_ID, VENDOR_ID, APPLID, CONTACT_ID, PERSON_ID, EXT_ORG_ID, BIDDER_ID, EOTP_PARTNERID ) "
            + " VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?   )";

    private static final String CHANGE_PASSWORD_SQL = "UPDATE %sPSOPRDEFN SET OPERPSWD = ? WHERE OPRID = ?";

    //
    private static final String INSERT_PSPRUHDEFN = "INSERT INTO %sPSPRUHDEFN "
            + "            (PORTAL_NAME, OPRID, PORTAL_GREETING254,VERSION, LASTUPDOPRID, LASTUPDDTTM,OBJECTOWNERID  ) "
            + "  VALUES (? ,?, ?, ? , 'AUTO_IDM', SYSDATE, ?) ";

    private static final String SELECT_PSPRUHDEFN = "SELECT OPRID FROM %sPSPRUHDEFN WHERE OPRID=?  ";

    private static final String SELECT_PSPRUHTAB = "SELECT OPRID FROM %sPSPRUHTAB WHERE OPRID=?  ";
    private static final String SELECT_PSPRUHTABPGLT = "SELECT OPRID FROM %sPSPRUHTABPGLT WHERE OPRID=?  ";

    private static final String INSERT_PSPRUHTAB = "INSERT INTO %sPSPRUHTAB "
            + "            (PORTAL_NAME, OPRID, PORTAL_OBJNAME, PORTAL_LABEL,PORTAL_COLLAYOUT,PORTAL_SEQ_NUM,PORTAL_STG_NAME ) "
            + "            VALUES ('EMPLOYEE' ,?, 'DEFAULT', 'My Page' , 2 , 0, 'PR_EMPLOYEE_DEFAULT') ";

    private static final String INSERT_PSPRUHTABPGLT = "INSERT INTO %sPSPRUHTABPGLT "
            + "            (PORTAL_NAME, OPRID, PORTAL_OBJNAME, PORTAL_OBJNAME_PGT,PORTAL_COL_NUM,PORTAL_ROW_NUM,PORTAL_MINIMIZE ) "
            + "            VALUES ('EMPLOYEE' ,?, 'DEFAULT', ? , 1 , 1, 0) ";

    static protected ResourceBundle res = ResourceBundle.getBundle("peoplesoft");
    static protected String schemaName = null;

    protected boolean identityExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {

                String sql = String.format(SELECT_SQL, schemaName);

                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    protected boolean roleExists(final Connection connection, final String principalName, final String roleName)
            throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_ROLE, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                statement.setString(2, roleName);

                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    public boolean userAttrExists(final Connection connection, final String principalName) throws SQLException {

        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_PSUSERATTR, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;

    }

    public boolean emailExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_EMAIL, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    public boolean roleExlatoprExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_ROLEXLATOPR, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    public boolean pspruhdefnExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_PSPRUHDEFN, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {

                    return true;
                }
            }
        }

        return exists;
    }

    public boolean pspruhtabExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_PSPRUHTAB, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    public boolean psruhtabpgltExists(final Connection connection, final String principalName) throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(SELECT_PSPRUHTABPGLT, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                final ResultSet rs = statement.executeQuery();
                if (rs != null && rs.next()) {
                    return true;
                }
            }
        }
        return exists;
    }

    protected int getVersion(Connection connection) throws SQLException {

        if (connection != null) {
            String sql = String.format(SELECT_VERSION, schemaName);
            final PreparedStatement statement = connection.prepareStatement(sql);
            final ResultSet rs = statement.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }

        }
        return 0;
    }

    protected String getSymbolicID(Connection connection) throws SQLException {

        if (connection != null) {
            String sql = String.format(SELECT_SYMBOLIC_ID, schemaName);
            final PreparedStatement statement = connection.prepareStatement(sql);
            final ResultSet rs = statement.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getString("SYMBOLICID");
            }

        }
        return null;
    }

    protected boolean insertEmail(final Connection connection, final String principalName, String email)
            throws SQLException {

        String sql = String.format(INSERT_EMAIL, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, principalName);
        statement.setString(2, "BUS");
        statement.setString(3, email);
        statement.setString(4, "Y");
        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected boolean insertUserAttribute(final Connection connection, final String principalName) throws SQLException {

        String sql = String.format(INSERT_PSUSERATTR, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, principalName);
        statement.setString(2, BLANK_SPACE_STRING);
        statement.setString(3, BLANK_SPACE_STRING);
        statement.setString(4, "N");
        statement.setString(5, BLANK_SPACE_STRING);
        statement.setString(6, BLANK_SPACE_STRING);
        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected boolean insertRoleExlatopr(final Connection connection, final String principalName,
            final String displayName, String email, final String employeeId) throws SQLException {

        String sql = String.format(INSERT_ROLEXLATOPR, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, principalName);
        statement.setString(2, displayName);
        statement.setString(3, principalName);
        statement.setString(4, nullCheck(email));
        statement.setString(5, BLANK_SPACE_STRING);
        statement.setString(6, "N");
        statement.setString(7, "Y");
        statement.setString(8, employeeId);
        statement.setString(9, "Y");
        statement.setString(10, BLANK_SPACE_STRING);
        statement.setString(11, BLANK_SPACE_STRING);
        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected void updateUser(final Connection connection, final String principalName, final String displayName,
            final String email, int status) throws SQLException {

        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(UPDATE_USER, schemaName);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, nullCheck(displayName));
                statement.setString(2, nullCheck(email));
                statement.setInt(3, status);
                statement.setString(4, principalName);

                int result = statement.executeUpdate();
                if (result == 0) {
                    return;
                }

            }
        }

    }

    protected void updateUserLock(final Connection connection, final String principalName, int status)
            throws SQLException {

        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(UPDATE_LOCK, schemaName);

                log.debug("Update User Lock SQL:" + sql);
                log.debug("Status:" + status);
                log.debug("principalName:" + principalName);

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, status);
                statement.setString(2, principalName);

                int result = statement.executeUpdate();
                if (result == 0) {
                    return;
                }

            }
        }

    }

    protected void updateEmail(final Connection connection, final String principalName, final String email)
            throws SQLException {

        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(UPDATE_EMAIL, schemaName);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, nullCheck(email));
                statement.setString(2, principalName);

                int result = statement.executeUpdate();
                if (result == 0) {
                    return;
                }

            }
        }

    }

    protected boolean insertUser(final Connection connection, final String principalName, final String displayName,
            final String employeeId, final String email, final String symbolicId, final String password, int version,
            int status) throws SQLException {
        boolean exists = false;

        String encPassword = null;
        try {

            encPassword = encryptPassword(password);
        } catch (EncryptionException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(INSERT_ADD_USER, schemaName);
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                statement.setString(2, nullCheck(displayName));
                statement.setString(3, nullCheck(employeeId));
                statement.setString(4, nullCheck(email));
                statement.setString(5, symbolicId);
                statement.setInt(6, version);
                statement.setString(7, "HCDPALL"); // OPRCLASS
                statement.setString(8, "HCDPALL"); // ROWSECCLASS
                statement.setString(9, encPassword); // OPERPSWD
                statement.setInt(10, 1); // ENCRYPTED
                statement.setString(11, "ENG"); // LANGUAGE_CD
                statement.setInt(12, 0); // MULTILANG
                statement.setString(13, "USD"); // CURRENCY CODE
                statement.setDate(14, new Date(System.currentTimeMillis())); // LASTPSWDCHANGE
                statement.setInt(15, status); // ACCTLOCK
                statement.setString(16, "HCSPPRFL"); // PRCSPRFLCLS
                statement.setString(17, "HCSPNAVHP"); // DEFAULTNAVHP
                statement.setInt(18, 0); // FAILEDLOGINS
                statement.setInt(19, 0); // EXPENT
                statement.setInt(20, 1); // OPRTYPE
                statement.setString(21, nullCheck(employeeId)); // USERIDALIAS
                statement.setString(22, "AUTO_IDM"); // LASTUPDOPRID
                statement.setInt(23, 0); // PTALLOWSWITCHUSER

                int result = statement.executeUpdate();
                if (result == 0) {
                    return false;
                }

                // INSERT ALIAS
                sql = String.format(INSERT_ADD_ALIAS, schemaName);
                statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                statement.setString(2, "EMP");
                statement.setString(3, employeeId);
                statement.setString(4, employeeId);
                statement.setString(5, BLANK_SPACE_STRING); // SETID
                statement.setString(6, BLANK_SPACE_STRING); // CUST_ID
                statement.setString(7, BLANK_SPACE_STRING); // VENDOR_ID
                statement.setString(8, BLANK_SPACE_STRING); // APPLID
                statement.setString(9, BLANK_SPACE_STRING); // CONTACT_ID
                statement.setString(10, BLANK_SPACE_STRING); // PERSON_ID
                statement.setString(11, BLANK_SPACE_STRING); // EXT_ORG_ID
                statement.setString(12, BLANK_SPACE_STRING); // BIDDER_ID
                statement.setInt(13, 0); // EOTP_PARTNERID

                result = statement.executeUpdate();
                return result != 0;

            }
        }
        return exists;
    }

    protected boolean insertPSPRUHDEFN(final Connection connection, final String principalName, int version)
            throws SQLException {

        String sql = String.format(INSERT_PSPRUHDEFN, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "EMPLOYEE");
        statement.setString(2, principalName);
        statement.setString(3, "Stater Bros. Markets");
        statement.setInt(4, 62);
        statement.setString(5, BLANK_SPACE_STRING);

        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected boolean insertPSPRUHTAB(final Connection connection, final String principalName) throws SQLException {

        String sql = String.format(INSERT_PSPRUHTAB, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, principalName);
        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected boolean insertPSPRUHTABPGLT(final Connection connection, final String principalName) throws SQLException {

        String sql = String.format(INSERT_PSPRUHTABPGLT, schemaName);
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, principalName);
        statement.setString(2, "PT_SC_PGT_MAIN_MENU");
        int result = statement.executeUpdate();
        if (result == 0) {
            return false;
        }
        return true;

    }

    protected String nullCheck(String str) {
        if (str == null || str.isEmpty()) {
            return BLANK_SPACE_STRING;

        }
        return str;
    }

    protected boolean addToRole(final Connection connection, final String principalName, final String role)
            throws SQLException {
        boolean exists = false;
        if (connection != null) {
            if (StringUtils.isNotBlank(principalName)) {
                String sql = String.format(INSERT_ADD_ROLE, schemaName);
                final PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, principalName);
                statement.setString(2, role);
                statement.setString(3, "N");
                int result = statement.executeUpdate();
                return result != 0;

            }
        }
        return exists;
    }

    protected boolean changePassword(final ManagedSysDto managedSys, final String principalName, final String password)
            throws SQLException, ClassNotFoundException {

        Connection connection = null;

        try {
            connection = connectionMgr.connect(managedSys);
            String sql = String.format(CHANGE_PASSWORD_SQL, schemaName);

            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(2, principalName);
            try {
                statement.setString(1, encryptPassword(password));
            } catch (EncryptionException e) {
                e.printStackTrace();

            }
            int result = statement.executeUpdate();
            return result != 0;

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public String encryptPassword(String password) throws EncryptionException {
        if (password != null) {
            return cryptor.encrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), password);
        }
        return null;
    }
}