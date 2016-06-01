package org.openiam.idm.srvc.msg.service;

import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;

/**
 * Provides methods to be able to send emails and send direct message to authorized users.
 *
 * @author suneet
 */
@WebService
public interface MailService {


    /**
     * Sends an email all users with OpenIAM
     */

    @WebMethod
    void sendToAllUsers();

    /**
     * Sends an email all users in a specific group
     *
     * @param groupId
     */
    @WebMethod
    void sendToGroup(String groupId);

    /**
     * Sends out a notification based on the information defined in the notification request.
     *
     * @param req
     */

    @WebMethod
    boolean sendNotification(
            @WebParam(name = "req", targetNamespace = "")
            NotificationRequest req);

    /**
     * sending a email to one user,cc and having one attachment at time.
     * @param from
     * @param to
     * @param cc
     * @param subject
     * @param msg
     * @param attachment
     * @param isHtmlFormat
     * @param executionDateTime - execution time
     */
    public void sendEmailByDateTime(String from, String to, String cc, String subject,
                          String msg ,String attachment ,boolean isHtmlFormat, Date executionDateTime);

    /**
     * sending a email to one user,cc and having one attachment at time.
     * @param from
     * @param to
     * @param cc
     * @param subject
     * @param msg
     * @param attachment
     * @param isHtmlFormat
     */
    public void sendEmail(String from, String to, String cc, String subject,
            String msg ,String attachment ,boolean isHtmlFormat);

    /**
     *
     * sending a email from one user to multiple user,cc and bcc having multiple attachement at a time.
     * @param from
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param msg
     * @param isHtmlFormat
     * @param attachmentPath
     */
    public void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath);

    /**
     * sending a email from one user to multiple user,cc and bcc having multiple attachement at a time.
     *
     * @param from
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param msg
     * @param isHtmlFormat
     * @param attachmentPath
     * @param executionDateTime - execution time
     */
    public void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime);

    /**
     *  sending out direct private message to authorized user on twitter.
     * @param userid
     * @param msg
     */
    public void tweetPrivateMessage(String userid, String msg);

    /**
     * mmethod used to update status over twitter.
     * @param status
     */
    public void tweetMessage(String status);


}
