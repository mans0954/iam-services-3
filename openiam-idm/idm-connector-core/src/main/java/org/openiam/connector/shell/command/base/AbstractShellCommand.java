package org.openiam.connector.shell.command.base;

import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.connector.common.command.AbstractCommand;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/19/13
 * Time: 7:26 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractShellCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {

    protected String getAttributeValue(String attributeName, List<ExtensibleAttribute> attrList){
        for (ExtensibleAttribute att : attrList) {
            if(att.getName()!=null){
                if (att.getName().equalsIgnoreCase(attributeName)) {
                    return att.getValue();
                }
            }
        }
        return null;
    }
}
