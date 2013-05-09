package org.openiam.spml2.spi.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.constants.CSVSource;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.parser.csv.CSVParser;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.recon.command.ReconciliationCommandFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.recon.report.ReconciliationReport;
import org.openiam.idm.srvc.recon.report.ReconciliationReportResults;
import org.openiam.idm.srvc.recon.report.ReconciliationReportRow;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserMgr;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

public class AbstractCSVCommand {
	protected static final Log log = LogFactory
			.getLog(AbstractCSVCommand.class);
	@Autowired
	protected ManagedSystemService managedSysService;
	@Autowired
	protected ResourceService resourceDataService;
	@Autowired
	@Qualifier("userCSVParser")
	protected CSVParser<User> userCSVParser;
	@Autowired
	private MailService mailService;
	// @Autowired
	// @Qualifier("userManager")
	// FIXME
	private UserMgr userManager;
	@Autowired
	private UserDozerConverter userDozerConverter;
	@Autowired
	private OrganizationDataService orgManager;
	@Value("${iam.files.location}")
	protected String pathToCSV;

	// public static ApplicationContext ac;

	@Deprecated
	// TODO REMOVE THIS, WHEN UI will be implemented FULLY
	public void improveFile(String pathToFile) throws Exception {
		// ScriptIntegration se = ScriptFactory.createModule(this.scriptEngine);
		// CSVImproveScript script = (CSVImproveScript)
		// se.instantiateClass(null,
		// "/recon/ImproveScript.groovy");
		// script.execute(pathToFile, orgManager.getAllOrganizations());
	}

	protected char getSeparator(ReconciliationConfig config) {
		char separator;

		if (StringUtils.hasText(config.getSeparator()))
			separator = config.getSeparator().toCharArray()[0];
		else
			separator = ',';
		return separator;
	}

	protected char getEndOfLine(ReconciliationConfig config) {
		char EOL;
		if (StringUtils.hasText(config.getEndOfLine()))
			EOL = config.getEndOfLine().toCharArray()[0];
		else
			EOL = '\n';
		return EOL;
	}

