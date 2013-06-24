package org.openiam.spml2.spi.csv.command.base;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractLookupCSVCommand <T, ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<LookupRequestType<ProvisionObject>, LookupResponseType> {
    @Override
    public LookupResponseType execute(LookupRequestType<ProvisionObject> lookupRequestType) throws ConnectorDataException{
        LookupResponseType response = new LookupResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        String principal = lookupRequestType.getPsoID().getID();
		/*
		 * A) Use the targetID to look up the connection information under
		 * managed systems
		 */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(lookupRequestType.getPsoID().getTargetID());

        // Initialise
        if (this.lookupObjectInCSV(principal, managedSys, response.getAny())) {
            response.setStatus(StatusCodeType.SUCCESS);

        } else
            response.setStatus(StatusCodeType.FAILURE);
        return response;
    }

    private boolean lookupObjectInCSV(String principal, ManagedSysEntity managedSys,  List<ExtensibleObject> extObjectList) throws ConnectorDataException{
        List<AttributeMapEntity> attrMapList = managedSysService.getResourceAttributeMaps(managedSys.getResourceId());

        List<ReconciliationObject<T>> objectList = this.getObjectList(managedSys);
        List<ExtensibleAttribute> eAttr = new ArrayList<ExtensibleAttribute>(0);

        for (ReconciliationObject<T> obj : objectList) {
            ExtensibleObject extObject = new ExtensibleObject();
            if (match(principal, obj, extObject)) {
                Map<String, String> res = this.getProvisionMap(obj, attrMapList);
                for (String key : res.keySet())
                    if (res.get(key) != null)
                        eAttr.add(new ExtensibleAttribute(key, obj.getPrincipal()));
                extObject.setAttributes(eAttr);
                extObjectList.add(extObject);
                return true;
            }
        }
        return false;
    }


    protected abstract List<ReconciliationObject<T>> getObjectList(ManagedSysEntity managedSys) throws ConnectorDataException;
    protected abstract boolean match(String principal, ReconciliationObject<T> object, ExtensibleObject extObject) throws ConnectorDataException;
    protected abstract Map<String, String> getProvisionMap(ReconciliationObject<T> object, List<AttributeMapEntity> attrMapList) throws ConnectorDataException;
}
