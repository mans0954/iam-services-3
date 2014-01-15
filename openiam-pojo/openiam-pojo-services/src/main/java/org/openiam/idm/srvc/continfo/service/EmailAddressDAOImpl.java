package org.openiam.idm.srvc.continfo.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;

@Repository("emailAddressDAO")
public class EmailAddressDAOImpl extends BaseDaoImpl<EmailAddressEntity, String> implements EmailAddressDAO {  

	private static final Log log = LogFactory.getLog(AddressDAOImpl.class);
	
	private String DELETE_BY_USER_ID = "DELETE FROM %s e WHERE e.parent.id = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
	}

    @Override
    protected Criteria getExampleCriteria(final EmailAddressEntity email) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(email.getEmailId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), email.getEmailId()));
        } else {
            if (StringUtils.isNotEmpty(email.getName())) {
                String emailName = email.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(emailName, "*") == 0) {
                    matchMode = MatchMode.END;
                    emailName = emailName.substring(1);
                }
                if (StringUtils.isNotEmpty(emailName) && StringUtils.indexOf(emailName, "*") == emailName.length() - 1) {
                    emailName = emailName.substring(0, emailName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(emailName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", emailName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", emailName));
                    }
                }
            }

            if (email.getParent() != null) {
                if (StringUtils.isNotBlank(email.getParent().getId())) {
                    criteria.add(Restrictions.eq("parent.id", email.getParent().getId()));
                }
            }

            if (email.getMetadataType() != null) {
                if (StringUtils.isNotBlank(email.getMetadataType().getMetadataTypeId())) {
                    criteria.add(Restrictions.eq("metadataType.metadataTypeId", email.getMetadataType().getMetadataTypeId()));
                }
            }
        }
        return criteria;
    }

    @Override
    public void removeByUserId(final String userId) {
    	final Query qry = getSession().createQuery(DELETE_BY_USER_ID);
		qry.setString("userId", userId);
		qry.executeUpdate();
	}


	@Override
	protected String getPKfieldName() {
		return "emailId";
	}
}