	protected ResponseType reconcile(ReconciliationConfig config) {
		long c = Calendar.getInstance().getTimeInMillis();
		ResponseType response = new ResponseType();
		ReconciliationReport result = new ReconciliationReport();
		List<ReconciliationReportRow> report = new ArrayList<ReconciliationReportRow>();
		StringBuilder message = new StringBuilder();
		result.setReport(report);
		ResourceEntity res = resourceDataService.findResourceById(config
				.getResourceId());
		String managedSysId = res.getManagedSysId();
		ManagedSysEntity mSys = managedSysService
				.getManagedSysById(managedSysId);

		Map<String, ReconciliationCommand> situations = new HashMap<String, ReconciliationCommand>();
		for (ReconciliationSituation situation : config.getSituationSet()) {
			situations.put(situation.getSituation().trim(),
					ReconciliationCommandFactory.createCommand(
							situation.getSituationResp(), situation,
							managedSysId));
			log.debug("Created Command for: " + situation.getSituation());
		}
		List<User> users = userDozerConverter.convertToDTOList(
				userManager.getUsersForMSys("0"), true);
		if (users == null) {
			log.error("user list from DB is empty");
			response.setStatus(StatusCodeType.FAILURE);
			response.setErrorMessage("user list from DB is empty");
			return response;
		}
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(mSys.getResourceId());
		if (CollectionUtils.isEmpty(attrMapList)) {
			log.error("user list from DB is empty");
			response.setStatus(StatusCodeType.FAILURE);
			response.setErrorMessage("attrMapList is empty");
			return response;
		}
		List<ReconciliationObject<User>> idmUsers = null;
		List<ReconciliationObject<User>> sourceUsers = null;
		List<ReconciliationObject<User>> dbUsers = new ArrayList<ReconciliationObject<User>>();
		for (User u : users) {
			dbUsers.add(userCSVParser.toReconciliationObject(u, attrMapList));
		}

		try {
			idmUsers = userCSVParser.getObjects(mSys, attrMapList,
					CSVSource.IDM);

			// Improve uploaded file
			improveFile(userCSVParser.getFileName(mSys, CSVSource.UPLOADED));
			sourceUsers = userCSVParser.getObjects(mSys, attrMapList,
					CSVSource.UPLOADED);
			List<String> hList = ReconciliationReport.getHeader(attrMapList);
			report.add(new ReconciliationReportRow(attrMapList));
			// First run from IDM search in Sourse
			report.add(new ReconciliationReportRow("Records from IDM: "
					+ idmUsers.size() + " items", hList.size() + 1));
			try {
				log.debug("First cycle");
				dbUsers.removeAll(reconCicle(hList, report, "IDM: ", idmUsers,
						dbUsers, attrMapList, mSys));
			} catch (Exception e) {
				log.error(e.getMessage());
				response.setStatus(StatusCodeType.FAILURE);
				response.setErrorMessage(e.getMessage());
				message.append("ERROR:" + response.getErrorMessage());
			}
			report.add(new ReconciliationReportRow("Records from Remote CSV: "
					+ sourceUsers.size() + " items", hList.size() + 1));
			try {
				log.debug("Second cycle");
				dbUsers.removeAll(reconCicle(hList, report, "Source: ",
						sourceUsers, dbUsers, attrMapList, mSys));
			} catch (Exception e) {
				log.error(e.getMessage());
				response.setStatus(StatusCodeType.FAILURE);
				response.setErrorMessage(e.getMessage());
				message.append("ERROR:" + response.getErrorMessage());
			}
			report.add(new ReconciliationReportRow("Records from DB: "
					+ dbUsers.size() + " items", hList.size() + 1));
			for (ReconciliationObject<User> obj : dbUsers) {
				String login = "";
				if (!CollectionUtils
						.isEmpty(obj.getObject().getPrincipalList())) {
					login = obj.getObject().getPrincipalList().get(0)
							.getLogin();
				}
				report.add(new ReconciliationReportRow(login, "DB: ",
						ReconciliationReportResults.NOT_EXIST_IN_RESOURCE, this
								.objectToString(hList, attrMapList, obj)));
			}

			// -----------------------------------------------
		} catch (Exception e) {
			log.error(e);
			response.setStatus(StatusCodeType.FAILURE);
			response.setErrorMessage(e.getMessage() + e.getStackTrace());
			message.append(response.getErrorMessage());
		}
		try {
			result.save(pathToCSV, mSys);
		} catch (IOException e) {
			log.error("can't save report");
			response.setStatus(StatusCodeType.FAILURE);
			response.setErrorMessage(e.getMessage() + e.getStackTrace());
			message.append("ERROR:" + response.getErrorMessage());
		}
		c = Calendar.getInstance().getTimeInMillis() - c;
		log.debug("RECONCILIATION TIME:" + c
				+ "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		if (StringUtils.hasText(config.getNotificationEmailAddress())) {
			message.append("Resource: " + res.getName() + ".\n");
			message.append("Uploaded CSV file: " + mSys.getResourceId()
					+ ".csv was successfully reconciled.\n");
			message.append("Totals:\n");
			message.append("Records from IDM: " + idmUsers.size() + " items.\n");
			message.append("Records from Remote CSV: " + sourceUsers.size()
					+ " items.\n");
			message.append("Records from DB: " + dbUsers.size() + " items.\n");
			message.append("Reconciliation time: " + c + "ms.\n");
			mailService.sendEmails(
					null,
					new String[] { config.getNotificationEmailAddress() },
					null,
					null,
					"CSVConnector",
					message.toString(),
					false,
					new String[] {
							pathToCSV + "report_" + mSys.getResourceId()
									+ ".html",
							pathToCSV + "report_" + mSys.getResourceId()
									+ ".csv" });
		}
		return response;
	}

	private String objectToString(List<String> head, Map<String, String> obj) {
		return userCSVParser.objectToString(head, obj);
	}

