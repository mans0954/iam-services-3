package org.openiam.spml2.spi.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.CrudRequestType;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.salesforce.dao.BaseSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.BaseModel;
import org.openiam.spml2.spi.salesforce.model.User;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/16/13
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSalesForceInsertCommand<ProvisionObject extends GenericProvisionObject,
                                                      Request extends CrudRequestType<ProvisionObject>,
                                                      Response extends ResponseType> extends AbstractSalesforceCommand<Request, Response>  {

    protected void insertOrUpdate(final String principalName, final List<ExtensibleObject> objectList, final ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            final Set<String> fieldNames = new HashSet<String>();
            final BaseModel model = getObjectModel(principalName);

            if(CollectionUtils.isNotEmpty(objectList)) {
                for (final ExtensibleObject obj : objectList) {
                    final List<ExtensibleAttribute> attrList = obj.getAttributes();
                    if(CollectionUtils.isNotEmpty(attrList)) {
                        for (final ExtensibleAttribute att : attrList) {
                            final Object value = getObject(att.getDataType(), att.getValue());
                            model.setField(att.getName(), att.getValue());
                            fieldNames.add(att.getName());
                        }
                    }
                    if(StringUtils.isNotBlank(obj.getPrincipalFieldName())) {
                        fieldNames.add(obj.getPrincipalFieldName());
                    }
                    /*
                    if(StringUtils.isNotBlank(obj.getPrincipalFieldName())) {
                        final Object value = getObject(obj.getPrincipalFieldDataType(), principalName);
                        user.setField(obj.getPrincipalFieldName(), value);
                    }
                    */
                }
            }

            /* sales force has a bug - if the ProfileId attribute is blank when sending the User, the User isn't saved, but no exception is thrown */
            if(StringUtils.isBlank(model.getProfileId())) {
                throw new ConnectorDataException(ErrorCode.PERSIST_EXCEPTION, "No ProfileId specified");
            }

            final BaseSalesForceDao dao = getObjectDao(managedSys.getUserId(), this.getDecryptedPassword(managedSys.getUserId(), managedSys.getPswd()), managedSys.getConnectionString(), fieldNames);

            /* The UI defines the attribute name as ProfileId.  However, the SOAP UI defines it as a Profile Name.  Up until this point, it is a name, and not an
             * ID.  Find the corresponding ID using SF API
             */
            final String profileName = StringUtils.trimToNull(model.getProfileId());
            final String profileId;

                profileId = dao.getProfileIdByName(profileName);

            if(StringUtils.isBlank(profileId)) {
                throw new ConnectorDataException(ErrorCode.PERSIST_EXCEPTION, String.format("No Profile '%s' exists", profileName));
            }
            model.setProfileId(profileId);

            dao.saveOrUpdate(model);
        } catch (SalesForcePersistException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.PERSIST_EXCEPTION, e.getMessage());
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.PARSE_EXCEPTION, e.getMessage());
        } catch (ConnectionException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.AUTHENTICATION_FAILED, e.getMessage());
        }
    }

    protected abstract BaseSalesForceDao getObjectDao(String userId, String decryptPassword, String connectionString, Set<String> fieldNames) throws ConnectionException;
    protected abstract BaseModel getObjectModel(String principalName);
}
