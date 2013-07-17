package org.openiam.spml2.spi.script.command.base;

import org.openiam.script.ScriptIntegration;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.AbstractCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractScriptCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {



}
