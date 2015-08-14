package org.openiam.connector.util;

import javax.naming.*;
import javax.naming.ldap.LdapContext;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.springframework.context.ApplicationContext;

/**
 * Interface for objects that will manage the connection to the target system.
 * @author suneet
 *
 */
public interface ConnectionMgr {

	LdapContext connect(ManagedSysEntity managedSys) throws NamingException;
	void close() throws NamingException;
    void setApplicationContext(ApplicationContext applicationContext);
}
