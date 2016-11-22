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
import org.openiam.base.ws.SearchParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
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
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof EmailSearchBean) {
			final EmailSearchBean sb = (EmailSearchBean)searchBean;
			
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
	            if (StringUtils.isNotEmpty(sb.getName())) {
	                String emailName = sb.getName();
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

                if (StringUtils.isNotBlank(sb.getParentId())) {
                    criteria.add(Restrictions.eq("parent.id", sb.getParentId()));
                }

                if (StringUtils.isNotBlank(sb.getMetadataTypeId())) {
                    criteria.add(Restrictions.eq("type.id", sb.getMetadataTypeId()));
                }
	            
	            if(sb.getEmailMatchToken() != null && sb.getEmailMatchToken().getMatchType() != null) {
	            	final String emailAddress = sb.getEmailMatchToken().getValue();
	            	switch(sb.getEmailMatchToken().getMatchType()) {
	            		case CONTAINS:
	            			criteria.add(Restrictions.like("emailAddress", emailAddress));
	            			break;
	            		case END_WITH:
	            			criteria.add(Restrictions.like("emailAddress", emailAddress, MatchMode.END));
	            			break;
	            		case EXACT:
	            			criteria.add(Restrictions.eq("emailAddress", emailAddress));
	            			break;
	            		case STARTS_WITH:
	            			criteria.add(Restrictions.like("emailAddress", emailAddress, MatchMode.START));
	            			break;
	            		default:
	            			break;
	            	}
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
		return "id";
	}
}
