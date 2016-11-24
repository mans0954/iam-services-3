package org.openiam.idm.srvc.loc.service;

// Generated May 9, 2009 1:42:34 PM by Hibernate Tools 3.2.2.GA

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DAO to manage the list of Locations
 * @see org.openiam.idm.srvc.loc.dto.Location
 * @author Suneet shah
 */
@Repository("locationDAO")
public class LocationDAOImpl extends BaseDaoImpl<LocationEntity, String> implements LocationDAO {

    private static final Log log = LogFactory.getLog(LocationDAOImpl.class);

    private String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s e WHERE e.organizationId = :organizationId";


    @PostConstruct
    public void initSQL() {
        DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
    }
    
    

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof LocationSearchBean) {
			final LocationSearchBean sb = (LocationSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
				if (StringUtils.isNotBlank(sb.getOrganizationId())) {
					criteria.add(Restrictions.eq("organization.id", sb.getOrganizationId()));
	            }
	            if (StringUtils.isNotEmpty(sb.getName())) {
	                String name = sb.getName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(name, "*") == 0) {
	                    matchMode = MatchMode.START;
	                    name = name.substring(1);
	                }
	                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
	                    name = name.substring(0, name.length() - 1);
	                    matchMode = (matchMode == MatchMode.START) ? MatchMode.ANYWHERE : MatchMode.END;
	                }

	                if (StringUtils.isNotEmpty(name)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("name", name, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("name", name));
	                    }
	                }
	            }

                if (StringUtils.isNotBlank(sb.getCountry())) {
                    criteria.add(Restrictions.eq("country", sb.getCountry()));
                }

                if (StringUtils.isNotBlank(sb.getCity())) {
                    criteria.add(Restrictions.eq("city", sb.getCity()));
                }
			}
		}
		return criteria;
	}

    @Override
    public void removeByOrganizationId(final String userId) {
        final Query qry = getSession().createQuery(DELETE_BY_ORGANIZATION_ID);
        qry.setString("locationId", userId);
        qry.executeUpdate();
    }

    public List<LocationEntity> findByOrganizationList(Set<String> orgsId) {
        return findByOrganizationList(orgsId, Integer.MAX_VALUE, 0);
    }

    public List<LocationEntity> findByOrganizationList(Set<String> orgsId, int from, int size) {
        Criteria criteria = getCriteria();

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        if (orgsId.size() > 0) {
            criteria.add(Restrictions.in("organization.id", orgsId));
            return criteria.list();
        }

        return null;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
