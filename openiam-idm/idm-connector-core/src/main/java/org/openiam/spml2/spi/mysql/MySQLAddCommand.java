package org.openiam.spml2.spi.mysql;

import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;

/**
 * AppTableAddCommand implements the add operation for the AppTableConnector
 */
public class MySQLAddCommand extends MySQLAbstractCommand {



    public ObjectResponse add(CrudRequest reqType) {


        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);



        return response;
    }


}
