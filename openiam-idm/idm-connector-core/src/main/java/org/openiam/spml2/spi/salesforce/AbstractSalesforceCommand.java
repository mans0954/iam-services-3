package org.openiam.spml2.spi.salesforce;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.spml2.spi.salesforce.exception.SalesForceDataIntegrityException;
import org.openiam.spml2.spi.salesforce.exception.SalesForcePersistException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

public abstract class AbstractSalesforceCommand {
	
	private static final String DATE_FORMAT = "MM/dd/yyyy";
	
	private static final String QUERY_SFQL = "SELECT %s FROM User WHERE %s='%s'";
	
	protected static final Log log = LogFactory.getLog(AbstractSalesforceCommand.class);

    protected ManagedSystemDataService managedSysService;
    protected ResourceDataService resourceDataService;
    
    @Required
    public void setManagedSysService(ManagedSystemDataService managedSysService) {
        this.managedSysService = managedSysService;
    }

    @Required
    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }
    
    protected Object getObject(final String dataType, final String dataValue) throws ParseException {
    	Object retVal = null;
        if(StringUtils.equalsIgnoreCase(dataType, "date")) {
            retVal = new SimpleDateFormat(DATE_FORMAT).parse(dataValue);
        }
        if(StringUtils.equalsIgnoreCase(dataType, "integer")) {
        	retVal = Integer.valueOf(dataValue);
        }

        if(StringUtils.equalsIgnoreCase(dataType, "float")) {
        	retVal = Float.valueOf(dataValue);
        }

        if(StringUtils.equalsIgnoreCase(dataType, "string")) {
        	retVal = dataValue;
        }

        if(StringUtils.equalsIgnoreCase(dataType, "timestamp")) {
        	retVal = Timestamp.valueOf(dataValue);
        }
    	return retVal;
    }
}
