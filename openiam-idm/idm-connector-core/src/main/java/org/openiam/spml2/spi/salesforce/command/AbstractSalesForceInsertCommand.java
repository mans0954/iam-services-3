package org.openiam.spml2.spi.salesforce.command;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.connector.salesforce.dao.SalesForceDao;
import org.openiam.connector.salesforce.exception.SalesForcePersistException;
import org.openiam.connector.salesforce.model.User;

import com.sforce.ws.ConnectionException;
import org.openiam.spml2.spi.salesforce.command.AbstractSalesforceCommand;

@Deprecated
public class AbstractSalesForceInsertCommand extends AbstractSalesforceCommand {

	protected void insertOrUpdate(final String principalName, final ExtensibleObject obj, final ManagedSysDto managedSys) throws ParseException, ConnectionException, SalesForcePersistException {
    	final Set<String> fieldNames = new HashSet<String>();
		final User user = new User(principalName);


        final List<ExtensibleAttribute> attrList = obj.getAttributes();
        if (CollectionUtils.isNotEmpty(attrList)) {
            for (final ExtensibleAttribute att : attrList) {
                //TODO for what here the 'value' ?
                final Object value = getObject(att.getDataType(), att.getValue());
                user.setField(att.getName(), att.getValue());
                fieldNames.add(att.getName());
            }
        }
        if (StringUtils.isNotBlank(obj.getPrincipalFieldName())) {
            fieldNames.add(obj.getPrincipalFieldName());
        }
        /*
                  if(StringUtils.isNotBlank(obj.getPrincipalFieldName())) {
                      final Object value = getObject(obj.getPrincipalFieldDataType(), principalName);
                      user.setField(obj.getPrincipalFieldName(), value);
                  }
                  */


        /* sales force has a bug - if the ProfileId attribute is blank when sending the User, the User isn't saved, but no exception is thrown */
		if(StringUtils.isBlank(user.getProfileId())) {
			throw new SalesForcePersistException("No ProfileId specified");
		}  
		
		final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(), managedSys.getConnectionString(), fieldNames);
		
		/* The UI defines the attribute name as ProfileId.  However, the SOAP UI defines it as a Profile Name.  Up until this point, it is a name, and not an
		 * ID.  Find the corresponding ID using SF API
		 */
		final String profileName = StringUtils.trimToNull(user.getProfileId());
		final String profileId = dao.getProfileIdByName(profileName);
		if(StringUtils.isBlank(profileId)) {
			throw new SalesForcePersistException(String.format("No Profile '%s' exists", profileName));
		}
		user.setProfileId(profileId);
		
		dao.saveOrUpdate(user);
	}
}
