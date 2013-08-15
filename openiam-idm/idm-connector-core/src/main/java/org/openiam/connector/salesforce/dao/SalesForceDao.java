package org.openiam.connector.salesforce.dao;

import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.User;

import com.sforce.ws.ConnectionException;

public interface SalesForceDao extends BaseSalesForceDao<User> {
	public User findById(final String id) throws ConnectionException, SalesForcePersistException;
	public void deleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	public void deleteById(final String id) throws ConnectionException, SalesForcePersistException;
	public void undeleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	public void undeleteById(final String id) throws ConnectionException, SalesForcePersistException;
	public void resetPasswordById(final String id) throws ConnectionException;
	public void resetPasswordByUserName(final String userName) throws ConnectionException;
	public void setPasswordById(final String id, final String password) throws ConnectionException;
	public void setPasswordByUserName(final String userName, final String password) throws ConnectionException;
}
