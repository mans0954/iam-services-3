package org.openiam.spml2.spi.salesforce;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.spi.salesforce.dao.CallerDependentSalesForceDao;
import org.openiam.spml2.spi.salesforce.dao.SalesForceDao;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.openiam.spml2.spi.salesforce.model.User;

import com.sforce.ws.ConnectionException;

public class AbstractSalesForceInsertCommand extends AbstractSalesforceCommand {

	protected void insertOrUpdate(final String principalName, final List<ExtensibleObject> objectList, final ManagedSys managedSys) throws ParseException, ConnectionException, SalesForcePersistException {
    	final Set<String> fieldNames = new HashSet<String>();
		final User user = new User(principalName);
		
		if(CollectionUtils.isNotEmpty(objectList)) {
			for (final ExtensibleObject obj : objectList) {
				final List<ExtensibleAttribute> attrList = obj.getAttributes();
				if(CollectionUtils.isNotEmpty(attrList)) {
					for (final ExtensibleAttribute att : attrList) {
						final Object value = getObject(att.getDataType(), att.getValue());
						user.setField(att.getName(), att.getValue());
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
		
		log.info(String.format("Saving user: %s", user));
		final SalesForceDao dao = new CallerDependentSalesForceDao(managedSys.getUserId(), managedSys.getDecryptPassword(), managedSys.getConnectionString(), fieldNames);
		dao.saveOrUpdate(user);
	}
}
