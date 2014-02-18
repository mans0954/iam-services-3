/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.batch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.batch.birt.ReportGenerator;
import org.openiam.idm.srvc.batch.constants.DeliveryMethod;
import org.openiam.idm.srvc.batch.constants.ReportFormat;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.report.dto.ReportInfoDto;
import org.openiam.idm.srvc.report.dto.ReportSubCriteriaParamDto;
import org.openiam.idm.srvc.report.dto.ReportSubscriptionDto;
import org.openiam.idm.srvc.report.ws.ReportWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Job bean that is called by quartz to kick off the nightly tasks.
 * 
 * @author suneet
 * 
 */
@Component
public class ReportingTask implements ApplicationContextAware {

	private static final Log log = LogFactory.getLog(ReportingTask.class);

	/*
	 * The flags for the running tasks are handled by this Thread-Safe Set. It
	 * stores the taskIds of the currently executing tasks. This is faster and
	 * as reliable as storing the flags in the database, if the tasks are only
	 * launched from ONE host in a clustered environment. It is unique for each
	 * class-loader, which means unique per war-deployment.
	 */
	private static Set<String> runningTask = Collections
			.newSetFromMap(new ConcurrentHashMap());

    @Resource(name = "reportServiceClient")
    private ReportWebService reportServiceClient;

    @Resource(name = "userServiceClient")
    private UserDataWebService userServiceClient;

    //@Resource(name = "groupServiceClient")
    //protected GroupDataWebService groupServiceClient;

    @Resource(name = "mailServiceClient")
    protected MailService mailService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Resource(name = "generatorExecutorService")
    private ExecutorService executorService;

    @Value("${org.openiam.subscription.reportRoot}")
    private String reportRoot;

    @Value("${org.openiam.subscription.generated_reports.folder}")
    private String generatedReportsFolder;

	// used to inject the application context into the groovy scripts
	public static ApplicationContext ac;

    /**
     * Executes the specified report subscription
     * @param report the report subscription entity
     * @throws BirtException
     */
    public void asyncExecuteReport(final ReportSubscriptionDto report) {
        Thread task = new Thread() {
            @Override
            public void run() {
                try {
                    runReportSubscription(report);
                } catch (BirtException e) {
                    log.error(e);
                }
            }
        };
        executorService.submit(task);
    }

