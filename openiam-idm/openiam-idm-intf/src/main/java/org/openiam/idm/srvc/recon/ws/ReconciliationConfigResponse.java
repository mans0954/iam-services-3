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
package org.openiam.idm.srvc.recon.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;

import java.util.List;


/**
 * Response object for a web service operation that returns a role.
 * @author suneet
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReconciliationConfigResponse", propOrder = {
    "config",
    "configList"
})
public class ReconciliationConfigResponse extends Response{

	ReconciliationConfig config;
    List<ReconciliationConfig> configList;

	public ReconciliationConfigResponse() {
		super();
	}

	public ReconciliationConfigResponse(ResponseStatus s) {
		super(s);
	}

	public ReconciliationConfig getConfig() {
		return config;
	}

	public void setConfig(ReconciliationConfig config) {
		this.config = config;
	}

    public List<ReconciliationConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<ReconciliationConfig> configList) {
        this.configList = configList;
    }
}
