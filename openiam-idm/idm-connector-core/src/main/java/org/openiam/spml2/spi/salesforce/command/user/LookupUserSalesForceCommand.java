package org.openiam.spml2.spi.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.salesforce.command.base.AbstractLookupSalesForceCommand;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;
import org.openiam.spml2.util.msg.ResponseBuilder;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("lookupUserSalesForceCommand")
public class LookupUserSalesForceCommand extends AbstractLookupSalesForceCommand<ProvisionUser> {
    @Override
    protected ExtensibleObject lookupObject(String principalName, ManagedSysEntity managedSys, Set<String> fieldNames) throws ConnectorDataException {
        ExtensibleObject resultObject=null;
        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()),
                    managedSys.getConnectionString(), fieldNames);

            final User user = dao.findByName(principalName);

            if(user != null) {
                resultObject = new ExtensibleObject();
                resultObject.setObjectId(principalName);
                resultObject.getAttributes().add(new ExtensibleAttribute("id", user.getId()));
                for(final Iterator<XmlObject> it = user.getChildren(); it.hasNext();) {
                    final XmlObject node = it.next();
                    resultObject.getAttributes().add(new ExtensibleAttribute(node.getName().getLocalPart(), (node.getValue() != null) ? node.getValue().toString() : null));
                }
            }
            return resultObject;

        }  catch (ConnectionException e) {
            log.error("Connection Exception", e);
            throw new ConnectorDataException(ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
        } catch(SalesForceDataIntegrityException e) {
            log.error("SalesForce Exception", e);
            throw new ConnectorDataException(ErrorCode.INVALID_ATTRIBUTE, e.getMessage());
        } catch(Throwable e) {
            log.error("Unkonwn error", e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
    }
}
