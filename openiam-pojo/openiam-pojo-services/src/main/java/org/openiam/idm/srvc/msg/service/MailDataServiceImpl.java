package org.openiam.idm.srvc.msg.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.base.request.NotificationParam;
import org.openiam.base.request.NotificationRequest;
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
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("mailDataService")
public class MailDataServiceImpl extends AbstractBaseService implements MailDataService, ApplicationContextAware {

    @Autowired
    private MailSender mailSender;

    @Value("${mail.defaultSender}")
    private String defaultSender;

    @Value("${mail.defaultSubjectPrefix}")
    private String subjectPrefix;

    @Value("${mail.optionalBccAddress}")
    private String optionalBccAddress;

    @Autowired
    protected UserDataService userManager;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    @Qualifier("pojoProperties")
    private Properties properties;

    public static ApplicationContext ac;

    private static final Log log = LogFactory.getLog(MailDataServiceImpl.class);
    private static final int SUBJECT_IDX = 0;
    private static final int SCRIPT_IDX = 1;
    private static final int IS_HTML_IDX = 2;

    public void sendToAllUsers() {
        log.warn("sendToAllUsers was called, but is not implemented");
    }

    public void sendToGroup(String groupId) {
        log.warn("sendToGroup was called, but is not implemented");
    }

//    /*
//     * public void send(String from, String to, String subject, String msg) {
//     * sendWithCC(from, to, null, subject, msg); }
//     */
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * org.openiam.srvc.user.MailService#sendWithCC(java.lang.String,
//     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
//     * java.lang.String, boolean)
//     */
//    public void sendEmail(String from, String to, String cc, String subject, String msg, String attachment,
//            boolean isHtmlFormat) {
//    	if(log.isDebugEnabled()) {
//	        log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", Cc:" + cc + ", Attachement:" + attachment
//	                + ", Format:" + isHtmlFormat);
//    	}
//        Message message = fillMessage(from, to, cc, optionalBccAddress, subject, msg, isHtmlFormat, attachment, null);
//        try {
//            mailSender.send(message);
//        } catch (Throwable e) {
//            log.error("can't send email", e);
//        }
//    }
//
//    public void sendEmailByDateTime(String from, String to, String cc, String subject, String msg, String attachment,
//                                    boolean isHtmlFormat, Date executionDateTime) {
//    	if(log.isDebugEnabled()) {
//	    	log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", Cc:" + cc + ", Attachement:" + attachment
//	                + ", Format:" + isHtmlFormat);
//    	}
//        Message message = fillMessage(from, to, cc, optionalBccAddress, subject, msg, isHtmlFormat, attachment, executionDateTime);
//        try {
//            mailSender.send(message);
//        } catch (Throwable e) {
//            log.error("can't send email", e);
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see org.openiam.srvc.user.MailService#send(java.lang.String,
//     * java.lang.String[], java.lang.String[], java.lang.String[],
//     * java.lang.String, java.lang.String, boolean, java.lang.String[])
//     */
//    public void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg,
//            boolean isHtmlFormat, String[] attachmentPath) {
//    	if(log.isDebugEnabled()) {
//	        log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", CC:" + cc + ", BCC:" + bcc
//	                + ", Attachment:" + attachmentPath);
//    	}
//        Message message = fillMessage(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, null);
//        try {
//            mailSender.send(message);
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//    }
//
//    @Override
//    public void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime) {
//        Message message = fillMessage(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime);
//
//        try {
//            mailSender.send(message);
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//    }
    public void sendEmail(String from, String to, String cc, String bcc, String subject, String msg, boolean isHtmlFormat, String attachmentPath, Date executionDateTime) {
        List<String> toList = new ArrayList<>();
        List<String> ccList = new ArrayList<>();
        List<String> bccList = new ArrayList<>();
        List<String> attachmentList = new ArrayList<>();
        toList.add(to);
        ccList.add(cc);
        bccList.add(bcc);
        attachmentList.add(attachmentPath);

        this.sendEmails(from, toList, ccList, bccList,subject,msg,isHtmlFormat,attachmentList,executionDateTime);
    }

