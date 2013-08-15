package org.openiam.connector.salesforce.dao;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.BaseModel;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseSalesForceDaoImpl<Model extends BaseModel> implements BaseSalesForceDao<Model>{
    protected final Log log = LogFactory.getLog(this.getClass());

    private static final String DEFAULT_SELECT_LIST = "id, IsActive, EmailEncodingKey, Alias, Email, TimeZoneSidKey, DefaultGroupNotificationFrequency, Username, LanguageLocaleKey, ProfileId, LocaleSidKey, DigestFrequency, LastName";
    private static final String GET_PROFILE_ID_BY_NAME = "SELECT Id FROM Profile WHERE Name='%s'";

    protected StringBuilder queryFields = null;
    protected PartnerConnection partnerConnection;

    public  BaseSalesForceDaoImpl(final String userName, final String password, final String endPoint, final Set<String> fields) throws ConnectionException {
        final ConnectorConfig connectorConfig = new ConnectorConfig();
        connectorConfig.setUsername(userName);
        connectorConfig.setPassword(password);
        connectorConfig.setAuthEndpoint(endPoint);
        partnerConnection = new PartnerConnection(connectorConfig);
        queryFields = new StringBuilder();
        if(CollectionUtils.isNotEmpty(fields)) {
            fields.add("id");
            fields.add("IsActive");
            int i = 0;
            for(final String field : fields) {
                queryFields.append(field);
                if(i++ < fields.size() - 1) {
                    queryFields.append(", ");
                }
            }
        } else {
            queryFields.append(DEFAULT_SELECT_LIST);
        }
    }

    @Override
    public void save(Model model) throws ConnectionException, SalesForcePersistException {
        final SaveResult[] saveResult = partnerConnection.create(new SObject[] {model});
        for(final SaveResult result : saveResult) {
            if(result.getSuccess()) {
                model.setId(result.getId());
            } else {
                throw new SalesForcePersistException(result);
            }
        }
    }

    @Override
    public void update(Model model) throws ConnectionException, SalesForcePersistException {
        final SaveResult[] saveResult = partnerConnection.update(new SObject[] {model});
        processSaveResult(saveResult);
    }

    @Override
    public void saveOrUpdate(Model model) throws ConnectionException, SalesForcePersistException {
        boolean update = false;
        if(model.getId() != null) {
            update = true;
        } else {
            final Model found = findByName(model.getNameField());
            if(found != null) {
                model.setId(found.getId());
                update = true;
            }
        }

        if(update) {
            update(model);
        } else {
            save(model);
        }
    }

    @Override
    public Model findByName(String Name) throws ConnectionException {
        throw new ConnectionException("Not Implemented for this object type");
    }


    protected void processSaveResult(final SaveResult[] saveResult) throws SalesForcePersistException {
        for(final SaveResult result : saveResult) {
            if(!result.getSuccess()) {
                throw new SalesForcePersistException(result);
            }
        }
    }

    @Override
    public String getProfileIdByName(final String profileName) throws ConnectionException {
        String id = null;
        final String sql = String.format(GET_PROFILE_ID_BY_NAME, StringEscapeUtils.escapeSql(profileName));
        final QueryResult queryResult = partnerConnection.query(sql);
        for(final SObject profile : queryResult.getRecords()) {
            id = profile.getId();
        }
        return id;
    }
}
