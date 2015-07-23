package org.openiam.provision.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("provDispatcher")
public class ProvisionDispatcher implements Sweepable {

    private static final Log log = LogFactory.getLog(ProvisionDispatcher.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    protected ProvisionConnectorService connectorService;

    @Autowired
    protected ProvisionDispatcherTransactionHelper provisionTransactionHelper;


    private final Object mutex = new Object();


    @Override
    @Scheduled(fixedRateString="${org.openiam.prov.threadsweep}", initialDelayString="${org.openiam.prov.threadsweep}")
    public void sweep() {

        jmsTemplate.browse("provQueue", new BrowserCallback<Object>() {
            @Override
            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
                synchronized (mutex) {
                    final List<ProvisionDataContainer> list = new ArrayList<ProvisionDataContainer>();
                    Enumeration e = browser.getEnumeration();
                    while (e.hasMoreElements()) {
                        list.add((ProvisionDataContainer) ((ObjectMessage) jmsTemplate.receive("provQueue")).getObject());
                        e.nextElement();
                    }

                    process(list);

                    return Boolean.TRUE;
                }
            }
        });

    }

    public void process(final List<ProvisionDataContainer> entities) {
        for (final ProvisionDataContainer data : entities) {
            provisionTransactionHelper.process(data);
            try {
                //chance to other threads to be executed
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        log.debug("updateAttributeList: Updating operations on attributes being passed to connectors");

        List<ExtensibleAttribute> extAttrList = extObject.getAttributes();
        if (extAttrList == null) {
            log.debug("Extended object attributes is null");
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
                        log.debug("- Op = 0 - AttrName = " + nm);
                        attr.setOperation(AttributeOperationEnum.NO_CHANGE.getValue());
                    } else if (curAttr == null || !curAttr.containsAnyValue()) {
                        log.debug("- Op = 1 - AttrName = " + nm);
                        attr.setOperation(AttributeOperationEnum.ADD.getValue());
                    } else if (!attr.containsAnyValue() && curAttr.containsAnyValue()) {
                        log.debug("- Op = 3 - AttrName = " + nm);
                        attr.setOperation(AttributeOperationEnum.DELETE.getValue());
                    } else if(attr.containsAnyValue() && curAttr.containsAnyValue()) {
                        log.debug("- Op = 2 - AttrName = " + nm);
                        attr.setOperation(AttributeOperationEnum.REPLACE.getValue());
                    }
                }
            }
        }
        return extObject;
    }

}
