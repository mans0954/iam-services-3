package org.openiam.idm.srvc.audit.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * Factory instantiates the appropriate ExportAuditEvent object
 * User: suneetshah
 * Date: 9/18/11
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AuditEventHandlerFactory implements BeanFactoryAware {

	@Value("${EXPORT_AUDIT_EVENT_HANDLER}")
	private String handlerName;
	
	private BeanFactory beanFactory;
	
    private static final Log log = LogFactory.getLog(AuditEventHandlerFactory.class);

    public ExportAuditEvent createInstance() {


		try {
			
			String beanName = handlerName;
			
			/* in case the property has a classname instead of spring bean name */
			try {
				beanName = Class.forName(handlerName).getAnnotation(Component.class).value();
			} catch(Throwable e) {
				
			}
			
			return beanFactory.getBean(beanName, ExportAuditEvent.class);
        } catch(Throwable e ) {
        	log.error(e);
        }
		return null;


    }

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}


}
