package org.openiam.idm.srvc.msg.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.jws.WebService;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("mailService")
@WebService(endpointInterface = "org.openiam.idm.srvc.msg.service.MailService", targetNamespace = "urn:idm.openiam.org/srvc/msg", portName = "EmailWebServicePort", serviceName = "EmailWebService")
public class MailServiceImpl implements MailService, ApplicationContextAware {

    @Autowired
    private MailSender mailSender;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    private LoginDataService loginManager;

    @Value("${mail.defaultSender}")
    private String defaultSender;

    @Value("${mail.defaultSubjectPrefix}")
    private String subjectPrefix;

    @Value("${mail.optionalBccAddress}")
    private String optionalBccAddress;

    @Value("${org.openiam.email.validation.regexp}")
    private String MAIL_REGEXP;

    @Autowired
    protected UserDataService userManager;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("pojoProperties")
    private Properties properties;

    public static ApplicationContext ac;

    private static final Log log = LogFactory.getLog(MailServiceImpl.class);
    private static final int SUBJECT_IDX = 0;
    private static final int SCRIPT_IDX = 1;
    private static final int IS_HTML_IDX = 2;

    public void sendToAllUsers() {
        log.warn("sendToAllUsers was called, but is not implemented");
    }

    public void sendToGroup(String groupId) {
        log.warn("sendToGroup was called, but is not implemented");
    }

