package org.openiam.idm.srvc.msg.service;

import org.openiam.base.request.NotificationRequest;

import java.util.Date;

/**
 * Provides methods to be able to send emails and send direct mq to authorized users.
 *
 * @author suneet
 */
public interface MailDataService {


    /**
     * Sends an email all users with OpenIAM
     */
    void sendToAllUsers();

    /**
     * Sends an email all users in a specific group
     *
     * @param groupId
     */
    void sendToGroup(String groupId);

    /**
     * Sends out a notification based on the information defined in the notification request.
     *
     * @param req
     */

    boolean sendNotification(NotificationRequest req);

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
    void sendEmailByDateTime(String from, String to, String cc, String subject,
                             String msg, String attachment, boolean isHtmlFormat, Date executionDateTime);

    
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
    void sendEmail(String from, String to, String cc, String subject,
                   String msg, String attachment, boolean isHtmlFormat);
    
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
    void sendEmails(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath);

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
    void sendEmailsByDateTime(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath, Date executionDateTime);

    /**
     *  sending out direct private mq to authorized user on twitter.
     * @param userid
     * @param msg
     */
    void tweetPrivateMessage(String userid, String msg);
    
    /**
     * mmethod used to update status over twitter.
     * @param status
     */
    void tweetMessage(String status);
}
