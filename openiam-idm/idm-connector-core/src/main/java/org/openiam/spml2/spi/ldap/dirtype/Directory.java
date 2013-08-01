package org.openiam.spml2.spi.ldap.dirtype;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.request.SuspendRequest;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.NamingException;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Directories may have differences in how they implement certain details. Objects implementing this interface are used
 * to support these variations.
 * User: suneetshah
 * Date: 1/26/12
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Directory {
    
    final static String ACTIVE_DIRECTORY = "ACTIVE_DIRECTORY";
    final static String LDAP_V3 = "LDAP_V3";

    
    ModificationItem[] setPassword(PasswordRequest reqType) throws UnsupportedEncodingException;

    ModificationItem[] suspend(SuspendRequest request);

    ModificationItem[] resume(SuspendResumeRequest request);

    /**
     * setAttributes allows you to set attributes on the implementation object which may be need for the specific
     * implementation
     * @param name
     * @param obj
     */
    
    void setAttributes(String name, Object obj);

    void delete(CrudRequest reqType, LdapContext ldapctx, String ldapName, String onDelete ) throws NamingException;

    void removeAccountMemberships( String ldapName, ManagedSystemObjectMatch matchObj,  LdapContext ldapctx );

    void updateAccountMembership(List<BaseAttribute> targetMembershipList, String ldapName,
                                 ManagedSystemObjectMatch matchObj,  LdapContext ldapctx, ExtensibleObject obj );

    
}
