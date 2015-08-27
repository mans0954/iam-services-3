package org.openiam.idm.srvc.synch.rs;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openiam.idm.srvc.synch.ws.AsynchIdentitySynchService;
import org.openiam.idm.srvc.synch.ws.IdentitySynchWebService;
import org.openiam.idm.srvc.synch.ws.SynchConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.PathParam;

/**
 * Created by Vitaly on 8/24/2015.
 */
@Path("/synch")
@Scope("prototype")
@Component("synchRestService")
public class SynchRestEndpoint {
    @Autowired
    private IdentitySynchWebService synchConfigServiceClient;

    @Autowired
    private AsynchIdentitySynchService asynchIdentitySynchService;

    @POST
    @Path("/run/{synchId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response execute(final @PathParam(value="synchId") String synchId,
                          final @FormParam("json") String jsonValue) {
        Response ajaxResponse = Response.accepted().build();
        try {
            if (StringUtils.isNotBlank(synchId)) {
                SynchConfigResponse res = synchConfigServiceClient.findById(synchId);
                if (res.isSuccess()) {
                    org.openiam.base.ws.Response testResponse = synchConfigServiceClient.testConnection(res.getConfig());
                    if (testResponse.isSuccess()) {
                        asynchIdentitySynchService.startCustomSynchronization(res.getConfig(), jsonValue);
                        ajaxResponse = Response.status(200).entity("synch/run/ is called, jsonParams : " + jsonValue + ", synchId=" + synchId).build();
                    } else {
                        ajaxResponse = Response.status(200).entity("WARN: Synch config ID=" + synchId + " - testConnection failed.").build();
                    }
                 } else {
                    ajaxResponse = Response.status(200).entity("WARN: Synch config ID=" + synchId + " is not available.").build();
                }
            }
        } catch (Exception e) {
            ajaxResponse = Response.status(200)
                    .entity(e.getLocalizedMessage())
                    .build();
        }

        return ajaxResponse;

    }

}
