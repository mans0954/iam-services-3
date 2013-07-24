package org.openiam.connector.util;

import javax.naming.*;
import javax.naming.ldap.LdapContext;

import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.springframework.context.ApplicationContext;

/**
 * Interface for objects that will manage the connection to the target system.
 * @author suneet
 *
 */
public interface ConnectionMgr {

	public LdapContext connect(ManagedSysDto managedSys) throws NamingException;
	public void close() throws NamingException;
    public void setApplicationContext(ApplicationContext applicationContext);
}
