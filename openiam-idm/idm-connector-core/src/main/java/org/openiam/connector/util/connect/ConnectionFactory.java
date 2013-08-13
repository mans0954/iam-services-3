package org.openiam.connector.util.connect;

import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.util.SpringContextProvider;

/**
 * Created by IntelliJ IDEA.
 * User: suneetshah
 * Date: 4/23/11
 * Time: 1:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionFactory {

    public static ConnectionMgr create( String factoryType) {

		if (factoryType.equals(ConnectionManagerConstant.LDAP_CONNECTION  )) {
			return SpringContextProvider.getApplicationContext().getAutowireCapableBeanFactory().getBean(LdapConnectionMgr.class);
		}
		return null;
	}


}
