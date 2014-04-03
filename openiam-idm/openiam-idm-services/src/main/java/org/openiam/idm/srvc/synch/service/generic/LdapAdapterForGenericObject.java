/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */
/**
 *
 */
package org.openiam.idm.srvc.synch.service.generic;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.synch.srcadapter.MatchRuleFactory;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Scan Ldap for any new records, changed users, or delete operations and then synchronizes them back into OpenIAM.
 *
 * @author suneet
 */
@Component("genericObjLdapAdapter")
public class LdapAdapterForGenericObject implements SourceAdapter {

    protected LineObject rowHeader = new LineObject();
    protected ProvisionUser pUser = new ProvisionUser();

    @Autowired
    protected LoginDataService loginManager;
    @Autowired
    protected RoleDataService roleDataService;
    @Autowired
    protected MatchRuleFactory matchRuleFactory;

    @Value("${KEYSTORE}")
    private String keystore;

    LdapContext ctx = null;
    @Autowired
    protected UserDataService userMgr;

    private static final Log log = LogFactory
            .getLog(LdapAdapterForGenericObject.class);

    public SyncResponse startSynch(SynchConfig config) {
        // rule used to match object from source system to data in IDM
        MatchObjectRule matchRule = null;
        // String changeLog = null;
        // Date mostRecentRecord = null;
        long mostRecentRecord = 0L;
        String lastRecProcessed = null;

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);

        return resp;

    }

    public Response testConnection(SynchConfig config) {
        try {
            if (connect(config)) {
                closeConnection();
                Response resp = new Response(ResponseStatus.SUCCESS);
                return resp;
            } else {
                Response resp = new Response(ResponseStatus.FAILURE);
                resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
                return resp;
            }
        } catch (NamingException e) {
            e.printStackTrace();
            log.error(e);

            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_CONNECTION);
            resp.setErrorText(e.getMessage());
            return resp;
        }
    }

    private boolean connect(SynchConfig config) throws NamingException {

        Hashtable<String, String> envDC = new Hashtable();
        System.setProperty("javax.net.ssl.trustStore", keystore);

        String hostUrl = config.getSrcHost(); // managedSys.getHostUrl();
        log.debug("Directory host url:" + hostUrl);

        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put(Context.SECURITY_PRINCIPAL, config.getSrcLoginId()); // "administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, config.getSrcPassword());

        if (hostUrl.contains("ldaps")) {

            envDC.put(Context.SECURITY_PROTOCOL, "SSL");
        }

        ctx = new InitialLdapContext(envDC, null);
        if (ctx != null) {
            return true;
        }

        return false;

    }

    private void closeConnection() {
        try {
            if (ctx != null) {
                ctx.close();
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(), ne);
            ne.printStackTrace();
        }

    }
}