	/**
	 * This method takes care of the following 1. Deletes existing generated
	 * report directories 2. Generates reports for all active subscriptions
	 * 3. Emails reports as applicable - this task can be moved to groovy if
	 * needed.
	 */
	private void executeBIRTTasks() {
		deleteGeneratedDirs();
		// task to generate BIRT reports
		List<ReportSubscriptionDto> subcribedReportList = reportServiceClient
				.getSubscribedReports().getReports();
		for (ReportSubscriptionDto report : subcribedReportList) {
            try {
                // TODO: add method to ReportWebService for filtered result
                if("ACTIVE".equals(report.getStatus())) {
                    runReportSubscription(report);
                }
            } catch (BirtException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}

    /**
     * This method processes the specified report subscription
     * @param report the report subscription entity
     * @throws BirtException
     */
    private void runReportSubscription(ReportSubscriptionDto report) throws BirtException {
        ReportInfoDto reportInfo = reportServiceClient
                .getReport(report.getReportInfoId()).getReport();

        final DeliveryMethod deliveryMethod = DeliveryMethod.parseMethod(report.getDeliveryMethod().toUpperCase());
        final String designFilePath = reportRoot + "/" + reportInfo.getReportUrl();
        final ReportFormat reportFormat = ReportFormat.parseFormat(report.getDeliveryFormat().toUpperCase());
        final String outputFileName = getOutputDir(deliveryMethod) + "/" + report.getUserId() +
                "/" + reportInfo.getReportName() + "." + reportFormat.getExtension();

        List<ReportSubCriteriaParamDto> reportParameters = reportServiceClient
                .getSubscribedReportParametersByReportId(report.getReportId()).getParameters();
        Map<String, String> params = new LinkedHashMap<String, String>();
        for (ReportSubCriteriaParamDto parameter : reportParameters) {
            params.put(parameter.getName(), parameter.getValue());
        }

        //Email the reports or replicate across userids as applicable
        switch (deliveryMethod) {
            case EMAIL:
                final List<String> emailAddresses = getEmailAddresses(report);
                if (CollectionUtils.isNotEmpty(emailAddresses)) {
                    reportGenerator.generateReport(designFilePath, params, outputFileName, reportFormat);
                    deliverReportByEmail(outputFileName, emailAddresses);
                } else {
                    log.info("No subscribers found. Report generation skipped for " + report.getReportName());
                }
                break;
            case VIEW:
                final List<String> userIds = getUserIds(report);
                if (CollectionUtils.isNotEmpty(userIds)) {
                    reportGenerator.generateReport(designFilePath, params, outputFileName, reportFormat);
                    deliverReportToFolder(outputFileName, userIds);
                } else {
                    log.info("No subscribers found. Report generation skipped for " + report.getReportName());
                }
                break;
            default:
                log.info("The specified delivery method isn't supported: " + deliveryMethod.toString());

        }
    }

    private List<String> getEmailAddresses(ReportSubscriptionDto report) {
        if ("SELF".equalsIgnoreCase(report.getDeliveryAudience())) {
            List<EmailAddress> emails = userServiceClient.getEmailAddressList(
                    report.getUserId());
            List<String> emailAddresses = new ArrayList<String>();
            //at least one email per user is mandatory
            emailAddresses.add(emails.get(0).getEmailAddress());
            return emailAddresses;
        }
        return null;
    }

    private List<String> getUserIds(ReportSubscriptionDto report) {
        if ("SELF".equalsIgnoreCase(report.getDeliveryAudience())) {
            List<String> userIds = new ArrayList<String>();
            userIds.add(report.getUserId());
            return userIds;
        }
        return null;
    }

    /*
            User reportOwner = findUser(report.getUserId());
            if ("ROLE".equalsIgnoreCase(report.getDeliveryAudience())) {
                UserRoleListResponse userRolesResponse = roleDataService.getUserRolesForUser(
                        report.getUserId());
                List<UserRole> userRoles = userRolesResponse.getUserRoleList();
                List<String> roleList = new ArrayList<String>();
                String domainId = "";
                //Assuming that domain of all users is same, or it will pick last one
                //TODO --clarify the above assumption
                for (UserRole role: userRoles) {
                    roleList.add(role.getRoleId());
                    domainId = role.getServiceId();
                }
                search.setRoleIdList(roleList);
                search.setDomainId(domainId);
            } else if ("DEPT".equalsIgnoreCase(report.getDeliveryAudience())) {
                search.setDeptCd(reportOwner.getDeptCd());
            } else if ("ORGANIZATION".equalsIgnoreCase(report.getDeliveryAudience())) {
                search.setOrgId(reportOwner.getCompanyId());
            } else if ("DIVISION".equalsIgnoreCase(report.getDeliveryAudience())) {
                search.setDivision(reportOwner.getDivision());
            } else if ("GROUP".equalsIgnoreCase(report .getDeliveryAudience())) {
                // TODO: set requester ID
                List<Group> groups = groupServiceClient.getGroupsForUser(report.getUserId(), "", 0, 100);
                Set<String> groupList = new HashSet<String>();
                for (Group group: groups) {
                    groupList.add(group.getGrpId());
                }
               search.setGroupIdSet(groupList);
            }

            List<User> userList = userServiceClient.findBeans(search, 0, 100);
            for (User user1 : userList) {
                emailAddresses.add(user1.getEmail());
                userIds.add(report.getUserId());
            }
    */

    private User findUser(String userId) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setUserId(userId);
        List<User> users = userServiceClient.findBeans(searchBean , 0, 1);
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        return users.get(0);
    }

    private void deliverReportByEmail(String filePath, List<String> emailAddresses) {
        //TODO check fromAddress
        final String[] emailAddressesArray = emailAddresses.toArray(new String[0]);
        final String[] attachmentsArray = new String[] {filePath};
        mailService.sendEmails("BIRTReportSender", emailAddressesArray, null, null,
                "Subscribed Report", "Please find your subscribed report attached.",
                false, attachmentsArray);
    }

    private void deliverReportToFolder(String filePath, List<String> userIds) {
        File reportFile = new File(filePath);
        final String ouputDir = getOutputDir(DeliveryMethod.VIEW);
        for (String userId : userIds){
            File destDir = new File(ouputDir + "/" + userId);
            try {
                FileUtils.copyFileToDirectory(reportFile, destDir);
            } catch (IOException e) {
                log.error("Failed to deliver report file", e);
            }
        }
    }

    public void deleteGeneratedDirs() {
        try {
            FileUtils.deleteDirectory(new File(reportRoot + "/" + generatedReportsFolder));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ac = applicationContext;
	}

    private String getOutputDir(DeliveryMethod deliveryMethod) {
        return reportRoot+ "/" + generatedReportsFolder + "/" + deliveryMethod.toString();
    }

/*
	public void execute() {
		log.debug("NightlyBatchJob called.");

		ScriptIntegration se = null;
		Map<String, Object> bindingMap = new HashMap<String, Object>();
		bindingMap.put("context", ac);

		if (!isPrimary) {
			log.debug("Scheduler: Not primary instance");
			return;
		}

		try {
			se = ScriptFactory.createModule(this.scriptEngine);
		} catch (Exception e) {
			log.error(e);
			return;
		}

		// get the list of domains
		List<BatchTask> taskList = batchService
				.getAllTasksByFrequency("NIGHTLY");
		log.debug("-Tasklist=" + taskList);

		if (taskList != null) {
			for (BatchTask task : taskList) {
				log.debug("Executing task:" + task.getTaskName());

				String requestId = UUIDGen.getUUID();

				try {
					if (task.getEnabled() != 0) {
						// This needs to be synchronized, because the check for
						// the taskId and the insertion need to
						// happen atomically. It is possible for two threads,
						// started by Quartz, to reach this point at
						// the same time for the same task.
						synchronized (runningTask) {
							if (runningTask.contains(task.getTaskId())) {
								log.debug("Task " + task.getTaskName()
										+ " already running");
								continue;
							}
							runningTask.add(task.getTaskId());
						}

						log.debug("Executing task:" + task.getTaskName());
						if (task.getLastExecTime() == null) {
							task.setLastExecTime(new Date(System
									.currentTimeMillis()));
						}

						bindingMap.put("taskObj", task);
						bindingMap.put("lastExecTime", task.getLastExecTime());
						bindingMap.put("parentRequestId", requestId);

						Integer output = (Integer) se.execute(bindingMap,
								task.getTaskUrl());
						if (output.intValue() == 0) {
							auditHelper.addLog(task.getTaskName(), null, null,
									"IDM BATCH TASK", null, "0", "DAILY BATCH",
									task.getTaskId(), null, "FAIL", null, null,
									null, null, null, null, null);
						} else {
							auditHelper.addLog(task.getTaskName(), null, null,
									"IDM BATCH TASK", null, "0", "DAILY BATCH",
									task.getTaskId(), null, "SUCCESS", null,
									null, null, null, null, null, null);
						}
					}
				} catch (Exception e) {
					log.error(e);
				} finally {
					if (task.getEnabled() != 0) {
						// this point can only be reached by the thread, which
						// put the taskId into the map
						runningTask.remove(task.getTaskId());
						// Get the updated status of the task
						task = batchService.getBatchTask(task.getTaskId());
						task.setLastExecTime(new Date(System
								.currentTimeMillis()));
						batchService.updateTask(task);
					}
				}

			}
		}
		executeBIRTTasks();
	}
*/

/*
	public File[] getReportsListForUser(String userId) {
		File dir = new File(reportRoot + "/" + generatedReportsFolder + "/VIEW/" + userId);
		File[] files = dir.listFiles();
		return files;
	}
*/

}
