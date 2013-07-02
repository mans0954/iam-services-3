package org.openiam.bpm.activiti.delegate.user.edit.displaymapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class EditUserDisplayMapperDelegate implements JavaDelegate {
	
	@Autowired
	private UserDataService userDataService;

	public EditUserDisplayMapperDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final UserStatusEnum primaryStatus = UserStatusEnum.getFromString((String)execution.getVariable(ActivitiConstants.USER_STATUS));
		final UserStatusEnum secondaryStatus = UserStatusEnum.getFromString((String)execution.getVariable(ActivitiConstants.USER_SECONDARY_STATUS));
		final String userId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		
		final User user = userDataService.getUserDto(userId);
		
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		if(user != null) {
			if(primaryStatus != null) {
				metadataMap.put("Primary Status", primaryStatus.toString());
			}
			if(secondaryStatus != null) {
				metadataMap.put("Secondary Status", secondaryStatus.toString());
			}
		}
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP, metadataMap);
	}
}
