package org.openiam.idm.srvc.synch.rs;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.synch.service.AsynchIdentitySynchDataService;
import org.openiam.idm.srvc.synch.service.IdentitySynchService;
import org.openiam.base.response.SynchConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Vitaly on 8/24/2015.
 */
@RestController("/synch")
public class SynchRestEndpoint {

    @Autowired
    protected IdentitySynchService synchService;

    @Autowired
    private AsynchIdentitySynchDataService asynchIdentitySynchService;

    @RequestMapping(value="/run/{synchId}", method=RequestMethod.POST)
    public @ResponseBody Response execute(final @PathVariable(value="synchId") String synchId,
                                          final @RequestParam(value="json") String jsonValue) {
        Response ajaxResponse = Response.accepted().build();
        try {
            if (StringUtils.isNotBlank(synchId)) {
                SynchConfigResponse res = synchService.findDTOById(synchId);
                if (res.isSuccess()) {
                    org.openiam.base.ws.Response testResponse = synchService.testConnection(res.getConfig());
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
