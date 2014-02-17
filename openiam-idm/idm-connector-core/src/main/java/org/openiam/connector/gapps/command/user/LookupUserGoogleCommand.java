package org.openiam.connector.gapps.command.user;

import java.util.ArrayList;
import java.util.List;

import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("lookupUserGoogleAppsCommand")
public class LookupUserGoogleCommand
        extends
        AbstractGoogleAppsCommand<LookupRequest<ExtensibleUser>, SearchResponse> {

    @Override
    public SearchResponse execute(LookupRequest<ExtensibleUser> searchRequest)
            throws ConnectorDataException {
        SearchResponse response = new SearchResponse();
        ManagedSysEntity mSys = managedSysService
                .getManagedSysById(searchRequest.getTargetID());
        String adminEmail = mSys.getUserId();
        String password = this.getPassword(mSys.getId());
        String domain = mSys.getHostUrl();
        try {
            GoogleAgent client = new GoogleAgent();
            GenericEntry googleUser = client.getUser(adminEmail, password,
                    domain, searchRequest.getSearchValue());
            List<ObjectValue> objList = new ArrayList<ObjectValue>();
            ExtensibleObject o = this.googleUserToExtensibleAttributes(googleUser
                    .getAllProperties());
            ObjectValue ov = new ObjectValue();
            ov.setAttributeList(o.getAttributes());
            ov.setObjectIdentity(o.getObjectId());
            objList.add(ov);
            response.setStatus(StatusCodeType.SUCCESS);
            response.setObjectList(objList);
        } catch (Exception e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return response;
    }
}
