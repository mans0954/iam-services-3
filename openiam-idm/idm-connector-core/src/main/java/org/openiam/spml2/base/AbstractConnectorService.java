package org.openiam.spml2.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.spml2.ConnectorCommandFactory;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.interf.ConnectorService;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

public abstract class AbstractConnectorService implements ConnectorService,ApplicationContextAware {
    protected final Log log = LogFactory.getLog(this.getClass());

    protected ConnectorType connectorType;
    private ApplicationContext applicationContext;

    @Autowired
    private ConnectorCommandFactory connectorCommandFactory;

    @PostConstruct
    public void init() {
        this.initConnectorType();
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected  <Response extends ResponseType> Response manageRequest(CommandType commandType, RequestType requestType, Class<Response> responseClass){
        Response response = null;
        try {
            response = responseClass.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if(response!=null){
            log.debug(String.format("%s request proceed in %s connector for %s object", commandType, connectorType, requestType.getProvisionObject().getProvisionObjectType()));
            try {
                ConnectorCommand cmd = connectorCommandFactory.getConnectorCommand(commandType, requestType.getProvisionObject().getProvisionObjectType(), this.connectorType);
                response = (Response)cmd.execute(requestType);

            } catch (ConnectorDataException e) {
                log.error(e.getMessage(), e);
                response.setStatus(StatusCodeType.FAILURE);
                response.setError(e.getCode());
                response.addErrorMessage(e.getMessage());
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                response.setStatus(StatusCodeType.FAILURE);
                response.setError(ErrorCode.CONNECTOR_ERROR);
                response.addErrorMessage(t.getMessage());
            }
        }
        return response;
    }

    protected abstract void initConnectorType();


}
