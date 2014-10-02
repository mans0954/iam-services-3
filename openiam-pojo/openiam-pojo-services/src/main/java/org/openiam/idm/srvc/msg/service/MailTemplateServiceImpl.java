package org.openiam.idm.srvc.msg.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.MailTemplateDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {
	
    private static final Log log = LogFactory.getLog(MailTemplateServiceImpl.class);

    @Autowired
    private MailTemplateDAO mailTemplateDAO;

	@Override
	@Transactional
	public void save(MailTemplateEntity entity) {
        if(StringUtils.isBlank(entity.getId())) {
        	mailTemplateDAO.save(entity);
        } else {
        	mailTemplateDAO.merge(entity);
        }
	}

	@Override
	@Transactional
	public void delete(String id) {
    	final MailTemplateEntity entity = mailTemplateDAO.findById(id);
    	if(entity != null) {
    		mailTemplateDAO.delete(entity);
    	}
	}

	@Override
	@Transactional(readOnly=true)
	public MailTemplateEntity get(String id) {
		return mailTemplateDAO.findById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<MailTemplateEntity> findBeans(MailTemplateSearchBean searchBean, int from, int size) {
		final List<MailTemplateEntity> entities = mailTemplateDAO.getByExample(searchBean, from, size);
		return entities;
	}

    
}
