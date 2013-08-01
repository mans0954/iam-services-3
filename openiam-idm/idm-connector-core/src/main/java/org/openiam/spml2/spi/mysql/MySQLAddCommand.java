package org.openiam.spml2.spi.mysql;

import org.openiam.connector.type.StatusCodeType;
import org.openiam.connector.type.UserRequest;
import org.openiam.connector.type.UserResponse;

/**
 * AppTableAddCommand implements the add operation for the AppTableConnector
 */
public class MySQLAddCommand extends MySQLAbstractCommand {



    public UserResponse add(UserRequest reqType) {


        UserResponse response = new UserResponse();
        response.setStatus(StatusCodeType.SUCCESS);



        return response;
    }


}
