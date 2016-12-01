package org.openiam.srvc.common;

import org.openiam.base.request.SendEmailRequest;
import org.openiam.base.request.TweetMessageRequest;
import org.openiam.base.request.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailDataService;
import org.openiam.mq.constants.api.common.EmailAPI;
import org.openiam.mq.constants.queue.common.MailServiceQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

import java.util.*;

@Service("mailService")
@WebService(endpointInterface = "org.openiam.srvc.common.MailService", targetNamespace = "urn:idm.openiam.org/srvc/msg", portName = "EmailWebServicePort", serviceName = "EmailWebService")
public class MailServiceImpl extends AbstractApiService implements MailService {

    @Autowired
    private MailDataService mailDataService;
    @Autowired
    public MailServiceImpl(MailServiceQueue queue) {
        super(queue);
    }


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
     * org.openiam.srvc.common.MailService#sendWithCC(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public void sendEmail(String from, String to, String cc, String subject, String msg, String attachment, boolean isHtmlFormat) {
//        mailDataService.sendEmail(from, to, cc, subject, msg, attachment, isHtmlFormat);
        doSendEmails(from, new String[]{to}, new String[]{cc}, null, subject, msg, isHtmlFormat, new String[]{attachment}, null);
    }
    @Override
    public void sendEmailByDateTime(String from, String to, String cc, String subject, String msg, String attachment,
                                    boolean isHtmlFormat, Date executionDateTime) {
        doSendEmails(from, new String[]{to}, new String[]{cc}, null, subject, msg, isHtmlFormat, null, executionDateTime);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.srvc.common.MailService#send(java.lang.String,
     * java.lang.String[], java.lang.String[], java.lang.String[],
     * java.lang.String, java.lang.String, boolean, java.lang.String[])
     */
    @Override
    public void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg,
            boolean isHtmlFormat, String[] attachmentPath) {
        doSendEmails(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, null);
    }

    @Override
    public void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime) {
        doSendEmails(from, to, cc, bcc, subject, msg, isHtmlFormat, attachmentPath, executionDateTime);
    }

    private void doSendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime){
        SendEmailRequest request = new SendEmailRequest();
        request.setFrom(from);
        request.setSubject(subject);
        request.setMsg(msg);
        request.setHtmlFormat(isHtmlFormat);
        request.setExecutionDateTime(executionDateTime);
        if(to!=null) {
            for (String var : to) {
                request.addTo(var);
            }
        }
        if(cc!=null) {
            for (String var : cc) {
                request.addCc(var);
            }
        }
        if(bcc!=null) {
            for (String var : bcc) {
                request.addBcc(var);
            }
        }
        if(attachmentPath!=null) {
            for (String var : attachmentPath) {
                request.addAttachment(var);
            }
        }
        this.sendAsync(EmailAPI.SendEmails, request);
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.common.MailService#sendNotification(org.openiam
     * .idm.srvc.msg.dto.NotificationRequest)
     */
    @Override
    public boolean sendNotification(NotificationRequest req) {
        return this.getBooleanValue(EmailAPI.SendNotification, req);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.common.MailService#SendTwitterMessage(java.
     * lang.String, java.lang.String)
     */
    @Override
    public void tweetPrivateMessage(String userid, String msg) {
        TweetMessageRequest request = new TweetMessageRequest();
        request.setMsg(msg);
        request.setUserid(userid);
        this.sendAsync(EmailAPI.TweetPrivateMessage, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.srvc.common.MailService#tweetMessage(java.lang.String
     * )
     */
    @Override
    public void tweetMessage(String status) {
        TweetMessageRequest request = new TweetMessageRequest();
        request.setMsg(status);
        this.sendAsync(EmailAPI.TweetMessage, request);
    }
}
