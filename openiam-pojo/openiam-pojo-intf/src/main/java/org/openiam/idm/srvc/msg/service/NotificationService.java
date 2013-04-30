package org.openiam.idm.srvc.msg.service;

// Generated Nov 27, 2009 11:18:13 PM by Hibernate Tools 3.2.2.GA

import org.openiam.idm.srvc.msg.dto.NotificationDto;

import java.util.List;

/**
 * Interface for the SysMessageDelivery service. The message Delivery service is allows you to create and define messages and have them
 * delivered to the audience an application such as the selfservice app..
 *
 * @author Suneet shah
 * @see org.openiam.idm.srvc.msg.dto.NotificationDto
 */
public interface NotificationService {

    /**
     * method used for adding notification
     * @param transientInstance
     * @return
     */
    NotificationDto addNotification(NotificationDto transientInstance);

    /**
     * 
     * method used for deleting notification by id.
     * @param id
     */
    void removeNotification(String id);

    /**
     * method used for updating notification.
     * @param detachedInstance
     * @return
     */
    NotificationDto updateNotification(NotificationDto detachedInstance);

    /**
     * method used for getting notification by id.
     * @param id
     * @return
     */
    NotificationDto getNotificationById(java.lang.String id);

    /**
     * method used for getting notification by using name.
     * @param name
     * @return
     */
    NotificationDto getNotificationByName(String name);

    /**method used for getting all notification.
     * 
     * @return
     */
    List<NotificationDto> getAllNotifications();

    /**
     * method returning list of configurable notification.
     * @return
     */
    List<NotificationDto> getConfigurableNotifications();

    /**
     * method used to get the  list of System notification
     * @return
     */
    List<NotificationDto> getSystemNotifications();
}
