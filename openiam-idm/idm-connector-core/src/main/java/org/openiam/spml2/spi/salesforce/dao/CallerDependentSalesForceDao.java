package org.openiam.spml2.spi.salesforce.dao;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * @author Lev Bornovalov
 * This is the DAO logic for talking to the SalesForce API
 * This is NOT designed a a Spring Bean.  The Connection is unique to the caller, and is NOT shared
 */
public class CallerDependentSalesForceDao implements SalesForceDao {

	protected static final Log log = LogFactory.getLog(CallerDependentSalesForceDao.class);
	
	private static final String FIELDS = "id, EmailEncodingKey, Alias, Email, TimeZoneSidKey, DefaultGroupNotificationFrequency, Username, LanguageLocaleKey, ProfileId, LocaleSidKey, DigestFrequency, LastName";
	private static final String FIND_BY_ID_SQL = "SELECT " + FIELDS + " FROM User WHERE Id='%s'";
	private static final String FIND_BY_USERNAME = "SELECT " + FIELDS + " FROM User WHERE Username='%s'";
	
	private PartnerConnection partnerConnection;
	
	public CallerDependentSalesForceDao(final String userName, final String password, final String endPoint) throws ConnectionException {
		final ConnectorConfig connectorConfig = new ConnectorConfig();
		connectorConfig.setUsername(userName);
		connectorConfig.setPassword(password);
		connectorConfig.setAuthEndpoint(endPoint);
		partnerConnection = new PartnerConnection(connectorConfig);
	}

	@Override
	public void save(User user) throws ConnectionException, SalesForcePersistException {
		final SaveResult[] saveResult = partnerConnection.create(new SObject[] {user});
		for(final SaveResult result : saveResult) {
			if(result.getSuccess()) {
				user.setId(result.getId());
			} else {
				throw new SalesForcePersistException(result);
			}
		}
	}

	@Override
	public void update(User user) throws ConnectionException, SalesForcePersistException {
		final SaveResult[] saveResult = partnerConnection.update(new SObject[] {user});
		for(final SaveResult result : saveResult) {
			if(!result.getSuccess()) {
				throw new SalesForcePersistException(result);
			}
		}
	}

	@Override
	public void saveOrUpdate(User user) throws ConnectionException, SalesForcePersistException {
		boolean update = false;
		if(user.getId() != null) {
			update = true;
		} else {
			final User found = findByUserName(user.getUserName());
			if(found != null) {
				user.setId(found.getId());
				update = true;
			}
		}
		
		if(update) {
			update(user);
		} else {
			save(user);
		}
	}

	@Override
	public User findByUserName(String userName) throws ConnectionException {
		final String sql = String.format(FIND_BY_USERNAME, StringEscapeUtils.escapeSql(userName));
		if(log.isDebugEnabled()) {
			log.debug(String.format("FindByUserName:%s", sql));
		}
		final QueryResult queryResult = partnerConnection.query(sql);
		return (queryResult.getSize() == 1) ? new User(queryResult.getRecords()[0]) : null;
	}

	@Override
	public User findById(String id) throws ConnectionException {
		final String sql = String.format(FIND_BY_ID_SQL, StringEscapeUtils.escapeSql(id));
		if(log.isDebugEnabled()) {
			log.debug(String.format("FindByUserName:%s", sql));
		}
		final QueryResult queryResult = partnerConnection.query(sql);
		return (queryResult.getSize() == 1) ? new User(queryResult.getRecords()[0]) : null;
	}

	@Override
	public void deleteByUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undeleteByUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undeleteById(String id) {
		// TODO Auto-generated method stub
		
	}
}
