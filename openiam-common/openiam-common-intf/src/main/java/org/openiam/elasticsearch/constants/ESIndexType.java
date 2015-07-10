package org.openiam.elasticsearch.constants;

/**
 * Created by: Alexander Duckardt
 * Date: 6/25/14.
 */
public class ESIndexType {
    public static final String USER="user";
    public static final String EMAIL="email";
    public static final String PHONE = "phone";
    public static final String LOGIN = "login";
    public static final String IDENTITY = "identity";
    public static final String LOCATION = "location";
    
    public static final String USER_TO_ROLE_XREF="userrolexref";
    public static final String USER_TO_GROUP_XREF="usergroupxref";
    public static final String USER_TO_ORG_XREF="userorgxref";
    public static final String USER_TO_RES_XREF="userresxref";
    
    public static final String RES_TO_ORG_XREF = "resorgxref";
    public static final String ORG_TO_ORG_XREF = "orgorgxref";
    public static final String ROLE_TO_ORG_XREF = "roleorgxref";
    public static final String GRP_TO_ORG_XREF = "grporgxref";
    
    public static final String ROLE_TO_GRP_XREF = "rolegrpxref";
    public static final String GRP_TO_RES_XREF = "grpresxref";
    public static final String GRP_TO_GRP_XREF = "grpgrpxref";
    
    public static final String ROLE_TO_RES_XREF = "roleresxref";
    public static final String ROLE_TO_ROLE_XREF = "rolerolexref";
    
    public static final String RES_TO_RES_XREF = "resresxref";
}