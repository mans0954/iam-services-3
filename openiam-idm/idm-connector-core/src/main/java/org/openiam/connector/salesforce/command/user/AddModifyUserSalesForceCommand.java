package org.openiam.connector.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.connector.salesforce.command.base.AbstractAddModifySalesForceCommand;
import org.openiam.connector.salesforce.dao.BaseSalesForceDao;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.model.BaseModel;
import org.openiam.connector.salesforce.model.User;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addModifyUserSalesForceCommand")
public class AddModifyUserSalesForceCommand extends AbstractAddModifySalesForceCommand<ExtensibleUser> {
    @Override
    protected BaseSalesForceDao getObjectDao(String userId, String decryptPassword, String connectionString, Set<String> fieldNames) throws ConnectionException {
        return new CallerDependentSalesForceDao(userId, decryptPassword, connectionString, fieldNames);
    }

    @Override
    protected BaseModel getObjectModel(String principalName) {
        return new User(principalName);
    }
}