    /*
     * public void send(String from, String to, String subject, String msg) {
     * sendWithCC(from, to, null, subject, msg); }
     */

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.msg.service.MailService#sendWithCC(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, boolean)
     */
    public void sendEmail(String from, String to, String cc, String subject, String msg, String attachment,
                          boolean isHtmlFormat) {
        if (log.isDebugEnabled()) {
            log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", Cc:" + cc + ", Attachement:" + attachment
                    + ", Format:" + isHtmlFormat);
        }
        Message message = fillMessage(from, to, cc, optionalBccAddress, subject, msg, isHtmlFormat, attachment, null);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.SEND_EMAIL.value());
        idmAuditLog.setAuditDescription("Send email to :" + to + "  subject: " + subject);

        try {
            mailSender.send(message);
            idmAuditLog.succeed();
        } catch (Throwable e) {
            log.error("can't send email", e);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
        }
        auditLogService.enqueue(idmAuditLog);
    }

    public void sendEmailByDateTime(String from, String to, String cc, String subject, String msg, String attachment,
                                    boolean isHtmlFormat, Date executionDateTime) {
        sendEmailByDateTimeExt(from, to, cc, subject, msg, attachment, isHtmlFormat, executionDateTime, null, null, AuditAction.SEND_EMAIL.value());
    }
    private void sendEmailByDateTimeExt(String from, String to, String cc, String subject, String msg, String attachment,
                                    boolean isHtmlFormat, Date executionDateTime, String userId, String principal, String action) {
        if (log.isDebugEnabled()) {
            log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", Cc:" + cc + ", Attachement:" + attachment
                    + ", Format:" + isHtmlFormat);
        }
        Message message = fillMessage(from, to, cc, optionalBccAddress, subject, msg, isHtmlFormat, attachment, executionDateTime);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(action);
        idmAuditLog.setTargetUser(userId, principal);
        idmAuditLog.setAuditDescription("Send email to :" + to + "  subject: " + subject + "   -  principal:" + principal);
        try {
            mailSender.send(message);
            idmAuditLog.succeed();
        } catch (Throwable e) {
            log.error("can't send email", e);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
        }
        auditLogService.enqueue(idmAuditLog);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.msg.service.MailService#send(java.lang.String,
     * java.lang.String[], java.lang.String[], java.lang.String[],
     * java.lang.String, java.lang.String, boolean, java.lang.String[])
     */
    public void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg,
                           boolean isHtmlFormat, String[] attachmentPath) {
        if (log.isDebugEnabled()) {
            log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", CC:" + cc + ", BCC:" + bcc
                    + ", Attachment:" + attachmentPath);
        }
        Message message = fillMessage(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, null);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.SEND_EMAIL.value());
        idmAuditLog.setAuditDescription("Send email to :" + to + "  subject: " + subject);
        try {
            mailSender.send(message);
            idmAuditLog.succeed();
        } catch (Exception e) {
            log.error(e.toString());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
        }
        auditLogService.enqueue(idmAuditLog);
    }
    @Override
    public void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat,
                                     String[] attachmentPath, Date executionDateTime) {
        sendEmailsByDateTimeExt(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime, null, null, AuditAction.SEND_EMAIL);
    }

    private void sendEmailsByDateTimeExt(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat,
                                     String[] attachmentPath, Date executionDateTime, String userId, String principal, AuditAction action) {
        Message message = fillMessage(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime);
        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setAction(AuditAction.SEND_EMAIL.value());
        idmAuditLog.setAuditDescription("Send email to :" + to + "  subject: " + subject);

        try {
            mailSender.send(message);
            idmAuditLog.succeed();
        } catch (Exception e) {
            log.error(e.toString());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getMessage());
        }
        auditLogService.enqueue(idmAuditLog);
    }

    private Message fillMessage(String from, String to, String cc, String bcc, String subject, String msg, boolean isHtmlFormat, String attachment, Date executionDateTime) {
        return fillMessage(from, (to != null) ? new String[]{to} : null,
                (cc != null) ? new String[]{cc} : null,
                (bcc != null) ? new String[]{bcc} : null,
                subject, msg,
                isHtmlFormat,
                (attachment != null) ? new String[]{attachment} : null,
                executionDateTime);
    }

    private Message fillMessage(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime) {
        Message message = new Message();

        if (from != null && from.length() > 0) {
            message.setFrom(from);
            if (log.isDebugEnabled()) {
                log.debug("MailServiceImpl adding From:"+from);
            }
        } else {
            message.setFrom(defaultSender);
            if (log.isDebugEnabled()) {
                log.debug("MailServiceImpl adding From:"+defaultSender);
            }
        }
        if (to != null && to.length > 0) {
            for (String toString : to) {
                if (StringUtils.isNotBlank(toString)) {
                    message.addTo(toString);
                    if (log.isDebugEnabled()) {
                        log.debug("MailServiceImpl adding To:"+toString);
                    }
                }

            }
        }
        if (cc != null && cc.length > 0) {
            for (String ccString : cc) {
                if (StringUtils.isNotBlank(ccString)) {
                    message.addCc(ccString);
                    if (log.isDebugEnabled()) {
                        log.debug("MailServiceImpl adding CC:"+ccString);
                    }
                }
            }
        }

        if (bcc != null && bcc.length > 0) {
            for (String bccString : bcc) {
                if (StringUtils.isNotBlank(bccString)) {
                    message.addBcc(bccString);
                    if (log.isDebugEnabled()) {
                        log.debug("MailServiceImpl adding BCC:"+bccString);
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(subjectPrefix)) {
            String subj = subjectPrefix.trim();
            if (subject != null && subject.length() > 0) {
                subject = subj + " " + subject;
            } else {
                subject = subj;
            }
        }

        if (subject != null && subject.length() > 0) {
            message.setSubject(subject);
        }

        if (msg != null && msg.length() > 0) {
            message.setBody(msg);
            if (log.isDebugEnabled()) {
                log.debug("MailServiceImpl adding Message:" + msg);
            }
        }

        if (executionDateTime != null) {
            message.setExecutionDateTime(executionDateTime);
        }

        message.setBodyType(isHtmlFormat ? Message.BodyType.HTML_TEXT : Message.BodyType.PLAIN_TEXT);
        if (attachmentPath != null) {
            for (String attachmentPathString : attachmentPath) {
                message.addAttachments(attachmentPathString);
            }
        }


        return message;
    }

    /**
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(MAIL_REGEXP, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.msg.service.MailService#sendNotification(org.openiam
     * .idm.srvc.msg.dto.NotificationRequest)
     */
    @Transactional
    public boolean sendNotification(NotificationRequest req) {
        if (req == null) {
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Send Notification called with notificationType = " + req.getNotificationType());
        }

        if (req.getUserId() != null) {
            return sendEmailForUser(req);
        } else if (req.getTo() != null) {
            return sendCustomEmail(req);
        }
        return false;
    }

    /**
     * @param req
     * @return
     */
    private boolean sendCustomEmail(NotificationRequest req) {
        if (log.isDebugEnabled()) {
            log.debug("sendNotification to = " + req.getTo());
        }

        String[] emailDetails = fetchEmailDetails(req.getNotificationType());
        if (emailDetails == null) {
            return false;
        }

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("req", req);

        String emailBody = createEmailBody(bindingMap, emailDetails[SCRIPT_IDX]);
        if (emailBody != null) {
            sendEmailByDateTime(null, req.getTo(), req.getCc(), emailDetails[SUBJECT_IDX], emailBody, null,
                    isHtmlFormat(emailDetails), req.getExecutionDateTime());
            return true;
        }
        return false;
    }

    /**
     * @param req
     * @return
     */
    private boolean sendEmailForUser(NotificationRequest req) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("sendNotification userId = %s", req.getUserId()));
        }
        // get the user object
        if (req.getUserId() == null) {
            log.warn("UserID is null");
            return false;
        }
        UserEntity usr = userManager.getUser(req.getUserId());
        if (usr == null) {
            log.warn(String.format("Can't find user with id '%s", req.getUserId()));
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Email address=%s", usr.getEmail()));
        }

        if (StringUtils.isBlank(usr.getEmail())) {

            log.error(String.format("Send notification failed. Email was null for userId=%s", usr.getId()));
            return false;
        }

        if (!isEmailValid(usr.getEmail())) {
            log.error(String.format("Send notfication failed. Email was is not valid for userId=%s - %s", usr.getId(),
                    usr.getEmail()));
            return false;
        }
        String[] emailDetails = fetchEmailDetails(req.getNotificationType());
        if (emailDetails == null) {
            log.warn(String.format("Email details were null for notification type '%s'", req.getNotificationType()));
            return false;
        }

        if (CollectionUtils.isEmpty(req.getParamList())) {
            req.setParamList(new LinkedList<NotificationParam>());
        }
        req.getParamList().add(new NotificationParam("APPLICATION_CONTEXT", ac));

        String action = AuditAction.SEND_EMAIL.value();
        if (req.getNotificationParam(MailTemplateParameters.AUDIT_ACTION.value()) != null) {
            action = req.getNotificationParam(MailTemplateParameters.AUDIT_ACTION.value()).getValue();
            log.warn("Audit Action :" + action);
        }

        LoginEntity principal = loginManager.getPrimaryIdentity(usr.getId());
        String login = null;
        if (principal != null) {
            login = principal.getLogin();
        }

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("user", usr);
        bindingMap.put("req", req);

        String emailBody = createEmailBody(bindingMap, emailDetails[SCRIPT_IDX]);
        if (emailBody != null) {
            if (req.getNotificationParam(MailTemplateParameters.SUBJECT.value()) != null)
                sendEmailByDateTimeExt(null, usr.getEmail(), null, String.valueOf(req.getNotificationParam(MailTemplateParameters.SUBJECT.value()).getValueObj()), emailBody, null,
                        isHtmlFormat(emailDetails), req.getExecutionDateTime(), usr.getId(), login, action);
            else
                sendEmailByDateTimeExt(null, usr.getEmail(), null, emailDetails[SUBJECT_IDX], emailBody, null,
                        isHtmlFormat(emailDetails), req.getExecutionDateTime(),  usr.getId(), login, action);
            return true;
        }
        log.warn("Email not sent - failure occurred");
        return false;
    }

    private boolean isHtmlFormat(String[] emailDetails) {
        boolean ret = false;
        if (emailDetails != null && emailDetails.length > IS_HTML_IDX) {
            String flag = emailDetails[IS_HTML_IDX];
            ret = "Y".equalsIgnoreCase(flag) || "YES".equalsIgnoreCase(flag);
        }
        return ret;
    }

    /**
     * @param bindingMap
     * @param emailScript
     * @return
     */
    private String createEmailBody(Map<String, Object> bindingMap, String emailScript) {
        try {
            return (String) scriptRunner.execute(bindingMap, emailScript);
        } catch (Exception e) {
            log.error("createEmailBody():" + e.toString());
            return null;
        }
    }

    /**
     * @param notificationType
     * @return
     */
    private String[] fetchEmailDetails(String notificationType) {
        // for each notification, there will be entry in the property file
        String notificationDetl = properties.getProperty(notificationType);
        String[] details = StringUtils.split(notificationDetl, ";");
        if (details == null || details.length < 2) {
            log.warn(String.format("Mail not sent, invalid notificationType: %s", notificationType));
            return null;
        }
        return details;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    @Value("${oauth.consumerKey}")
    private String consumerKey;

    @Value("${oauth.consumerSecret}")
    private String consumerSecret;

    @Value("${oauth.accessToken}")
    private String accessToken;

    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.msg.service.MailService#SendTwitterMessage(java.
     * lang.String, java.lang.String)
     */

    public void tweetPrivateMessage(String userid, String msg) {
        if (!areTwitterPropsSet()) {
            log.warn("Twitter properties are not set. Please set the same before tweeting.");
            return;
        }
        try {
            DirectMessage message = getTwitterInstance().sendDirectMessage(userid, msg);
            log.info("Direct message successfully sent to " + message.getRecipientScreenName());
        } catch (TwitterException te) {
            te.printStackTrace();
            log.error("Failed to send a direct message: " + te.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.msg.service.MailService#tweetMessage(java.lang.String
     * )
     */
    @Override
    public void tweetMessage(String status) {
        if (!areTwitterPropsSet()) {
            log.warn("Twitter properties are not set. Please set the same before tweeting.");
            return;
        }
        try {
            Status stat = getTwitterInstance().updateStatus(status);
            log.info("Status successfully Updated  ");

        } catch (TwitterException te) {
            te.printStackTrace();
            log.error("Failed to update Status: " + te.getMessage());

        }
    }

    private Twitter getTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }

    private boolean areTwitterPropsSet() {
        boolean areTwitterPropsSet = false;
        if (StringUtils.isNotBlank(consumerKey) && StringUtils.isNotBlank(consumerSecret)
                && StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(accessTokenSecret)) {
            areTwitterPropsSet = true;
        }
        return areTwitterPropsSet;
    }

}
