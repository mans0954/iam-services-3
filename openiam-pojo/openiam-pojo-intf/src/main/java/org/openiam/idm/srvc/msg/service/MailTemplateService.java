package org.openiam.idm.srvc.msg.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;

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
    void save(MailTemplateEntity entity);

    /**
     * method for deleting template.
     * @param id
     */
    void delete(String id);

    /**
     * method for retriving template by id .
     * @param id
     * @return
     */
    MailTemplateEntity get(String id);
    
    List<MailTemplateEntity> findBeans(final MailTemplateSearchBean searchBean, final int from, final int size);
}
