package org.openiam.authmanager.dao.impl;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public abstract class AbstractJDBCDao extends JdbcDaoSupport {

	@Autowired
	@Qualifier("dataSource") 
	private DataSource dataSource;
	
	@Value("${openiam.databaseSchema.name}")
	private String schemaName;
	
	@PostConstruct
	protected void initDataSource() {
		setDataSource(dataSource);
		initSqlStatements();
	} 
	
	public String getSchemaName() {
		return schemaName;
	}
	
	protected abstract void initSqlStatements();
}
