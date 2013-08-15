package org.openiam.connector.salesforce.command.user;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.salesforce.command.base.AbstractLookupSalesForceCommand;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.dao.SalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.connector.salesforce.model.User;
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
public class LookupUserSalesForceCommand extends AbstractLookupSalesForceCommand<ExtensibleUser> {
    @Override
    protected ObjectValue lookupObject(String principalName, ManagedSysEntity managedSys, Set<String> fieldNames) throws ConnectorDataException {
        ObjectValue resultObject=null;
        try {
            final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), getDecryptedPassword(managedSys.getPswd()),
                    managedSys.getConnectionString(), fieldNames);

            final User user = dao.findByName(principalName);

            if(user != null) {
                resultObject = new ObjectValue();
                resultObject.setObjectIdentity(principalName);
                resultObject.getAttributeList().add(new ExtensibleAttribute("id", user.getId()));
                for(final Iterator<XmlObject> it = user.getChildren(); it.hasNext();) {
                    final XmlObject node = it.next();
                    resultObject.getAttributeList().add(new ExtensibleAttribute(node.getName().getLocalPart(), (node.getValue() != null) ? node.getValue().toString() : null));
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
