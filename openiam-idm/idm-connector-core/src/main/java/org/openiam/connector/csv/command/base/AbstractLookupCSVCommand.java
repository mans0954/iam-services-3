package org.openiam.connector.csv.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.result.dto.ReconciliationResultField;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractLookupCSVCommand <T, ExtObject extends ExtensibleObject> extends AbstractCSVCommand<LookupRequest<ExtensibleObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtensibleObject> lookupRequest) throws ConnectorDataException {
        SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);
        String principal = lookupRequest.getObjectIdentity();
		/*
		 * A) Use the targetID to look up the connection information under
		 * managed systems
		 */
        ConnectorConfiguration config =  getConfiguration(lookupRequest.getTargetID(), ConnectorConfiguration.class);

        // Initialise
        ObjectValue object = this.lookupObjectInCSV(principal, config.getManagedSys());
        if (object != null) {
            response.getObjectList().add(object);
        } else
            response.setStatus(StatusCodeType.FAILURE);
        return response;
    }

    private ObjectValue lookupObjectInCSV(String principal, ManagedSysEntity managedSys) throws ConnectorDataException{
        List<AttributeMapEntity> attrMapList = managedSysService.getResourceAttributeMaps(managedSys.getResourceId());
        List<ReconciliationObject<T>> objectList = this.getObjectList(managedSys, attrMapList);
        List<ExtensibleAttribute> eAttr = new ArrayList<ExtensibleAttribute>(0);

        for (ReconciliationObject<T> obj : objectList) {
            if (match(principal, obj)) {
                ObjectValue objectValue = new ObjectValue();
                Map<String, ReconciliationResultField> res = this.getProvisionMap(obj, attrMapList);
                for (String key : res.keySet()) {
                    if (res.get(key) != null)
                        eAttr.add(new ExtensibleAttribute(key, obj.getPrincipal()));
                }
                objectValue.setObjectIdentity(obj.getPrincipal());
                objectValue.setAttributeList(eAttr);
                return objectValue;
            }
        }
        return null;
    }

    private boolean match(String findValue, ReconciliationObject<T> object) throws ConnectorDataException {
        return StringUtils.hasText(findValue) && object != null && findValue.equals(object.getPrincipal());
    }

    protected abstract List<ReconciliationObject<T>> getObjectList(ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList) throws ConnectorDataException;
    protected abstract Map<String, ReconciliationResultField> getProvisionMap(ReconciliationObject<T> object, List<AttributeMapEntity> attrMapList) throws ConnectorDataException;
}
