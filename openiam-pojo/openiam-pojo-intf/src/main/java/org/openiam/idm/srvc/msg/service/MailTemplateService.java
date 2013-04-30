package org.openiam.idm.srvc.msg.service;

import org.openiam.idm.srvc.msg.dto.MailTemplateDto;

import java.util.List;

/**
 * @author suneet
 *
 *this service provides method for create, delete, update, list all the mail template
 */
public interface MailTemplateService {

    /**
     * method for adding template.
     * @param transientInstance
     * @return
     */
    public MailTemplateDto addTemplate(MailTemplateDto transientInstance);

    /**
     * method for deleting template.
     * @param id
     */
    public void removeTemplate(String id);

    /**
     * method for updating template.
     * @param detachedInstance
     * @return
     */
    public MailTemplateDto updateTemplate(MailTemplateDto detachedInstance);

    /**
     * method for retriving template by id .
     * @param id
     * @return
     */
    public MailTemplateDto getTemplateById(java.lang.String id);


    /**
     * method for getting the list of all template.
     * @return
     */
    public List<MailTemplateDto> getAllTemplates();
}
