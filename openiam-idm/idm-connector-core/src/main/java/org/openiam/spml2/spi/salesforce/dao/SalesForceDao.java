package org.openiam.spml2.spi.salesforce.dao;

import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;

import com.sforce.ws.ConnectionException;

public interface SalesForceDao {
	public void save(final User user) throws ConnectionException, SalesForcePersistException;
	public void update(final User user) throws ConnectionException, SalesForcePersistException;
	public void saveOrUpdate(final User user) throws ConnectionException, SalesForcePersistException;
	public User findByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	public User findById(final String id) throws ConnectionException, SalesForcePersistException;
	public void deleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	public void deleteById(final String id) throws ConnectionException, SalesForcePersistException;
	public void undeleteByUserName(final String userName) throws ConnectionException, SalesForcePersistException;
	public void undeleteById(final String id) throws ConnectionException, SalesForcePersistException;
}
