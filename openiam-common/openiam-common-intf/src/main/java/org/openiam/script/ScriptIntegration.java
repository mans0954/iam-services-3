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

/**
 *
 */
package org.openiam.script;

import java.io.IOException;
import java.util.Map;
import org.openiam.exception.ScriptEngineException;

/**
 * Interface that all script integration modules must implement
 *
 * @author suneet
 */
public interface ScriptIntegration {

    String evaluate(Map<String, Object> bindingMap, String gstring) throws IOException;

    Object execute(Map<String, Object> bindingMap, String scriptName) throws ScriptEngineException;

    Object instantiateClass(Map<String, Object> bindingMap, String scriptName) throws IOException;

    Object instantiateClass(Map<String, Object> bindingMap, String storageDirectory, String scriptName) throws IOException;

    boolean scriptExists(final String scriptName);
}
