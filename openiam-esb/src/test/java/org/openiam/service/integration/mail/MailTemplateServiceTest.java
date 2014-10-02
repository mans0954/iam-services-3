package org.openiam.service.integration.mail;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;
import org.openiam.idm.srvc.msg.ws.MailTemplateWebService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MailTemplateServiceTest extends AbstractKeyNameServiceTest<MailTemplateDto, MailTemplateSearchBean> {

	@Autowired
	@Qualifier("mailTemplateServiceClient")
	private MailTemplateWebService mailTemplateServiceClient;
	
	@Override
	protected MailTemplateDto newInstance() {
		return new MailTemplateDto();
	}

	@Override
	protected MailTemplateSearchBean newSearchBean() {
		return new MailTemplateSearchBean();
	}

	@Override
	protected Response save(MailTemplateDto t) {
		return mailTemplateServiceClient.save(t);
	}

	@Override
	protected Response delete(MailTemplateDto t) {
		return mailTemplateServiceClient.removeTemplate(t.getId());
	}

	@Override
	protected MailTemplateDto get(String key) {
		return mailTemplateServiceClient.getTemplateById(key);
	}

	@Override
	public List<MailTemplateDto> find(MailTemplateSearchBean searchBean,
			int from, int size) {
		return mailTemplateServiceClient.findBeans(searchBean, from, size);
	}

}
