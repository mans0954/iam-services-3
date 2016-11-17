package org.openiam.mq.constants.queue;


/**
 * Created by alexander on 06/07/16.
 */
public class OpenIAMQueue  {
    // AM
    public static final MqQueue RoleAttributeQueue = new MqQueue("RoleAttributeQueue");
    public static final MqQueue GroupAttributeQueue = new MqQueue("GroupAttributeQueue");
    public static final MqQueue ResourceAttributeQueue = new MqQueue("ResourceAttributeQueue");
    public static final MqQueue OrganizationAttributeQueue = new MqQueue("OrganizationAttributeQueue");


    // user
    public static final MqQueue UserQueue = new MqQueue("UserQueue");
    public static final MqQueue UserAttributeQueue = new MqQueue("UserAttributeQueue");

    // idm
    public static final MqQueue ProvisionQueue = new MqQueue("ProvisionQueue");
    public static final MqQueue ManagedSysQueue = new MqQueue("ManagedSysQueue");
}
