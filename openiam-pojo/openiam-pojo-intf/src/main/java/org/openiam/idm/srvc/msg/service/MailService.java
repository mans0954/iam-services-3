package org.openiam.idm.srvc.msg.service;

import org.openiam.idm.srvc.msg.dto.NotificationRequest;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Provides methods to be able to send emails.
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
    
    
    public void send(String from, String[] to, String[] cc, String[] bcc, String subject, String msg, boolean isHtmlFormat, String[] attachmentPath);
}
