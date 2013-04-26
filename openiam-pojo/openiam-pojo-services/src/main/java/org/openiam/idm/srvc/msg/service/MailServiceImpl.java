package org.openiam.idm.srvc.msg.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("mailService")
@WebService(endpointInterface = "org.openiam.idm.srvc.msg.service.MailService", targetNamespace = "urn:idm.openiam.org/srvc/msg", portName = "EmailWebServicePort", serviceName = "EmailWebService")
public class MailServiceImpl implements MailService, ApplicationContextAware {

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
    protected AuditHelper auditHelper;
    
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

    public void sendToAllUsers() {
        log.warn("sendToAllUsers was called, but is not implemented");
    }

    public void sendToGroup(String groupId) {
        log.warn("sendToGroup was called, but is not implemented");
    }

    public void send(String from, String to, String subject, String msg) {
        sendWithCC(from, to, null, subject, msg);
    }

    public void sendWithCC(String from, String to, String cc, String subject,
            String msg) {
        log.debug("To:" + to + ", From:" + from + ", Subject:" + subject);

        Message message = new Message();
        if (from != null && from.length() > 0) {
            message.setFrom(from);
        } else {
            message.setFrom(defaultSender);
        }
        message.addTo(to);
        if (cc != null && !cc.isEmpty()) {
            message.addCc(cc);
        }
        if (subjectPrefix != null) {
            subject = subjectPrefix + " " + subject;
        }
        if (optionalBccAddress != null && !optionalBccAddress.isEmpty()) {
            message.addBcc(optionalBccAddress);
        }
        message.setSubject(subject);
        message.setBody(msg);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
    
    public void send(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath) {
        log.debug("To:" + to + ", From:" + from + ", Subject:" + subject +", CC:"+ cc +", BCC:"+ bcc +", MESSG:" + msg +", Attachment:"+ attachmentPath);

        Message message = new Message();
        if (from != null && from.length() > 0) {
            message.setFrom(from);
        } else {
            message.setFrom(defaultSender);
        }
        for (String toString: to){
        	message.addTo(toString);
        }
        for (String ccString: cc){
        	message.addCc(ccString);
        }
        if (subjectPrefix != null) {
            subject = subjectPrefix + " " + subject;
        }
        for (String bccString: bcc){
        	message.addBcc(bccString);
        }
        message.setSubject(subject);
        message.setBody(msg);
        message.setBodyType(isHtmlFormat ? Message.BodyType.HTML_TEXT : Message.BodyType.PLAIN_TEXT);
        for (String attachmentPathString: attachmentPath){
        	message.addAttachments(attachmentPathString);
        }
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean sendNotification(NotificationRequest req) {
        if (req == null) {
            return false;
        }
        log.debug("Send Notification called with notificationType = "
                + req.getNotificationType());

        if (req.getUserId() != null) {
            return sendEmailForUser(req);
        } else if (req.getTo() != null) {
            return sendCustomEmail(req);
        }
        return false;
    }

    private boolean sendCustomEmail(NotificationRequest req) {
        log.debug("sendNotification to = " + req.getTo());

        String[] emailDetails = fetchEmailDetails(req.getNotificationType());
        if (emailDetails == null) {
            return false;
        }

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("context", ac);
        bindingMap.put("req", req);

        String emailBody = createEmailBody(bindingMap, emailDetails[SCRIPT_IDX]);
        if (emailBody != null) {
            sendWithCC(null, req.getTo(), req.getCc(),
                    emailDetails[SUBJECT_IDX], emailBody);
            return true;
        }
        return false;
    }

    private boolean sendEmailForUser(NotificationRequest req) {
    	if(log.isDebugEnabled()) {
    		log.debug(String.format("sendNotification userId = %s", req.getUserId()));
    	}
        // get the user object
        if (req.getUserId() == null) {
            return false;
        }
        UserEntity usr = userManager.getUser(req.getUserId());
        if (usr == null) {
            return false;
        }
        if(log.isDebugEnabled()) {
        	log.debug(String.format("Email address=%s", usr.getEmail()));
        }

        if (StringUtils.isBlank(usr.getEmail())) {
            log.error(String.format("Send notfication failed. Email was null for userId=%s", usr.getUserId()));
            return false;
        }

        if (!isEmailValid(usr.getEmail())) {
            log.error(String.format("Send notfication failed. Email was is not valid for userId=%s - %s",usr.getUserId(), usr.getEmail()));
            return false;
        }
        String[] emailDetails = fetchEmailDetails(req.getNotificationType());
        if (emailDetails == null) {
            return false;
        }

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("context", ac);
        bindingMap.put("user", usr);
        bindingMap.put("req", req);

        String emailBody = createEmailBody(bindingMap, emailDetails[SCRIPT_IDX]);
        if (emailBody != null) {
            send(null, usr.getEmail(), emailDetails[SUBJECT_IDX], emailBody);
            return true;
        }
        return false;
    }

    private String createEmailBody(Map<String, Object> bindingMap,
            String emailScript) {
        try {
            return (String) scriptRunner.execute(bindingMap, emailScript);
        } catch (Exception e) {
            log.error("createEmailBody():" + e.toString());
            return null;
        }
    }

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

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ac = applicationContext;
    }
}
