package org.openiam.connector.salesforce.command.base;

import org.apache.commons.lang.StringUtils;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.common.command.AbstractCommand;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/15/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSalesforceCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {
    private static final String DATE_FORMAT = "MM/dd/yyyy";


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
