package org.openiam.spml2.spi.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.salesforce.command.base.AbstractAddSalesForceCommand;
import org.openiam.spml2.spi.salesforce.dao.BaseSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.model.BaseModel;
import org.openiam.spml2.spi.salesforce.model.User;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addUserSalesForceCommand")
public class AddUserSalesForceCommand extends AbstractAddSalesForceCommand<ProvisionUser> {
    @Override
    protected BaseSalesForceDao getObjectDao(String userId, String decryptPassword, String connectionString, Set<String> fieldNames) throws ConnectionException {
        return new CallerDependentSalesForceDao(userId, decryptPassword, connectionString, fieldNames);
    }

    @Override
    protected BaseModel getObjectModel(String principalName) {
        return new User(principalName);
    }
}
