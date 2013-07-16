package org.openiam.spml2.spi.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.data.ConnectorConfiguration;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;
import org.openiam.spml2.util.msg.ResponseBuilder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractLookupSalesForceCommand<ProvisionObject extends GenericProvisionObject> extends AbstractSalesforceCommand<LookupRequestType<ProvisionObject>, LookupResponseType> {
    @Override
    public LookupResponseType execute(LookupRequestType<ProvisionObject> lookupRequestType) throws ConnectorDataException {
        final LookupResponseType response = new LookupResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = lookupRequestType.getPsoID().getTargetID();
        final String objectId = lookupRequestType.getPsoID().getID();

        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);

        final List<ExtensibleObject> objectList = lookupRequestType.getAny();
        final Set<String> fieldNames = new HashSet<String>();

        /* only attempt to fetch if you can construct a Select query, since SalesForce does not allow 'SELECT *' queries */
        if(CollectionUtils.isNotEmpty(objectList)) {
            for (final ExtensibleObject obj : objectList) {
                final List<ExtensibleAttribute> attrList = obj.getAttributes();
                if(CollectionUtils.isNotEmpty(attrList)) {
                    for (final ExtensibleAttribute att : attrList) {
                        fieldNames.add(att.getName());
                    }
                }
            }
        }
        final ExtensibleObject resultObject = lookupObject(objectId, configuration.getManagedSys(), fieldNames);
        if(resultObject!=null)
            response.getAny().add(resultObject);
        return response;
    }

    protected abstract ExtensibleObject lookupObject(String objectId, ManagedSysEntity managedSys, Set<String> fieldNames) throws ConnectorDataException;
}
