package org.openiam.connector.salesforce.command.base;

import com.sforce.ws.ConnectionException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.salesforce.dao.BaseSalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.BaseModel;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/8/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractAddModifySalesForceCommand<ExtObject extends ExtensibleObject> extends AbstractSalesforceCommand<CrudRequest<ExtObject>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = crudRequest.getTargetID();
        ConnectorConfiguration configuration = this.getConfiguration(targetID, ConnectorConfiguration.class);
        final String principalName = crudRequest.getObjectIdentity();
        try {

            final ExtObject object = crudRequest.getExtensibleObject();
            this.insertOrUpdate(principalName, object, configuration.getManagedSys());
            //com.sforce.soap.partner.sobject.SObject
            //partnerConnection.create(sObjects);
        }  catch(Throwable e) {
            log.error("Unkonwn error", e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
        return response;
    }


    protected void insertOrUpdate(final String principalName, final ExtObject object, final ManagedSysEntity managedSys) throws ConnectorDataException {
        try {
            final Set<String> fieldNames = new HashSet<String>();
            final BaseModel model = getObjectModel(principalName);

            final List<ExtensibleAttribute> attrList = object.getAttributes();
            if(CollectionUtils.isNotEmpty(attrList)) {
                for (final ExtensibleAttribute att : attrList) {
                    final Object value = getObject(att.getDataType(), att.getValue());
                    model.setField(att.getName(), att.getValue());
                    fieldNames.add(att.getName());
                }
            }
            if(StringUtils.isNotBlank(object.getPrincipalFieldName())) {
                fieldNames.add(object.getPrincipalFieldName());
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
