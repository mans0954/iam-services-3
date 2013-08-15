package org.openiam.connector.salesforce.dao;

import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.User;

import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.ResetPasswordResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.SetPasswordResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

/**
 * @author Lev Bornovalov
 * This is the DAO logic for talking to the SalesForce API
 * This is NOT designed a a Spring Bean.  The Connection is unique to the caller, and is NOT shared
 */
public class CallerDependentSalesForceDao extends BaseSalesForceDaoImpl<User> implements SalesForceDao {

	private static final String FIND_BY_ID_SQL = "SELECT %s FROM User WHERE Id='%s'";
	private static final String FIND_BY_USERNAME = "SELECT %s FROM User WHERE Username='%s'";

	public CallerDependentSalesForceDao(final String userName, final String password, final String endPoint, final Set<String> fields) throws ConnectionException {
		super(userName, password, endPoint, fields);
	}


	@Override
	public User findByName(String name) throws ConnectionException {
		final String sql = String.format(FIND_BY_USERNAME, queryFields, StringEscapeUtils.escapeSql(name));
		if(log.isDebugEnabled()) {
			log.debug(String.format("FindByUserName:%s", sql));
		}
		final QueryResult queryResult = partnerConnection.query(sql);
		return (queryResult.getSize() == 1) ? new User(queryResult.getRecords()[0]) : null;
	}

	@Override
	public User findById(String id) throws ConnectionException {
		final String sql = String.format(FIND_BY_ID_SQL, queryFields, StringEscapeUtils.escapeSql(id));
		if(log.isDebugEnabled()) {
			log.debug(String.format("FindByUserName:%s", sql));
		}
		final QueryResult queryResult = partnerConnection.query(sql);
		return (queryResult.getSize() == 1) ? new User(queryResult.getRecords()[0]) : null;
	}

	@Override
	public void deleteByUserName(String userName) throws ConnectionException, SalesForcePersistException {
		final User user = findByName(userName);
		if(user != null) {
			user.setActive(false);
			final SaveResult[] saveResult = partnerConnection.update(new SObject[] {user});
			processSaveResult(saveResult);
		}
	}

	@Override
	public void deleteById(String id) throws ConnectionException, SalesForcePersistException {
		final User user = findById(id);
		if(user != null) {
			user.setActive(false);
			final SaveResult[] saveResult = partnerConnection.update(new SObject[] {user});
			processSaveResult(saveResult);
		}
	}

	@Override
	public void undeleteByUserName(String userName) throws ConnectionException, SalesForcePersistException {
		final User user = findByName(userName);
		if(user != null) {
			user.setActive(true);
			final SaveResult[] saveResult = partnerConnection.update(new SObject[] {user});
			processSaveResult(saveResult);
		}
	}

	@Override
	public void undeleteById(String id) throws ConnectionException, SalesForcePersistException {
		final User user = findById(id);
		if(user != null) {
			user.setActive(true);
			final SaveResult[] saveResult = partnerConnection.update(new SObject[] {user});
			processSaveResult(saveResult);
		}
	}
	
	@Override
	public void resetPasswordById(final String id) throws ConnectionException {
		final ResetPasswordResult resetPasswordResult =  partnerConnection.resetPassword(id);
	}
	
	@Override
	public void resetPasswordByUserName(final String userName) throws ConnectionException {
		final User user = findByName(userName);
		if(user != null) {
			resetPasswordById(user.getId());
		}
	}
	
	@Override
	public void setPasswordById(final String id, final String password) throws ConnectionException {
		final SetPasswordResult setPasswordResult = partnerConnection.setPassword(id, password);
	}
	
	@Override
	public void setPasswordByUserName(final String userName, final String password) throws ConnectionException {
		final User user = findByName(userName);
		if(user != null) {
			setPasswordById(user.getId(), password);
		}
	}
	

}