	private String objectToString(List<String> head,
			List<AttributeMapEntity> attrMapList, ReconciliationObject<User> u) {
		return userCSVParser.objectToString(head, attrMapList, u);
	}

	private Map<String, String> matchFields(List<AttributeMapEntity> attrMap,
			ReconciliationObject<User> u, ReconciliationObject<User> o) {
		return userCSVParser.matchFields(attrMap, u, o);
	}

	/**
	 * 
	 * @param hList
	 * @param report
	 * @param preffix
	 * @param reconUserList
	 * @param dbUsers
	 * @param attrMapList
	 * @param mSys
	 * @return
	 * @throws Exception
	 */
	private Set<ReconciliationObject<User>> reconCicle(List<String> hList,
			List<ReconciliationReportRow> report, String preffix,
			List<ReconciliationObject<User>> reconUserList,
			List<ReconciliationObject<User>> dbUsers,
			List<AttributeMapEntity> attrMapList, ManagedSysEntity mSys)
			throws Exception {
		Set<ReconciliationObject<User>> used = new HashSet<ReconciliationObject<User>>(
				0);
		for (ReconciliationObject<User> u : reconUserList) {
			log.info("User " + u.toString());
			if (u.getObject() == null || u.getPrincipal() == null) {
				log.warn("Skip USER" + u.toString() + " key or objecy is NULL");
				if (u.getObject() != null) {
					report.add(new ReconciliationReportRow(preffix,
							ReconciliationReportResults.BROKEN_CSV, this
									.objectToString(hList, attrMapList, u)));
				}
				continue;
			}

			// if (!isUnique(u, reconUserList)) {
			// report.add(new ReconciliationHTMLRow(preffix,
			// ReconciliationHTMLReportResults.NOT_UNIQUE_KEY, this
			// .objectToString(hList,
			// csvParser.convertToMap(attrMapList, u))));
			// continue;
			// }
			boolean isFind = false;
			boolean isMultiple = false;
			ReconciliationObject<User> finded = null;
			for (ReconciliationObject<User> o : dbUsers) {
				if (used.contains(o)) {
					log.debug("already used");
					continue;
				}
				if (!StringUtils.hasText(o.getPrincipal())) {
					used.add(o);
					continue;
				}
				if (o.getPrincipal().replaceFirst("^0*", "")
						.equals(u.getPrincipal().replaceFirst("^0*", ""))) {
					if (!isFind) {
						isFind = true;
						finded = o;
						used.add(finded);
						continue;
					} else {
						isMultiple = true;
						report.add(new ReconciliationReportRow(preffix,
								ReconciliationReportResults.NOT_UNIQUE_KEY,
								this.objectToString(hList, attrMapList, u)));
						break;
					}
				}
			}

			if (!isFind) {
				report.add(new ReconciliationReportRow(preffix,
						ReconciliationReportResults.NOT_EXIST_IN_IDM_DB, this
								.objectToString(hList, attrMapList, u)));
			} else if (!isMultiple && finded != null) {
				if (finded.getObject().getPrincipalList().get(0) == null) {
					if (UserStatusEnum.DELETED.equals(finded.getObject()
							.getStatus())) {
						report.add(new ReconciliationReportRow(preffix,
								ReconciliationReportResults.IDM_DELETED, this
										.objectToString(hList, userCSVParser
												.convertToMap(attrMapList, u))));
						continue;
					}
					report.add(new ReconciliationReportRow(preffix,
							ReconciliationReportResults.LOGIN_NOT_FOUND, this
									.objectToString(hList, userCSVParser
											.convertToMap(attrMapList, u))));
					continue;
				} else {
					report.add(new ReconciliationReportRow(finded.getObject()
							.getPrincipalList().get(0).getLogin(), preffix,
							ReconciliationReportResults.MATCH_FOUND,
							this.objectToString(hList,
									matchFields(attrMapList, u, finded))));
					continue;
				}
				// FIXME fix login cheking
				// Login l = null;
				// List<Login> logins = loginManager.getLoginByUser(finded
				// .getObject().getUserId());
				// if (logins != null) {
				// for (Login login : logins) {
				// if (login.getId().getDomainId()
				// .equalsIgnoreCase(mSys.getDomainId())
				// ) {
				// l = login;
				// break;
				// }
				// }
				// }
				// if (l == null) {
				// if (UserStatusEnum.DELETED.equals(finded.getObject()
				// .getStatus())) {
				// report.add(new ReconciliationHTMLRow(preffix,
				// ReconciliationHTMLReportResults.IDM_DELETED,
				// this.objectToString(hList,
				// csvParser.convertToMap(attrMapList, u))));
				// continue;
				// }
				// report.add(new ReconciliationHTMLRow(preffix,
				// ReconciliationHTMLReportResults.LOGIN_NOT_FOUND,
				// this.objectToString(hList,
				// csvParser.convertToMap(attrMapList, u))));
				// continue;
				// } else {
				// report.add(new ReconciliationHTMLRow(preffix,
				// ReconciliationHTMLReportResults.MATCH_FOUND, this
				// .objectToString(
				// hList,
				// matchFields(csvParser.convertToMap(
				// attrMapList, u), csvParser
				// .convertToMap(attrMapList,
				// finded)))));
				// continue;
				// }
			}
		}
		return used;
	}

