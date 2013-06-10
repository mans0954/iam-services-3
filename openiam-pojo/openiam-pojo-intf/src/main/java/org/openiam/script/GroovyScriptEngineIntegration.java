/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

package org.openiam.script;

import groovy.lang.*;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.openiam.exception.ScriptEngineException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

/* 
 * Spring bean created through XML. Not using annotations, as the actual implementation of the 
 * interface is configurable via the properties file
 */
public class GroovyScriptEngineIntegration implements ScriptIntegration, ApplicationContextAware {

    public static final String APP_CONTEXT = "appContext";

    private ApplicationContext ac;

	@Value("${org.openiam.groovy.script.root}")
	private String scriptRoot;
	
    protected static final Log log = LogFactory.getLog(GroovyScriptEngineIntegration.class);

    private GroovyScriptEngine gse = null;
    
    @PostConstruct
    public void init() throws IOException {
    	gse = new GroovyScriptEngine(new String[]{scriptRoot});
    }

    @Override
    public Object execute(Map<String, Object> bindingMap, String scriptName) throws ScriptEngineException {
        try {
            Binding binding = new Binding(bindingMap);

            // make application context accessible from all groovy scripts
            binding.setVariable(APP_CONTEXT, ac);

            gse.run(scriptName, binding);
            return binding.getVariable("output");

        } catch (ScriptException se) {
            String msg = "Could not run script " + scriptName;
            log.error(msg, se);
            throw new ScriptEngineException(msg, se);
        } catch (ResourceException re) {
            String msg = "Resource problem for " + scriptName;
            log.error(msg, re);
            throw new ScriptEngineException(msg, re);
        }
    }

    @Override
    public Object instantiateClass(Map<String, Object> bindingMap, String scriptName) throws IOException {
        log.info("instantiateClass called.");

        try {
            String fullPath = scriptRoot + scriptName;
            Class cl = gse.loadScriptByName(fullPath);
            Object instance = cl.newInstance();

            try {
                InvokerHelper.setAttribute(instance, APP_CONTEXT, ac);
            } catch (Exception e) {
                log.info("Ignoring field " + APP_CONTEXT + " in script " + scriptName + ", error: " + e.toString());
            }

            if (bindingMap != null) {
                for (String key : bindingMap.keySet()) {
                    try {
                        InvokerHelper.setAttribute(instance, key, bindingMap.get(key));
                    } catch (Exception e) {
                        log.info("Ignoring field " + key + " in script " + scriptName + ", error: " + e.toString());
                    }
                }
            }

            return instance;

        } catch (ResourceException e) {
            log.error("Resource problems when instantiating class " + scriptName, e);
        } catch (ScriptException e) {
            log.error("Script problems when instantiating class " + scriptName, e);
        } catch (IllegalAccessException ia) {
            log.error("Access problems when instantiating class " + scriptName, ia);
        } catch (InstantiationException ia) {
            log.error("Instantiation problems when instantiating class " + scriptName, ia);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

}
