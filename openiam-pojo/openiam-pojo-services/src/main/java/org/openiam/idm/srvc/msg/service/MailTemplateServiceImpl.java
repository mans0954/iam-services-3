package org.openiam.idm.srvc.msg.service;

import org.openiam.dozer.converter.MailTemplateDozerConverter;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class MailTemplateServiceImpl implements MailTemplateService {

    @Autowired
    private MailTemplateDozerConverter mailTemplateDozerConverter;

    private MailTemplateDAO mailTemplateDAO;

    @Override
    @Transactional
    public MailTemplateDto addTemplate(MailTemplateDto transientInstance) {
        if (transientInstance == null) {
            throw new NullPointerException("Config object is null");
        }

        MailTemplateEntity mailTemplateEntity = mailTemplateDAO.add(mailTemplateDozerConverter.convertToEntity(transientInstance, true));
        return mailTemplateDozerConverter.convertToDTO(mailTemplateEntity, true);
    }

    @Override
    @Transactional
    public void removeTemplate(String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
        mailTemplateEntity.setTmplId(id);
        mailTemplateDAO.remove(mailTemplateEntity);
    }

    @Override
    @Transactional
    public MailTemplateDto updateTemplate(MailTemplateDto detachedInstance) {
        if (detachedInstance == null) {
            throw new NullPointerException("policy is null");
        }
        MailTemplateEntity sysMessageEntity = mailTemplateDAO.update(mailTemplateDozerConverter.convertToEntity(detachedInstance, true));
        return mailTemplateDozerConverter.convertToDTO(sysMessageEntity, true);
    }

    @Override
    @Transactional (readOnly = true)
    public MailTemplateDto getTemplateById(String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        return mailTemplateDozerConverter.convertToDTO(mailTemplateDAO.findById(id), true);
    }

    @Override
    @Transactional (readOnly = true)
    public List<MailTemplateDto> getAllTemplates() {
        return mailTemplateDozerConverter.convertToDTOList(mailTemplateDAO.findAll(), true);
    }

    public MailTemplateDAO getMailTemplateDAO() {
        return mailTemplateDAO;
    }

    public void setMailTemplateDAO(MailTemplateDAO mailTemplateDAO) {
        this.mailTemplateDAO = mailTemplateDAO;
    }

}
