package org.openiam.am.srvc.resattr.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.Attribute;
import org.openiam.am.srvc.dto.AttributeMap;
import org.openiam.am.srvc.resattr.dto.WebResourceResponse;
import org.openiam.am.srvc.service.WebResourceAttributeService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@Service("webResource")
@Deprecated
@WebService(endpointInterface = "org.openiam.am.srvc.resattr.ws.WebResourceService",
            targetNamespace = "urn:idm.openiam.org/srvc/res/service", portName = "WebResourceServicePort",
            serviceName = "WebResourceService")
public class WebResourceServiceImpl implements WebResourceService {
    protected final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private WebResourceAttributeService webResourceAttributeService;

    @Override
    public AttributeMap getAttributeMap(@WebParam(name = "attributeId", targetNamespace = "") String attributeId)
            throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got getAttributeMap request. Params: attributeId="+attributeId);
    	}
        return webResourceAttributeService.getAttributeMap(attributeId);
    }

    @Override
    public List<AttributeMap> getAttributeMapCollection(
            @WebParam(name = "resourceId", targetNamespace = "") String resourceId) throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got getAttributeMapCollection request. Params: resourceId="+resourceId);
    	}
        return webResourceAttributeService.getAttributeMapCollection(resourceId);
    }

    @Override
    public AttributeMap addAttributeMap(@WebParam(name = "attribute", targetNamespace = "") AttributeMap attribute)
            throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got addAttributeMap request. Params: attribute="+attribute.toString());
    	}
        return webResourceAttributeService.addAttributeMap(attribute);
    }

    @Override
    public Response addAttributeMapCollection(
            @WebParam(name = "attributeList", targetNamespace = "") List<AttributeMap> attributeList) throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got addAttributeMapCollection request. Params: attributeList="+attributeList.toString());
    	}
        Response resp = new Response(ResponseStatus.SUCCESS);
        try {
         webResourceAttributeService.addAttributeMapCollection(attributeList);
        } catch (Exception ex) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorText(ex.getMessage());
        }
        return resp;
    }

    @Override
    public AttributeMap updateAttributeMap(@WebParam(name = "attribute", targetNamespace = "") AttributeMap attribute)
            throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got updateAttributeMap request. Params: attribute="+attribute.toString());
    	}
        return webResourceAttributeService.updateAttributeMap(attribute);
    }

    @Override
    public void removeAttributeMap(@WebParam(name = "attributeId", targetNamespace = "") String attributeId)
            throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got removeAttributeMap request. Params: attributeId="+attributeId);
    	}
        webResourceAttributeService.removeAttributeMap(attributeId);
    }

    @Override
    public int removeResourceAttributeMaps(@WebParam(name = "resourceId", targetNamespace = "") String resourceId)
            throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Got removeResourceAttributeMaps request. Params: resourceId="+resourceId);
    	}
        return webResourceAttributeService.removeResourceAttributeMaps(resourceId);
    }

    @Override
    public WebResourceResponse getSSOAttributes(@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                                @WebParam(name = "principalName", targetNamespace = "")
                                                String principalName,
                                                @WebParam(name = "managedSysId", targetNamespace = "")
                                                String managedSysId) {
        StringBuilder msg = new StringBuilder();
        msg.append("Try to get SSO attributes for { resource id:").append(resourceId)
                .append(", principalName: ").append(principalName).append(", managedSysId:")
           .append(managedSysId).append("}");
        if(log.isDebugEnabled()) {
        	log.debug(msg.toString());
        }

        WebResourceResponse resp = new WebResourceResponse(ResponseStatus.SUCCESS);
        List<Attribute> attributeList = webResourceAttributeService
                .getSSOAttributes(resourceId, principalName, managedSysId);

        if (attributeList == null || attributeList.isEmpty()) {
        	if(log.isDebugEnabled()) {
        		log.debug("There no any attributes for given resource id:" + resourceId);
        	}
            resp.setStatus(ResponseStatus.FAILURE);
        } else {
        	if(log.isDebugEnabled()) {
	            log.debug("Attribute datas has been found for given resource id:" + resourceId + "; Attributes count: "
	                      + attributeList.size());
        	}
            resp.setAttributeList(attributeList);
        }
        return resp;
    }
}




