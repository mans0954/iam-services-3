package org.openiam.spml2.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.spi.csv.CSVConnectorImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

public abstract class AbstractConnectorService implements ConnectorService,ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());

    protected ConnectorType connectorType;
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        this.initConnectorType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected abstract void initConnectorType();


}
