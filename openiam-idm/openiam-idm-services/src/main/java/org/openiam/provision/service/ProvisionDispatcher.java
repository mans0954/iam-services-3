package org.openiam.provision.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component("provDispatcher")
public class ProvisionDispatcher {

    private static final Log log = LogFactory.getLog(ProvisionDispatcher.class);

    @Autowired
    protected ProvisionConnectorService connectorService;

    @Autowired
    protected ProvisionDispatcherTransactionHelper provisionTransactionHelper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisMessageListenerContainer listener;

    @PostConstruct
    public void init() {
    	listener.addMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message, byte[] pattern) {
				final ProvisionDataContainer entity = (ProvisionDataContainer)redisTemplate.getDefaultSerializer().deserialize(message.getBody());
				provisionTransactionHelper.process(entity);
			}
		}, Arrays.asList(new Topic[] { new ChannelTopic("provQueue")}));
    }

    /**
     * Update the list of attributes with the correct operation values so that
     * they can be passed to the connector
     */
    static ExtensibleObject updateAttributeList(org.openiam.provision.type.ExtensibleObject extObject,
                                                Map<String, ExtensibleAttribute> currentValueMap) {
        if (extObject == null) {
            return null;
        }
        if(log.isDebugEnabled()) {
        	log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");
        }

        List<ExtensibleAttribute> extAttrList = extObject.getAttributes();
        if (extAttrList == null) {
        	if(log.isDebugEnabled()) {
        		log.debug("Extended object attributes is null");
        	}
            return null;
        }

        if (currentValueMap == null) {
            for (ExtensibleAttribute attr : extAttrList) {
                if(attr.getOperation() == -1) {
                    attr.setOperation(AttributeOperationEnum.ADD.getValue());
                }
            }
        } else {
            for (ExtensibleAttribute attr : extAttrList) {
                if(attr.getOperation() == -1) {
                    String nm = attr.getName();
                    ExtensibleAttribute curAttr = currentValueMap.get(nm);
                    attr.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
                    if (attr.valuesAreEqual(curAttr)) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("- Op = 0 - AttrName = " + nm);
                    	}
                        attr.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
                    } else if (curAttr == null || !curAttr.containsAnyValue()) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("- Op = 1 - AttrName = " + nm);
                    	}
                        attr.setOperation(AttributeOperationEnum.ADD.getValue());
                    } else if (!attr.containsAnyValue() && curAttr.containsAnyValue()) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("- Op = 3 - AttrName = " + nm);
                    	}
                        attr.setOperation(AttributeOperationEnum.DELETE.getValue());
                    } else if (attr.containsAnyValue() && curAttr.containsAnyValue()) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("- Op = 2 - AttrName = " + nm);
                    	}
                        attr.setOperation(AttributeOperationEnum.REPLACE.getValue());
                    }
                }
            }
        }
        return extObject;
    }

}
