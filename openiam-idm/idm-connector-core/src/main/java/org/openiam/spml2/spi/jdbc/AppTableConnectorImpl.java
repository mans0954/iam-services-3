/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
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
package org.openiam.spml2.spi.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.SearchRequest;
import org.openiam.connector.type.SearchResponse;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.LookupAttributeRequestType;
import org.openiam.spml2.msg.LookupAttributeResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.common.jdbc.AbstractJDBCConnectorImpl;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Enables provisionign and deprovisioning to custom tables in a database.  This is usually the case when mapping to
 * a custom database structure for an application
 *
 * @author suneet
 */

@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ApplicationTablesConnectorPort",
        serviceName = "ApplicationTablesConnector")
public class AppTableConnectorImpl extends AbstractJDBCConnectorImpl {

    private static final Log log = LogFactory.getLog(AppTableConnectorImpl.class);

    /*
    * (non-Javadoc)
    *
    * @see org.openiam.spml2.interf.SpmlCore#lookupAttributeNames(org.openiam.spml2.msg.
    * LookupAttributeRequestType)
    */
    public LookupAttributeResponseType lookupAttributeNames(LookupAttributeRequestType reqType){
        LookupAttributeResponseType respType = new LookupAttributeResponseType();
        respType.setStatus(StatusCodeType.FAILURE);
        respType.setError(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);

        return respType;
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        throw new UnsupportedOperationException("Not supportable.");
    }

}
