package org.openiam.util;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;

public class MuleContextProvider implements MuleContextAware {

    private static MuleContext ctx;
    @Override
    public void setMuleContext(MuleContext muleContext) {
        ctx =muleContext;
    }

    public static MuleContext getCtx() {
        return ctx;
    }

}
