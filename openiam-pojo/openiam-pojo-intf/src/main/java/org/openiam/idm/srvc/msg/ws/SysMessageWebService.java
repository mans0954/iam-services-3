package org.openiam.idm.srvc.msg.ws;

// Generated Nov 27, 2009 11:18:13 PM by Hibernate Tools 3.2.2.GA

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.msg.dto.NotificationConfig;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interface for the SysMessageDelivery service. The message Delivery service is allows you to create and define messages and have them
 * delivered to the audience an application such as the selfservice app..
 *
 * @author Suneet shah
 * @see org.openiam.idm.srvc.msg.dto.NotificationConfig
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/msg/service", name = "SysMessageWebService")
public interface SysMessageWebService {

    @WebMethod
    public SysMessageResponse addMessage(
            @WebParam(name = "msg", targetNamespace = "")
            NotificationConfig msg);

    @WebMethod
    public Response removeMessage(
            @WebParam(name = "msgId", targetNamespace = "")
            String msgId);

    @WebMethod
    public SysMessageResponse updateMessage(
            @WebParam(name = "msg", targetNamespace = "")
            NotificationConfig msg);

    @WebMethod
    public SysMessageResponse getMessageById(
            @WebParam(name = "id", targetNamespace = "")
            java.lang.String id);


    @WebMethod
    public SysMessageListResponse getAllMessages();
}
