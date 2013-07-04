package org.openiam.bpm.activiti.delegate.user.newuser.displaymapper;

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
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class NewUserDisplayMapperDelegate implements JavaDelegate {
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;
	
	@Autowired
	private RoleDataService roleDataService;

	public NewUserDisplayMapperDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = (String)execution.getVariable(ActivitiConstants.PROVISION_REQUEST_ID);
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel request = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		if(request != null) {
			final User user = request.getUser();
			if(user != null) {
				if(StringUtils.isNotBlank(user.getFirstName())) {
					metadataMap.put("First Name", user.getFirstName());
				}
				if(StringUtils.isNotBlank(user.getLastName())) {
					metadataMap.put("Last Name", user.getLastName());
				}
				if(StringUtils.isNotBlank(user.getMiddleInit())) {
					metadataMap.put("Middle Name", user.getMiddleInit());
				}
				if(StringUtils.isNotBlank(user.getMaidenName())) {
					metadataMap.put("Maiden Name", user.getMaidenName());
				}
				if(StringUtils.isNotBlank(user.getNickname())) {
					metadataMap.put("Nick Name", user.getNickname());
				}
				if(user.getBirthdate() != null) {
					metadataMap.put("Date of Birth", new SimpleDateFormat("MMMM dd yyyy").format(user.getBirthdate()));
				}
				if(StringUtils.isNotBlank(user.getTitle())) {
					metadataMap.put("Functional Title", user.getTitle());
				}
				if(StringUtils.isNotBlank(user.getSex())) {
					metadataMap.put("Gender", user.getSex());
				}
			}
			
			final List<Address> addresses = request.getAddresses();
			if(CollectionUtils.isNotEmpty(addresses)) {
				for(int i = 0; i < addresses.size(); i++) {
					final Address address = addresses.get(i);
					if(address != null) {
						final String str = toString(address);
						if(StringUtils.isNotBlank(str)) {
							metadataMap.put(String.format("Address %s", i), str);
						}
					}
				}
			}
			
			final List<Phone> phones = request.getPhones();
			if(CollectionUtils.isNotEmpty(phones)) {
				for(int i = 0; i < phones.size(); i++) {
					final Phone phone = phones.get(i);
					if(phone != null) {
						final String str = toString(phone);
						if(StringUtils.isNotBlank(str)) {
							metadataMap.put(String.format("Phone %s", i), str);
						}
					}
				}
			}
				
			
			final List<EmailAddress> emails = request.getEmails();
			if(CollectionUtils.isNotEmpty(emails)) {
				for(int i = 0; i < emails.size(); i++) {
					final EmailAddress email = emails.get(i);
					if(email != null) {
						final String str = toString(email);
						if(StringUtils.isNotBlank(str)) {
							metadataMap.put(String.format("Email %s", i), str);
						}
					}
				}
			}
			
			final List<Login> logins = request.getLoginList();
			if(CollectionUtils.isNotEmpty(logins)) {
				for(int i = 0; i < logins.size(); i++) {
					final Login login = logins.get(i);
					if(login != null) {
						final String str = toString(login);
						if(StringUtils.isNotBlank(str)) {
							metadataMap.put(String.format("Login %s", i), str);
						}
					}
				}
			}
			
			final PageTempate template = request.getPageTemplate();
			if(template != null) {
				metadataMap.putAll(toElementMap(template));
			}
			
			final String userId = (String)execution.getVariable(ActivitiConstants.TASK_OWNER);
			if(StringUtils.isNotBlank(userId)) {
				final User requestor = userDataService.getUserDto(userId);
				if(requestor != null) {
					metadataMap.put("Reqeustor", requestor.getDisplayName());
				}
			}
			
			final List<String> roleIds = request.getRoleIds();
			if(CollectionUtils.isNotEmpty(roleIds)) {
				for(final String roleId : roleIds) {
					if(StringUtils.isNotBlank(roleId)) {
						final RoleEntity role = roleDataService.getRole(roleId);
						if(role != null) {
							metadataMap.put("Role", role.getRoleName());
						}
					}
				}
			}
		}
	
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP, metadataMap);
	}

	private String toString(final Address address) {
		final StringBuilder sb = new StringBuilder();
		if(address != null) {
			if(StringUtils.isNotBlank(address.getBldgNumber())) {
				sb.append(address.getBldgNumber()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getAddress1())) {
				sb.append(address.getAddress1()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getAddress2())) {
				sb.append(address.getAddress2()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getCity())) {
				sb.append(address.getCity()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getState())) {
				sb.append(address.getState()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getCountry())) {
				sb.append(address.getCountry()).append(" ");
			}
			if(StringUtils.isNotBlank(address.getPostalCd())) {
				sb.append(address.getPostalCd()).append(" ");
			}
		}
		return sb.toString();
	}
	
	private String toString(final Phone phone) {
		final StringBuilder sb = new StringBuilder();
		if(phone != null) {
			if(StringUtils.isNotBlank(phone.getCountryCd())) {
				sb.append(String.format("+%s ", phone.getCountryCd()));
			}
			if(StringUtils.isNotBlank(phone.getAreaCd())) {
				sb.append(String.format("(%s) ", phone.getAreaCd()));
			}
			if(StringUtils.isNotBlank(phone.getPhoneNbr())) {
				sb.append(phone.getPhoneNbr()).append(" ");
			}
			if(StringUtils.isNotBlank(phone.getPhoneExt())) {
				sb.append(phone.getPhoneExt()).append(" ");
			}
		}
		return sb.toString();
	}
	
	private String toString(final EmailAddress email) {
		return StringUtils.trimToNull(email.getEmailAddress());
	}
	
	private String toString(final Login login) {
		return StringUtils.trimToNull(login.getLogin());
	}
	
	private LinkedHashMap<String, String> toElementMap(final PageTempate template) {
		final LinkedHashMap<String, String> retVal = new LinkedHashMap<String, String>();
		if(template != null) {
			if(CollectionUtils.isNotEmpty(template.getPageElements())) {
				for(final PageElement pageElement : template.getPageElements()) {
					if(StringUtils.isNotBlank(pageElement.getDisplayName()) && CollectionUtils.isNotEmpty(pageElement.getUserValues())) {
						final String str = toString(pageElement.getUserValues());
						if(StringUtils.isNotBlank(str)) {
							retVal.put(pageElement.getDisplayName(), str);
						}
					}
				}
			}
		}
		return retVal;
	}
	
	private String toString(final Set<PageElementValue> userValues) {
		final StringBuilder sb = new StringBuilder();
		final List<PageElementValue> values = new LinkedList<PageElementValue>();
		for(final PageElementValue elementValue : userValues) {
			if(elementValue != null && StringUtils.isNotBlank(elementValue.getValue())) {
				values.add(elementValue);
			}
		}
		for(int i = 0; i < values.size(); i++) {
			final PageElementValue value = values.get(i);
			sb.append(value.getValue());
			if(i < values.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
