package org.openiam.connector.gapps.command.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.gapps.GoogleAgent;
import org.openiam.connector.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import com.ctc.wstx.util.StringUtil;
import com.google.gdata.data.appsforyourdomain.generic.GenericEntry;

@Service("searchUserGoogleAppsCommand")
public class SearchUserGoogleCommand
		extends
		AbstractGoogleAppsCommand<SearchRequest<ExtensibleUser>, SearchResponse> {

	@Override
	public SearchResponse execute(SearchRequest<ExtensibleUser> searchRequest)
			throws ConnectorDataException {
		SearchResponse response = new SearchResponse();
		ManagedSysEntity mSys = managedSysService
				.getManagedSysById(searchRequest.getTargetID());
		String adminEmail = mSys.getUserId();
		String password = this.getPassword(mSys.getId());
		String domain = mSys.getHostUrl();
		String searchValue = searchRequest.getSearchQuery();
		try {
			GoogleAgent client = new GoogleAgent();
			List<GenericEntry> googleUsers = null;
			if (StringUtils.isBlank(searchValue) || "*".equals(searchValue)) {
				googleUsers = client.getAllUsers(adminEmail, password, domain);
			} else {
				String[] ouPathes = searchValue.split(",");
				if (ouPathes != null) {
					googleUsers = new ArrayList<GenericEntry>();
					for (String ou : ouPathes) {
						googleUsers.addAll(client
								.retrieveAllOrganizationUsersByOrgUnit(
										adminEmail, password, domain, ou));
					}
				}
			}
			List<ObjectValue> objList = new ArrayList<ObjectValue>();
			if (googleUsers != null)
				for (GenericEntry u : googleUsers) {
					ExtensibleObject o = this
							.googleUserToExtensibleAttributes(u
									.getAllProperties());
					ObjectValue ov = new ObjectValue();
					ov.setAttributeList(o.getAttributes());
					ov.setObjectIdentity(o.getObjectId());
					objList.add(ov);
				}
			response.setStatus(StatusCodeType.SUCCESS);
			response.setObjectList(objList);
		} catch (Exception e) {
			throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
					e.getMessage());
		}
		return response;
	}
}
