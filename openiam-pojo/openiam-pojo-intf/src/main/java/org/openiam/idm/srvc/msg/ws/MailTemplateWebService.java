package org.openiam.idm.srvc.msg.ws;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/msg/service", name = "MailTemplateWebService")
public interface MailTemplateWebService {
	
	/**
     * method for adding template.
     * @param transientInstance
     * @return
     */
    Response save(MailTemplateDto transientInstance);

    /**
     * method for deleting template.
     * @param id
     */
    Response removeTemplate(String id);

    /**
     * method for retriving template by id .
     * @param id
     * @return
     */
    MailTemplateDto getTemplateById(String id);
    
    List<MailTemplateDto> findBeans(final MailTemplateSearchBean searchBean, final int from, final int size);

}
