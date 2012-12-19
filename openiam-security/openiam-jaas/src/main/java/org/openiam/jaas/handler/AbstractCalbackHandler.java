package org.openiam.jaas.handler;


import org.apache.log4j.Logger;

import javax.security.auth.callback.*;
import java.io.IOException;

public abstract class AbstractCalbackHandler implements CallbackHandler {
    protected final Logger log = Logger.getLogger(this.getClass());

    /* (non-Javadoc)
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    @Override
    public void handle(Callback[] callbackAry) throws IOException, UnsupportedCallbackException {
        log.debug(this.getClass().getSimpleName() + " is called.");

        for(Callback callback: callbackAry)   {
            processCallback(callback);
        }
    }

    protected void processCallback(Callback callback)throws IOException, UnsupportedCallbackException{
        if (callback instanceof TextOutputCallback) {
            log.debug("TextOutputCallback found");

        } else if (callback instanceof NameCallback) {
            log.debug("NameCallback found");
            log.debug("Default name" + ((NameCallback)callback).getDefaultName());
            log.debug("Name" + ((NameCallback) callback).getName());

            ((NameCallback)callback).setName(this.getUserName());

        } else if (callback instanceof PasswordCallback) {
            log.debug("PasswordCallback found");
            ((PasswordCallback)callback).setPassword(this.getPassword());
        } else {
            log.error("CallbackName=" + callback.getClass().getName());
            throw new UnsupportedCallbackException
                    (callback, "Unrecognized Callback");
        }
    }

    protected abstract String getUserName();
    protected abstract char[] getPassword();
    public abstract void clean();
}
