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

@Repository("emailAddressDAO")
public class EmailAddressDAOImpl extends BaseDaoImpl<EmailAddressEntity, String> implements EmailAddressDAO {  

	private static final Log log = LogFactory.getLog(AddressDAOImpl.class);

	public EmailAddressEntity findByName(String name, String parentId, String parentType) {


		Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EmailAddressEntity.class)
                .createAlias("parent","p")
                .add(Restrictions.eq("p.userId",parentId))
                .add(Restrictions.eq("parentType",parentType))
                .add(Restrictions.eq("name",name));

		List<EmailAddressEntity> result = (List<EmailAddressEntity>)criteria.list();
		if (result == null || result.size() == 0)
			return null;
		return result.get(0);	

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
                if (StringUtils.isNotBlank(email.getParent().getUserId())) {
                    criteria.add(Restrictions.eq("parentType", email.getParentType()));
                }
            }
        }
        return criteria;
    }

    public Map<String, EmailAddressEntity> findByParent(String parentId, String parentType) {

		
		Map<String, EmailAddressEntity> adrMap = new HashMap<String,EmailAddressEntity>();

		List<EmailAddressEntity> addrList = findByParentAsList(parentId, parentType);
		if (addrList == null)
			return null;
		int size = addrList.size();
		for (int i=0; i<size; i++) {
			EmailAddressEntity adr = addrList.get(i);
			//adrMap.put(adr.getDescription(), adr);
			adrMap.put(adr.getEmailId(), adr);
		}
		if (adrMap.isEmpty())
			return null;
		return adrMap;
	}

	public List<EmailAddressEntity> findByParentAsList(String parentId, String parentType) {


		Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EmailAddressEntity.class)
                .createAlias("parent","p")
                .add(Restrictions.eq("p.userId",parentId))
                .add(Restrictions.eq("parentType",parentType));

		List<EmailAddressEntity> result = (List<EmailAddressEntity>)criteria.list();
		if (result == null || result.size() == 0)
			return null;
		return result;		
	}

	public EmailAddressEntity findDefault(String parentId, String parentType) {


		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(EmailAddressEntity.class)
                .createAlias("parent","p")
                .add(Restrictions.eq("p.userId",parentId))
                .add(Restrictions.eq("parentType",parentType))
                .add(Restrictions.eq("isDefault",1));

		return (EmailAddressEntity)criteria.uniqueResult();
	}

	public void removeByParent(String parentId, String parentType) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("delete org.openiam.idm.srvc.continfo.domain.EmailAddressEntity a " +
				" where a.parent.userId = :parentId and   " +
				" 		a.parentType = :parentType");
		qry.setString("parentId", parentId);
		qry.setString("parentType", parentType);
		qry.executeUpdate();	
		
	}

	
	public void saveEmailAddressMap(String parentId, String parentType, Map<String, EmailAddressEntity> adrMap) {
		// get the current map and then compare each record with the map that has been passed in.
		Map<String, EmailAddressEntity> currentMap =  this.findByParent(parentId, parentType);
		if (currentMap != null) {
			Iterator<EmailAddressEntity> it = currentMap.values().iterator();
			while (it.hasNext()) {
				EmailAddressEntity curEmail =  it.next();
				EmailAddressEntity newEmail = adrMap.get(curEmail.getEmailId());
				if (newEmail == null) {
					delete(curEmail);
				}else {
					this.update(newEmail);
				}
					
			}
		}
		// add the new records in currentMap that are not in the existing records
		Collection<EmailAddressEntity> adrCol = adrMap.values();
		Iterator<EmailAddressEntity> itr = adrCol.iterator();
		while (itr.hasNext()) {
			EmailAddressEntity newEmail = itr.next();
			EmailAddressEntity curEmail = null;
			if (currentMap != null ) {
				curEmail = currentMap.get(newEmail.getEmailId());
			}
			if (curEmail == null) {
				save(newEmail);
			}
		}		
	}

	public EmailAddressEntity[] findByParentAsArray(String parentId, String parentType) {

		List<EmailAddressEntity> result = this.findByParentAsList(parentId, parentType);
		if (result == null || result.size() == 0)
			return null;
		return (EmailAddressEntity[])result.toArray();
	}



	public void saveEmailAddressArray(String parentId, String parentType,	EmailAddressEntity[] emailAry) {
		int len = emailAry.length;

		Map<String, EmailAddressEntity> currentMap =  this.findByParent(parentId, parentType);
		if (currentMap != null) {
			Set<String> keys = currentMap.keySet();
			Iterator<String> it = keys.iterator();
			int ctr = 0;
			while (it.hasNext()) {
				String key = it.next();
				EmailAddressEntity newEmail = getEmailFromArray(emailAry, key);
				EmailAddressEntity curEmail = currentMap.get(key);
				if (newEmail == null) {
					// address was removed - deleted
					delete(curEmail);
				}else if (!curEmail.equals(newEmail)) {
					// object changed
					this.update(newEmail);
				}
			}
		}
		// add the new records in currentMap that are not in the existing records
		for (int i=0; i<len; i++) {
			EmailAddressEntity curAdr = null;
			EmailAddressEntity  email = emailAry[i];
			String key =  email.getEmailId();
			if (currentMap != null )  {
				curAdr = currentMap.get(key);
			}
			if (curAdr == null) {
				// new address object
				save(email);
			}
		}
		
	}

	private EmailAddressEntity getEmailFromArray(EmailAddressEntity[] adrAry, String id) {
		EmailAddressEntity adr = null;
		int len = adrAry.length;
		for (int i=0;i<len;i++) {
			EmailAddressEntity tempAdr = adrAry[i];
			if (tempAdr.getEmailId().equals(id)) {
				return tempAdr;
			}
		}
		return adr;
	}


	@Override
	protected String getPKfieldName() {
		return "emailId";
	}
}
