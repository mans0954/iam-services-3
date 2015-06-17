package org.openiam.connector.salesforce.dao;

import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.User;

import com.sforce.ws.ConnectionException;

public interface SalesForceDao extends BaseSalesForceDao<User> {
	User findById(final String id) throws ConnectionException, SalesForcePersistException;
	void deleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	void deleteById(final String id) throws ConnectionException, SalesForcePersistException;
	void undeleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	void undeleteById(final String id) throws ConnectionException, SalesForcePersistException;
	void resetPasswordById(final String id) throws ConnectionException;
	void resetPasswordByUserName(final String userName) throws ConnectionException;
	void setPasswordById(final String id, final String password) throws ConnectionException;
	void setPasswordByUserName(final String userName, final String password) throws ConnectionException;
}
