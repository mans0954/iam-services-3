package org.openiam.bpm.activiti.delegate.user.edit

import org.openiam.idm.srvc.user.dto.UserProfileRequestModel

import java.text.SimpleDateFormat

import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.MapUtils
import org.apache.commons.lang.StringUtils
import org.openiam.idm.srvc.continfo.dto.Address
import org.openiam.idm.srvc.continfo.dto.EmailAddress
import org.openiam.idm.srvc.continfo.dto.Phone
import org.openiam.idm.srvc.meta.dto.PageElement
import org.openiam.idm.srvc.meta.dto.PageElementValue
import org.openiam.idm.srvc.meta.dto.PageTempate
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel
import org.openiam.idm.srvc.user.dto.User
import org.openiam.idm.srvc.user.dto.UserAttribute
import org.openiam.idm.srvc.user.domain.UserEntity

def UserEntity toNotify = req.getNotificationParam("TO_NOTIFY")?.valueObj
def UserEntity targetUser = req.getNotificationParam("TARGET_USER")?.valueObj
def String requestName = req.getNotificationParam("REQUEST_REASON")?.valueObj
def String requestDescription = req.getNotificationParam("REQUEST_DESCRIPTION")?.valueObj
def String requestor = (req.getNotificationParam("REQUESTOR") != null) ? req.getNotificationParam("REQUESTOR")?.valueObj : null
def UserProfileRequestModel request = req.getNotificationParam("TARGET_REQUEST")?.valueObj




emailStr = "Dear " + toNotify.firstName + " " + toNotify.lastName + ": \n\n" +
        "The following request has been requested:\n\n" +
        "Request Name: " + requestName + "\n\n" +
        "Request Description: " + requestDescription + "\n\n"

StringBuilder sb = new StringBuilder();
LinkedHashMap<String, String> map = getMetadataMap(request);
for (String key : map.keySet()) {
    if (!map.get(key)?.isEmpty()) {
        sb.append(key);
        sb.append(": ");
        sb.append(map.get(key));
        sb.append("\n");
    }
}
emailStr = emailStr + sb.toString();

output = emailStr

public LinkedHashMap<String, String> getMetadataMap(final UserProfileRequestModel request) {
    println("^^^^^^^^^^^^^^ REQ:" + request);
    final LinkedHashMap<String, String> metadataMap = new LinkedHashMap<String, String>();
    if (request != null && request.getUser() != null) {
        final User user = request.getUser();
        if (StringUtils.isNotBlank(user.getFirstName())) {
            metadataMap.put("First Name", user.getFirstName());
        }
        if (StringUtils.isNotBlank(user.getLastName())) {
            metadataMap.put("Last Name", user.getLastName());
        }
        if (StringUtils.isNotBlank(user.getMiddleInit())) {
            metadataMap.put("Middle Name", user.getMiddleInit());
        }
        if (StringUtils.isNotBlank(user.getMaidenName())) {
            metadataMap.put("Maiden Name", user.getMaidenName());
        }
        if (StringUtils.isNotBlank(user.getNickname())) {
            metadataMap.put("Nick Name", user.getNickname());
        }
        if (user.getBirthdate() != null) {
            metadataMap.put("Date of Birth", new SimpleDateFormat("MMMM dd yyyy").format(user.getBirthdate()));
        }
        if (StringUtils.isNotBlank(user.getTitle())) {
            metadataMap.put("Functional Title", user.getTitle());
        }
        if (StringUtils.isNotBlank(user.getSex())) {
            metadataMap.put("Gender", user.getSex());
        }

        if (StringUtils.isNotBlank(user.getClassification())) {
            metadataMap.put("Classification", user.getClassification());
        }

        if (StringUtils.isNotBlank(user.getCostCenter())) {
            metadataMap.put("Cost Center", user.getCostCenter());
        }

        if (StringUtils.isNotBlank(user.getEmployeeId())) {
            metadataMap.put("Employee ID", user.getEmployeeId());
        }

        if (StringUtils.isNotBlank(user.getEmployeeTypeId())) {
            metadataMap.put("Employee Type", user.getEmployeeTypeId());
        }

        if (user.getStartDate() != null) {
            metadataMap.put("Start Date", new SimpleDateFormat("MMMM dd yyyy").format(user.getStartDate()));
        }

        if (user.getLastDate() != null) {
            metadataMap.put("End Date", new SimpleDateFormat("MMMM dd yyyy").format(user.getLastDate()));
        }

        if (user.getJobCodeId() != null) {
            metadataMap.put("Job Code", user.getJobCodeId());
        }

        if (StringUtils.isNotBlank(user.getLocationCd())) {
            metadataMap.put("Location Code", user.getLocationCd());
        }

        if (StringUtils.isNotBlank(user.getLocationName())) {
            metadataMap.put("Location Name", user.getLocationName());
        }

        if (StringUtils.isNotBlank(user.getMailCode())) {
            metadataMap.put("Mail Code", user.getMailCode());
        }

        if (StringUtils.isNotBlank(user.getMdTypeId())) {
            metadataMap.put("Metadata Type", user.getMdTypeId());
        }

        if (StringUtils.isNotBlank(user.getPrefix())) {
            metadataMap.put("Prefix", user.getPrefix());
        }

        if (StringUtils.isNotBlank(user.getSuffix())) {
            metadataMap.put("Suffix", user.getSuffix());
        }

        if (user.getStatus() != null) {
            metadataMap.put("User Status", user.getStatus().getValue());
        }

        if (user.getSecondaryStatus() != null) {
            metadataMap.put("Account Status", user.getSecondaryStatus().getValue());
        }

        if (StringUtils.isNotBlank(user.getTitle())) {
            metadataMap.put("Title", user.getTitle());
        }

        if (StringUtils.isNotBlank(user.getUserTypeInd())) {
            metadataMap.put("User Type", user.getUserTypeInd());
        }

        if (MapUtils.isNotEmpty(user.getUserAttributes())) {
            for (final UserAttribute attribute : user.getUserAttributes().values()) {
                if (StringUtils.isNotBlank(attribute.getName())) {
                    List<String> values = new LinkedList<>();
                    if (Boolean.TRUE.equals(attribute.getIsMultivalued())) {
                        values = attribute.getValues();
                    } else {
                        values.add(attribute.getValue());
                    }
                    if (CollectionUtils.isNotEmpty(values)) {
                        metadataMap.put(attribute.getName(), values.toString());
                    }
                }
            }
        }
        final List<Address> addresses = request.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            for (int i = 0; i < addresses.size(); i++) {
                final Address address = addresses.get(i);
                if (address != null) {
                    final String str = toString(address);
                    if (StringUtils.isNotBlank(str)) {
                        metadataMap.put(String.format("Address %s", i + 1), str);
                    }
                }
            }
        }

        final List<Phone> phones = request.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (int i = 0; i < phones.size(); i++) {
                final Phone phone = phones.get(i);
                if (phone != null) {
                    final String str = toString(phone);
                    if (StringUtils.isNotBlank(str)) {
                        metadataMap.put(String.format("Phone %s", i + 1), str);
                    }
                }
            }
        }


        final List<EmailAddress> emails = request.getEmails();
        if (CollectionUtils.isNotEmpty(emails)) {
            for (int i = 0; i < emails.size(); i++) {
                final EmailAddress email = emails.get(i);
                if (email != null) {
                    final String str = toString(email);
                    if (StringUtils.isNotBlank(str)) {
                        metadataMap.put(String.format("Email %s", i + 1), str);
                    }
                }
            }
        }

        final PageTempate template = request.getPageTemplate();
        if (template != null) {
            metadataMap.putAll(toElementMap(template));
        }
    }



    return metadataMap;
}

