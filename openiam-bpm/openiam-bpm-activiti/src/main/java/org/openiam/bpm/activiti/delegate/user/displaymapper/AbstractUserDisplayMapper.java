package org.openiam.bpm.activiti.delegate.user.displaymapper;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractUserDisplayMapper extends AbstractActivitiJob {
	
	@Autowired
	protected RoleDataService roleDataService;
	
	@Autowired
	protected GroupDataService groupDataService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected UserDataService userDataService;
	
	public AbstractUserDisplayMapper() {
		super();
	}

	public LinkedHashMap<String, String> getMetadataMap(final UserProfileRequestModel request, final DelegateExecution execution) {
		final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
		if(request != null && request.getUser() != null) {
			final User user = request.getUser();
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
			
			if(StringUtils.isNotBlank(user.getAlternateContactId())) {
				final UserEntity alternateContact = userDataService.getUser(user.getAlternateContactId());
				if(alternateContact != null) {
					metadataMap.put("Alternate Contact", alternateContact.getDisplayName());
				}
			}
			
			if(StringUtils.isNotBlank(user.getClassification())) {
				metadataMap.put("Classification", user.getClassification());
			}
			
			if(StringUtils.isNotBlank(user.getCostCenter())) {
				metadataMap.put("Cost Center", user.getCostCenter());
			}
			
			if(StringUtils.isNotBlank(user.getEmployeeId())) {
				metadataMap.put("Employee ID", user.getEmployeeId());
			}
			
			if(StringUtils.isNotBlank(user.getEmployeeTypeId())) {
				metadataMap.put("Employee Type", user.getEmployeeTypeId());
			}
			
			if(user.getStartDate() != null) {
				metadataMap.put("Start Date", new SimpleDateFormat("MMMM dd yyyy").format(user.getStartDate()));
			}
			
			if(user.getLastDate() != null) {
				metadataMap.put("End Date", new SimpleDateFormat("MMMM dd yyyy").format(user.getLastDate()));
			}

			if(user.getJobCodeId()!=null) {
				metadataMap.put("Job Code", user.getJobCodeId());
			}

			if(StringUtils.isNotBlank(user.getLocationCd())) {
				metadataMap.put("Location Code", user.getLocationCd());
			}

			if(StringUtils.isNotBlank(user.getLocationName())) {
				metadataMap.put("Location Name", user.getLocationName());
			}

			if(StringUtils.isNotBlank(user.getMailCode())) {
				metadataMap.put("Mail Code", user.getMailCode());
			}

			if(StringUtils.isNotBlank(user.getMetadataTypeId())) {
				metadataMap.put("Object Class", user.getMetadataTypeId());
			}

			if(StringUtils.isNotBlank(user.getPrefix())) {
				metadataMap.put("Prefix", user.getPrefix());
			}
			
			if(StringUtils.isNotBlank(user.getSuffix())) {
				metadataMap.put("Suffix", user.getSuffix());
			}

			if(user.getStatus() != null) {
				metadataMap.put("User Status", user.getStatus().getValue());
			}
			
			if(user.getSecondaryStatus() != null) {
				metadataMap.put("Account Status", user.getSecondaryStatus().getValue());
			}
			
			if(StringUtils.isNotBlank(user.getTitle())) {
				metadataMap.put("Title", user.getTitle());
			}
			
			if(StringUtils.isNotBlank(user.getUserTypeInd())) {
				metadataMap.put("User Type", user.getUserTypeInd());
			}
		
			if(MapUtils.isNotEmpty(user.getUserAttributes())) {
				for(final UserAttribute attribute : user.getUserAttributes().values()) {
					if(StringUtils.isNotBlank(attribute.getName())) {
						List<String> values = new LinkedList<>();
						if(Boolean.TRUE.equals(attribute.getIsMultivalued())) {
							values = attribute.getValues();
						} else {
							values.add(attribute.getValue());
						}
						if(CollectionUtils.isNotEmpty(values)) {
							metadataMap.put(attribute.getName(), values.toString());
						}
					}
				}
			}
		}
		
		final List<Address> addresses = request.getAddresses();
		if(CollectionUtils.isNotEmpty(addresses)) {
			for(int i = 0; i < addresses.size(); i++) {
				final Address address = addresses.get(i);
				if(address != null) {
					final String str = toString(address);
					if(StringUtils.isNotBlank(str)) {
						metadataMap.put(String.format("Address %s", i + 1), str);
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
						metadataMap.put(String.format("Phone %s", i + 1), str);
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
						metadataMap.put(String.format("Email %s", i + 1), str);
					}
				}
			}
		}
		
		final PageTempate template = request.getPageTemplate();
		if(template != null) {
			metadataMap.putAll(toElementMap(template));
		}
		
		final String userId = getRequestorId(execution);
		if(StringUtils.isNotBlank(userId)) {
			final User requestor = userDataService.getUserDto(userId);
			if(requestor != null) {
				metadataMap.put("Requestor", requestor.getDisplayName());
			}
		}
		
		return metadataMap;
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
