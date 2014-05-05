package org.openiam.connector.common.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages connections to App Tabless
 * 
 * @author Suneet Shah
 * 
 */
@Component("jdbcConnection")
public class JDBCConnectionMgr {

    Connection sqlCon = null;

    private static final Log log = LogFactory.getLog(JDBCConnectionMgr.class);

    public JDBCConnectionMgr() {
    }

    public Connection connect(ManagedSysDto managedSys) throws ClassNotFoundException, SQLException {
	Class.forName(managedSys.getDriverUrl());
	final String url = managedSys.getConnectionString();
	sqlCon = DriverManager.getConnection(url, managedSys.getUserId(), managedSys.getDecryptPassword());
	return sqlCon;
    }
}
