package org.openiam.srvc.user;

import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.base.request.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;

import java.util.*;

@Service("mailService")
@WebService(endpointInterface = "org.openiam.srvc.user.MailService", targetNamespace = "urn:idm.openiam.org/srvc/msg", portName = "EmailWebServicePort", serviceName = "EmailWebService")
public class MailServiceImpl extends AbstractBaseService implements MailService {

    @Autowired
    private MailDataService mailDataService;



    public void sendToAllUsers() {
        mailDataService.sendToAllUsers();
    }

    public void sendToGroup(String groupId) {
        mailDataService.sendToGroup(groupId);
    }

    /*
     * public void send(String from, String to, String subject, String msg) {
     * sendWithCC(from, to, null, subject, msg); }
     */

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.user.MailService#sendWithCC(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, boolean)
     */
    public void sendEmail(String from, String to, String cc, String subject, String msg, String attachment,
            boolean isHtmlFormat) {
        mailDataService.sendEmail(from, to, cc, subject, msg, attachment, isHtmlFormat);
    }

    public void sendEmailByDateTime(String from, String to, String cc, String subject, String msg, String attachment,
                                    boolean isHtmlFormat, Date executionDateTime) {
        mailDataService.sendEmailByDateTime(from, to, cc, subject, msg, attachment, isHtmlFormat, executionDateTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.srvc.user.MailService#send(java.lang.String,
     * java.lang.String[], java.lang.String[], java.lang.String[],
     * java.lang.String, java.lang.String, boolean, java.lang.String[])
     */
    public void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg,
            boolean isHtmlFormat, String[] attachmentPath) {
        mailDataService.sendEmails(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath);
    }

    @Override
    public void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime) {
        mailDataService.sendEmailsByDateTime(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime);
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
        return mailDataService.sendNotification(req);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.user.MailService#SendTwitterMessage(java.
     * lang.String, java.lang.String)
     */

    public void tweetPrivateMessage(String userid, String msg) {
        mailDataService.tweetPrivateMessage(userid, msg);
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
        mailDataService.tweetMessage(status);
    }
}
