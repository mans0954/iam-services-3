package org.openiam.connector.gapps.command.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

@Service("lookupUserGoogleAppsCommand")
public class LookupUserGoogleCommand
        extends
        AbstractGoogleAppsCommand<LookupRequest<ExtensibleUser>, SearchResponse> {

    @Override
    public SearchResponse execute(LookupRequest<ExtensibleUser> searchRequest)
            throws ConnectorDataException {
        SearchResponse response = new SearchResponse();
        try {

            response.setStatus(StatusCodeType.SUCCESS);
            ManagedSysEntity mSys = managedSysService
                    .getManagedSysById(searchRequest.getTargetID());
            List<ManagedSysRuleEntity> rules = this.getRules(mSys);

            Directory dir = getGoogleAppsClient(rules);

            User u = dir.users().get(searchRequest.getSearchValue()).execute();

            List<ObjectValue> objList = new ArrayList<ObjectValue>();
            ExtensibleUser exUser = new ExtensibleUser();
            this.convertToExtensibleUser(u, exUser, rules);
            ObjectValue obj = new ObjectValue();
            obj.setAttributeList(exUser.getAttributes());
            obj.setObjectIdentity(exUser.getObjectId());
            objList.add(obj);
            response.setObjectList(objList);
        } catch (IOException e) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        }
        return response;
    }
}
