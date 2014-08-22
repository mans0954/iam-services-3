package org.openiam.provision.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractPrePostExecutor implements PrePostExecutor {

        protected ApplicationContext context;

        private static final Log log = LogFactory.getLog(AbstractPrePostExecutor.class);


        public void setApplicationContext(ApplicationContext context) {
            this.context = context;
        }

}
