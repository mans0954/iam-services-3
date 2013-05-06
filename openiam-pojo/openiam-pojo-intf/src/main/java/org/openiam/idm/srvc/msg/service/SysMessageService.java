package org.openiam.idm.srvc.msg.service;

// Generated Nov 27, 2009 11:18:13 PM by Hibernate Tools 3.2.2.GA

import org.openiam.idm.srvc.msg.dto.NotificationConfig;

import java.util.List;

/**
 * Interface for the SysMessageDelivery service. The message Delivery service is allows you to create and define messages and have them
 * delivered to the audience an application such as the selfservice app..
 *
 * @author Suneet shah
 * @see org.openiam.idm.srvc.msg.dto.NotificationConfig
 */
public interface SysMessageService {

    /**
     * method used for adding message.
     * @param transientInstance
     * @return
     */
    public NotificationConfig addMessage(NotificationConfig transientInstance);

    /**
     * method used to remove  message by id.
     * @param id
     */
    public void removeMessage(String id);

    /**
     * 
     * method used for updating message.
     * @param detachedInstance
     * @return
     */
    public NotificationConfig updateMessage(NotificationConfig detachedInstance);

    /**
     * method used to get message by the id.
     * @param id
     * @return
     */
    public NotificationConfig getMessageById(java.lang.String id);


    /**
     * 
     * method fetching the list of all messages.
     * @return
     */
    public List<NotificationConfig> getAllMessages();
}
