package org.openiam.idm.srvc.msg.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;
import org.openiam.idm.srvc.msg.dto.MailTemplateSearchBean;
import org.springframework.stereotype.Repository;

import javax.naming.InitialContext;
import java.util.List;

@Repository
public class MailTemplateDAOImpl extends BaseDaoImpl<MailTemplateEntity, String> implements MailTemplateDAO {

    private static final Log log = LogFactory
            .getLog(MailTemplateDAO.class);

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		final Criteria criteria = super.getCriteria();
		if(searchBean != null) {
			if(searchBean instanceof MailTemplateSearchBean) {
				final MailTemplateSearchBean mailTemplateSearchBean = (MailTemplateSearchBean)searchBean;
			}
		}
		return criteria;
	}

	
}