	protected List<ReconciliationObject<User>> getUsersFromCSV(
			ManagedSysEntity managedSys) throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		return userCSVParser.getObjects(managedSys, attrMapList, CSVSource.IDM);
	}

	protected Map<String, String> getUserProvisionMap(
			ReconciliationObject<User> obj, ManagedSysEntity managedSys)
			throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		return userCSVParser.convertToMap(attrMapList, obj);
	}

	protected void addUsersToCSV(String principal, User newUser,
			ManagedSysEntity managedSys) throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		userCSVParser.add(new ReconciliationObject<User>(principal, newUser),
				managedSys, attrMapList, CSVSource.IDM);
	}

	protected void deleteUser(String principal, User newUser,
			ManagedSysEntity managedSys) throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		userCSVParser.delete(principal, managedSys, attrMapList, CSVSource.IDM);
	}

	protected void updateUser(ReconciliationObject<User> newUser,
			ManagedSysEntity managedSys) throws Exception {
		List<AttributeMapEntity> attrMapList = managedSysService
				.getResourceAttributeMaps(managedSys.getResourceId());
		userCSVParser.update(newUser, managedSys, attrMapList, CSVSource.IDM);
	}

	protected boolean lookupObjectInCSV(String findValue,
			ManagedSysEntity managedSys, List<ExtensibleObject> extOnjectList)
			throws Exception {
		List<ReconciliationObject<User>> users = this
				.getUsersFromCSV(managedSys);
		List<ExtensibleAttribute> eAttr = new ArrayList<ExtensibleAttribute>(0);

		for (ReconciliationObject<User> user : users) {
			ExtensibleObject extOnject = new ExtensibleObject();
			if (match(findValue, user, extOnject)) {
				Map<String, String> res = this.getUserProvisionMap(user,
						managedSys);
				for (String key : res.keySet())
					if (res.get(key) != null)
						eAttr.add(new ExtensibleAttribute(key, user
								.getPrincipal()));
				extOnject.setAttributes(eAttr);
				extOnjectList.add(extOnject);
				return true;
			}
		}
		return false;
	}

	protected boolean match(String findValue, ReconciliationObject<User> user2,
			ExtensibleObject extOnject) {
		if (!StringUtils.hasText(findValue) || user2 == null) {
			return false;
		}
		if (findValue.equals(user2.getPrincipal())) {
			extOnject.setObjectId(user2.getPrincipal());
			return true;
		}
		return false;
	}

	/**
	 * @return the orgManager
	 */
	public OrganizationDataService getOrgManager() {
		return orgManager;
	}

	/**
	 * @param orgManager
	 *            the orgManager to set
	 */
	public void setOrgManager(OrganizationDataService orgManager) {
		this.orgManager = orgManager;
	}
}
