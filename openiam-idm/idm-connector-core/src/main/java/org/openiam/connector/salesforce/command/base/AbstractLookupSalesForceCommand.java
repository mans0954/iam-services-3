package org.openiam.connector.salesforce.command.base;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ObjectValue;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.LookupRequest;
import org.openiam.base.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.common.data.ConnectorConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupSalesForceCommand<ExtObject extends ExtensibleObject> extends AbstractSalesforceCommand<LookupRequest<ExtObject>, SearchResponse>{
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = lookupRequest.getTargetID();
        final String objectId = lookupRequest.getSearchValue();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        final ExtObject object = lookupRequest.getExtensibleObject();
        final Set<String> fieldNames = new HashSet<String>();

        /* only attempt to fetch if you can construct a Select query, since SalesForce does not allow 'SELECT *' queries */
        final List<ExtensibleAttribute> attrList = object.getAttributes();
        if(CollectionUtils.isNotEmpty(attrList)) {
            for (final ExtensibleAttribute att : attrList) {
                fieldNames.add(att.getName());
            }
        }
        final ObjectValue resultObject = lookupObject(objectId, configuration.getManagedSys(), fieldNames);
        if(resultObject!=null)
            response.getObjectList().add(resultObject);
        return response;
    }

    protected abstract ObjectValue lookupObject(String objectId, ManagedSysEntity managedSys, Set<String> fieldNames) throws ConnectorDataException;
}