private String toString(final Address address) {
    final StringBuilder sb = new StringBuilder();
    if (address != null) {
        if (StringUtils.isNotBlank(address.getBldgNumber())) {
            sb.append(address.getBldgNumber()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getAddress1())) {
            sb.append(address.getAddress1()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getAddress2())) {
            sb.append(address.getAddress2()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getCity())) {
            sb.append(address.getCity()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getState())) {
            sb.append(address.getState()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getCountry())) {
            sb.append(address.getCountry()).append(" ");
        }
        if (StringUtils.isNotBlank(address.getPostalCd())) {
            sb.append(address.getPostalCd()).append(" ");
        }
    }
    return sb.toString();
}

private String toString(final Phone phone) {
    final StringBuilder sb = new StringBuilder();
    if (phone != null) {
        if (StringUtils.isNotBlank(phone.getCountryCd())) {
            sb.append(String.format("+%s ", phone.getCountryCd()));
        }
        if (StringUtils.isNotBlank(phone.getAreaCd())) {
            sb.append(String.format("(%s) ", phone.getAreaCd()));
        }
        if (StringUtils.isNotBlank(phone.getPhoneNbr())) {
            sb.append(phone.getPhoneNbr()).append(" ");
        }
        if (StringUtils.isNotBlank(phone.getPhoneExt())) {
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
    if (template != null) {
        if (CollectionUtils.isNotEmpty(template.getPageElements())) {
            for (final PageElement pageElement : template.getPageElements()) {
                if (StringUtils.isNotBlank(pageElement.getDisplayName()) && CollectionUtils.isNotEmpty(pageElement.getUserValues())) {
                    final String str = toString(pageElement.getUserValues());
                    if (StringUtils.isNotBlank(str)) {
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
    for (final PageElementValue elementValue : userValues) {
        if (elementValue != null && StringUtils.isNotBlank(elementValue.getValue())) {
            values.add(elementValue);
        }
    }
    for (int i = 0; i < values.size(); i++) {
        final PageElementValue value = values.get(i);
        sb.append(value.getValue());
        if (i < values.size() - 1) {
            sb.append(", ");
        }
    }
    return sb.toString();
}

