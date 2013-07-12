package org.openiam.idm.parser.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.am.srvc.constants.UserFields;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.stereotype.Service;

@Service
public class ProvisionUserCSVParser extends
		AbstractCSVParser<ProvisionUser, UserFields> implements
		CSVParser<ProvisionUser> {

	@Override
	public ReconciliationObject<ProvisionUser> toReconciliationObject(
			ProvisionUser pu, List<AttributeMapEntity> attrMap) {
		return this.toReconciliationObject(pu, attrMap, UserFields.class);
	}

	@Override
	protected void putValueInDTO(ProvisionUser user, UserFields field,
			String objValue) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		switch (field) {
		case birthdate:
			try {
				user.setBirthdate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setBirthdate(null);
			}
			break;
		case companyOwnerId:
			user.setCompanyOwnerId(objValue);
			break;
		case createDate:
			try {
				user.setCreateDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setCreateDate(null);
			}
			break;
		case createdBy:
			user.setCreatedBy(objValue);
			break;
		case employeeId:
			user.setEmployeeId(objValue);
			break;
		case employeeType:
			user.setEmployeeType(objValue);
			break;
		case firstName:
			user.setFirstName(objValue);
			break;
		case jobCode:
			user.setJobCode(objValue);
			break;
		case lastName:
			user.setLastName(objValue);
			break;
		case lastUpdate:
			try {
				user.setLastUpdate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setLastUpdate(null);
			}
			break;
		case lastUpdatedBy:
			user.setLastUpdatedBy(objValue);
			break;
		case locationCd:
			user.setLocationCd(objValue);
			break;
		case locationName:
			user.setLocationName(objValue);
			break;
		case managerId:
			user.setManagerId(objValue);
			break;
		case metadataTypeId:
			user.setMetadataTypeId(objValue);
			break;
		case classification:
			user.setClassification(objValue);
			break;
		case middleInit:
			user.setMiddleInit(objValue);
			break;
		case prefix:
			user.setPrefix(objValue);
			break;
		case sex:
			user.setSex(objValue);
			break;
		case status:
			user.setStatus(Enum.valueOf(UserStatusEnum.class,
					objValue.toUpperCase()));
			break;
		case secondaryStatus:
			user.setSecondaryStatus(Enum.valueOf(UserStatusEnum.class,
					objValue.toUpperCase()));
			break;
		case suffix:
			user.setSuffix(objValue);
			break;
		case title:
			user.setTitle(objValue);
			break;
		case uid:
		case userId:
			user.setUserId(objValue);
			break;
		case userTypeInd:
			user.setUserTypeInd(objValue);
			break;
		case userNotes:
		case emailAddresses:
		case userAttributes:
			break;
		case costCenter:
			user.setCostCenter(objValue);
			break;
		case startDate:
			try {
				user.setStartDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setStartDate(null);
			}
			break;
		case lastDate:
			try {
				user.setLastDate(sdf.parse(objValue));
			} catch (Exception e) {
				user.setLastDate(null);
			}
			break;
		case mailCode:
			user.setMailCode(objValue);
			break;
		case nickname:
			user.setNickname(objValue);
			break;
		case maidenName:
			user.setMaidenName(objValue);
			break;
		case passwordTheme:
			user.setPasswordTheme(objValue);
			break;
		case mail:
		case email:
		case emailAddress:
			user.setEmail(objValue);
			break;
		case showInSearch:
			try {
				user.setShowInSearch(Integer.valueOf(objValue));
			} catch (Exception e) {
				user.setShowInSearch(null);
			}
			break;
		case principalList:
		case phones:
		case supervisor:
			break;
		case alternateContactId:
			user.setAlternateContactId(objValue);
			break;
		case securityDomain:
			user.setSecurityDomain(objValue);
			break;
		case userOwnerId:
			user.setUserOwnerId(objValue);
			break;
		case datePasswordChanged:
			try {
				user.setDatePasswordChanged(sdf.parse(objValue));
			} catch (Exception e) {
				user.setDatePasswordChanged(null);
			}
			break;
        case dateITPolicyApproved:
            try {
                user.setDateITPolicyApproved(sdf.parse(objValue));
            } catch (Exception e) {
                user.setDateITPolicyApproved(null);
            }
            break;
		case dateChallengeRespChanged:
			try {
				user.setDateChallengeRespChanged(sdf.parse(objValue));
			} catch (Exception e) {
				user.setDateChallengeRespChanged(null);
			}
			break;
		case DEFAULT:
			break;
		default:
			break;

		}

	}

	@Override
	protected String putValueIntoString(ProvisionUser user, UserFields field) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String objValue = "";
		switch (field) {
		case birthdate:
			objValue = user.getBirthdate() == null ? "" : toString(sdf
					.format(user.getBirthdate()));
			break;
		case companyOwnerId:
			objValue = toString(user.getCompanyOwnerId());
			break;
		case createDate:
			objValue = user.getCreateDate() == null ? "" : toString(sdf
					.format(user.getCreateDate()));
			break;
		case createdBy:
			objValue = toString(user.getCreatedBy());
			break;
		case employeeId:
			objValue = toString(user.getEmployeeId());
			break;
		case employeeType:
			objValue = toString(user.getEmployeeType());
			break;
		case firstName:
			objValue = toString(user.getFirstName());
			break;
		case jobCode:
			objValue = toString(user.getJobCode());
			break;
		case lastName:
			objValue = toString(user.getLastName());
			break;
		case lastUpdate:
			objValue = user.getLastUpdate() == null ? "" : toString(sdf
					.format(user.getLastUpdate()));
			break;
		case lastUpdatedBy:
			objValue = toString(user.getLastUpdatedBy());
			break;
		case locationCd:
			objValue = toString(user.getLocationCd());
			break;
		case locationName:
			objValue = toString(user.getLocationName());
			break;
		case managerId:
			objValue = toString(user.getManagerId());
			break;
		case metadataTypeId:
			objValue = toString(user.getMetadataTypeId());
			break;
		case classification:
			objValue = toString(user.getClassification());
			break;
		case middleInit:
			objValue = toString(user.getMiddleInit());
			break;
		case prefix:
			objValue = toString(user.getPrefix());
			break;
		case sex:
			objValue = toString(user.getSex());
			break;
		case status:
			objValue = toString(user.getStatus());
			break;
		case secondaryStatus:
			objValue = toString(user.getSecondaryStatus());
			break;
		case suffix:
			objValue = toString(user.getSuffix());
			break;
		case title:
			objValue = toString(user.getTitle());
			break;
		case uid:
		case userId:
			objValue = toString(user.getUserId());
			break;
		case userTypeInd:
			objValue = toString(user.getUserTypeInd());
			break;
		case costCenter:
			objValue = toString(user.getCostCenter());
			break;
		case startDate:
			objValue = user.getStartDate() == null ? "" : toString(sdf
					.format(user.getStartDate()));
			break;
		case lastDate:
			objValue = user.getLastDate() == null ? "" : toString(sdf
					.format(user.getLastDate()));
			break;
		case mailCode:
			objValue = toString(user.getMailCode());
			break;
		case nickname:
			objValue = toString(user.getNickname());
			break;
		case maidenName:
			objValue = toString(user.getMaidenName());
			break;
		case passwordTheme:
			objValue = toString(user.getPasswordTheme());
			break;
		case mail:
		case email:
		case emailAddress:
			objValue = toString(user.getEmail());
			break;
		case showInSearch:
			objValue = toString(user.getShowInSearch());
			break;
		case alternateContactId:
			objValue = toString(user.getAlternateContactId());
			break;
		case securityDomain:
			objValue = toString(user.getSecurityDomain());
			break;
		case userOwnerId:
			objValue = toString(user.getUserOwnerId());
			break;
		case datePasswordChanged:
			objValue = user.getDatePasswordChanged() == null ? ""
					: toString(sdf.format(user.getDatePasswordChanged()));
			break;
        case dateITPolicyApproved:
            objValue = user.getDateITPolicyApproved() == null ? ""
                    : toString(sdf.format(user.getDateITPolicyApproved()));
            break;
		case dateChallengeRespChanged:
			objValue = user.getDateChallengeRespChanged() == null ? ""
					: toString(sdf.format(user.getDateChallengeRespChanged()));
			break;
		case userAttributes:
		case principalList:
		case supervisor:
		case userNotes:
		case phones:
		case emailAddresses:
			break;
		case DEFAULT:
			objValue = toString("");
			break;
		default:
			break;
		}
		return objValue;
	}

	@Override
	public void add(ReconciliationObject<ProvisionUser> newObject,
			ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
			CSVSource source) throws Exception {
		appendObjectToCSV(newObject, managedSys, attrMapList,
				ProvisionUser.class, UserFields.class, true, source);
	}

	@Override
	public void delete(String principal, ManagedSysEntity managedSys,
			List<AttributeMapEntity> attrMapList, CSVSource source)
			throws Exception {
		List<ReconciliationObject<ProvisionUser>> users = this.getObjects(
				managedSys, attrMapList, source);
		Iterator<ReconciliationObject<ProvisionUser>> userIter = users
				.iterator();
		while (userIter.hasNext()) {
			ReconciliationObject<ProvisionUser> user = userIter.next();
			if (principal != null) {
				if (principal.equals(user.getPrincipal())) {
					userIter.remove();
				}
			}
		}
		updateCSV(users, managedSys, attrMapList, ProvisionUser.class,
				UserFields.class, false, source);
	}

	@Override
	public void update(ReconciliationObject<ProvisionUser> newUser,
			ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
			CSVSource source) throws Exception {
		List<ReconciliationObject<ProvisionUser>> users = this.getObjects(
				managedSys, attrMapList, source);
		List<ReconciliationObject<ProvisionUser>> newUsers = new ArrayList<ReconciliationObject<ProvisionUser>>(
				0);
		for (ReconciliationObject<ProvisionUser> user : users) {
			if (newUser.getPrincipal().equals(user.getPrincipal())) {
				newUsers.add(newUser);
			} else {
				newUsers.add(user);
			}
		}
		updateCSV(newUsers, managedSys, attrMapList, ProvisionUser.class,
				UserFields.class, false, source);
	}

	@Override
	public Map<String, String> convertToMap(List<AttributeMapEntity> attrMap,
			ReconciliationObject<ProvisionUser> obj) {
		return super.convertToMap(attrMap, obj, UserFields.class);
	}

	@Override
	public List<ReconciliationObject<ProvisionUser>> getObjects(
			ManagedSysEntity managedSys, List<AttributeMapEntity> attrMapList,
			CSVSource source) throws Exception {
		return getObjectList(managedSys, attrMapList, ProvisionUser.class,
				UserFields.class, source);
	}

	@Override
	public String getFileName(ManagedSysEntity mngSys, CSVSource source) {
		return super.getFileName(mngSys, source);
	}

	@Override
	public String objectToString(List<String> head, Map<String, String> obj) {
		StringBuilder stb = new StringBuilder();
		for (String h : head) {
			stb.append(obj.get(h.trim()) == null ? "" : obj.get(h));
			stb.append(",");
		}
		stb.deleteCharAt(stb.length() - 1);
		return stb.toString();
	}

	@Override
	public String objectToString(List<String> head,
			List<AttributeMapEntity> attrMapList,
			ReconciliationObject<ProvisionUser> u) {
		return this.objectToString(head, this.convertToMap(attrMapList, u));
	}

	@Override
	public Map<String, String> matchFields(List<AttributeMapEntity> attrMap,
			ReconciliationObject<ProvisionUser> u,
			ReconciliationObject<ProvisionUser> o) {
		Map<String, String> res = new HashMap<String, String>(0);
		Map<String, String> one = this.convertToMap(attrMap, u);
		Map<String, String> two = this.convertToMap(attrMap, o);
		for (String field : one.keySet()) {

			if (one.get(field) == null && two.get(field) == null) {
				res.put(field, null);
				continue;
			}
			if (one.get(field) == null && two.get(field) != null) {
				res.put(field, two.get(field));
				continue;
			}
			if (one.get(field) != null && two.get(field) == null) {
				res.put(field, one.get(field));
				continue;
			}
			if (one.get(field) != null && two.get(field) != null) {
				String firstVal = one.get(field).replaceFirst("^0*", "").trim();
				String secondVal = two.get(field).replaceFirst("^0*", "")
						.trim();
				res.put(field, firstVal.equalsIgnoreCase(secondVal) ? secondVal
						: ("[" + firstVal + "][" + secondVal + "]"));
				continue;
			}
		}

		return res;
	}

}