    @Override
    public void sendEmails(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String msg, boolean isHtmlFormat, List<String> attachmentPath, Date executionDateTime) {
        if(log.isDebugEnabled()) {
            log.debug("To:" + to + ", From:" + from + ", Subject:" + subject + ", CC:" + cc + ", BCC:" + bcc
                    + ", Attachment:" + attachmentPath);
        }
        Message message = fillMessage(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


//    private Message fillMessage(String from, String to, String cc, String bcc, String subject, String msg, boolean isHtmlFormat, String attachment, Date executionDateTime) {
//        return fillMessage(from, (to != null) ? new String[]{to} : null,
//                (cc != null)? new String[]{cc} : null,
//                (bcc != null)? new String[]{bcc} : null,
//                subject, msg,
//                isHtmlFormat,
//                (attachment != null)? new String[]{attachment} : null,
//                executionDateTime);
//    }

    private Message fillMessage(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String msg, boolean isHtmlFormat, List<String> attachmentPath, Date executionDateTime) {
        Message message = new Message();

        if (StringUtils.isNotBlank(from)) {
            message.setFrom(from);
        } else {
            message.setFrom(defaultSender);
        }

        if (CollectionUtils.isNotEmpty(to)) {
            for (String toString : to) {
            	if(StringUtils.isNotBlank(toString)) {
            		message.addTo(StringUtils.trimToNull(toString));
            	}
            }
        }
        if (CollectionUtils.isNotEmpty(cc)) {
            for (String ccString : cc) {
            	if(StringUtils.isNotBlank(ccString)) {
            		message.addCc(StringUtils.trimToNull(ccString));
            	}
            }
        }

        if (CollectionUtils.isNotEmpty(bcc)) {
            for (String bccString : bcc) {
            	if(StringUtils.isNotBlank(bccString)) {
            		message.addBcc(StringUtils.trimToNull(bccString));
            	}
            }
        }

        if (StringUtils.isNotBlank(subjectPrefix)) {
            String subj = subjectPrefix.trim();
            if (StringUtils.isNotBlank(subject)) {
                subject = subj + " " + subject;
            } else {
                subject = subj;
            }
        }

        if (StringUtils.isNotBlank(subject)) {
            message.setSubject(subject);
        }

        if (StringUtils.isNotBlank(msg)) {
            message.setBody(msg);
        }

        if (executionDateTime != null) {
            message.setExecutionDateTime(executionDateTime);
        }

        message.setBodyType(isHtmlFormat ? Message.BodyType.HTML_TEXT : Message.BodyType.PLAIN_TEXT);
        if (CollectionUtils.isNotEmpty(attachmentPath)) {
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
        Pattern pattern = Pattern.compile(propertyValueSweeper.getString("org.openiam.email.validation.regexp"), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.user.MailService#sendNotification(org.openiam
     * .idm.srvc.msg.dto.NotificationRequest)
     */
    @Transactional
    public boolean sendNotification(NotificationRequest req) {
        if (req == null) {
            return false;
        }
        if(log.isDebugEnabled()) {
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
    	if(log.isDebugEnabled()) {
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
            sendEmail(null, req.getTo(), req.getCc(), null, emailDetails[SUBJECT_IDX], emailBody,
                    isHtmlFormat(emailDetails), null, req.getExecutionDateTime());
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
        
        if(CollectionUtils.isEmpty(req.getParamList())) {
        	req.setParamList(new LinkedList<NotificationParam>());
        }
        req.getParamList().add(new NotificationParam("APPLICATION_CONTEXT", ac));

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("user", usr);
        bindingMap.put("req", req);

        String emailBody = createEmailBody(bindingMap, emailDetails[SCRIPT_IDX]);
        if (emailBody != null) {
            sendEmail(null, usr.getEmail(), null, null, emailDetails[SUBJECT_IDX], emailBody,
                    isHtmlFormat(emailDetails), null, req.getExecutionDateTime());
//            sendEmailByDateTime(null, usr.getEmail(), null, emailDetails[SUBJECT_IDX], emailBody, null,
//                    isHtmlFormat(emailDetails), req.getExecutionDateTime());
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
     * org.openiam.srvc.user.MailService#SendTwitterMessage(java.
     * lang.String, java.lang.String)
     */

    public void tweetPrivateMessage(String userid, String msg) {
        if (!areTwitterPropsSet()) {
            log.warn("Twitter properties are not set. Please set the same before tweeting.");
            return;
        }
        try {
            DirectMessage message = getTwitterInstance().sendDirectMessage(userid, msg);
            log.info("Direct mq successfully sent to " + message.getRecipientScreenName());
        } catch (TwitterException te) {
            //te.printStackTrace();
            log.error("Failed to send a direct mq: ", te);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.user.MailService#tweetMessage(java.lang.String
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
            //te.printStackTrace();
            log.error("Failed to update Status: ", te);

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
