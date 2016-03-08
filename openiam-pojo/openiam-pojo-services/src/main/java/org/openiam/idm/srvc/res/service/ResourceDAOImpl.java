package org.openiam.idm.srvc.res.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.Tuple;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for Resources.
 */
@Repository("resourceDAO")
public class ResourceDAOImpl extends BaseDaoImpl<ResourceEntity, String>
		implements ResourceDAO {

	private static final Log log = LogFactory.getLog(ResourceDAOImpl.class);

	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ResourceSearchBean) {
			final ResourceSearchBean sb = (ResourceSearchBean)searchBean;
			addSearchBeanCriteria(criteria, sb);
		}
		return criteria;
	}

	@Override
	@LocalizedDatabaseGet
	public ResourceEntity findByName(String name) {
		Criteria criteria = getCriteria().add(Restrictions.eq("name", name));
		return (ResourceEntity) criteria.uniqueResult();
	}
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

	private void addSearchBeanCriteria(final Criteria criteria, final ResourceSearchBean sb) {
		if(sb != null && criteria != null) {

			if(sb.getRisk() != null) {
                criteria.add(Restrictions.eq("risk", sb.getRisk()));
            }
            
            if(StringUtils.isNotBlank(sb.getReferenceId())) {
            	criteria.add(Restrictions.eq("referenceId", sb.getReferenceId()));
            }

			if (StringUtils.isNotEmpty(sb.getName())) {
				String resourceName = sb.getName();
				MatchMode matchMode = null;
				if (StringUtils.indexOf(resourceName, "*") == 0) {
					matchMode = MatchMode.END;
					resourceName = resourceName.substring(1);
				}
				if (StringUtils.isNotEmpty(resourceName) && StringUtils.indexOf(resourceName, "*") == resourceName.length() - 1) {
					resourceName = resourceName.substring(0, resourceName.length() - 1);
					matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
				}

				if (StringUtils.isNotEmpty(resourceName)) {
					if (matchMode != null) {
						criteria.add(Restrictions.ilike("name", resourceName, matchMode));
					} else {
						criteria.add(Restrictions.eq("name", resourceName));
					}
				}
			}

            if (StringUtils.isNotEmpty(sb.getURL())) {
                String url = sb.getURL();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(url, "*") == 0) {
                    matchMode = MatchMode.END;
                    url = url.substring(1);
                }
                if (StringUtils.isNotEmpty(url) && StringUtils.indexOf(url, "*") == url.length() - 1) {
                    url = url.substring(0, url.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(url)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("URL", url, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("URL", url));
                    }
                }
            }

			if (StringUtils.isNotBlank(sb.getResourceTypeId())) {
				criteria.add(Restrictions.eq("resourceType.id", sb.getResourceTypeId()));
			}
			
			if(sb.hasMultipleKeys()) {
				criteria.add(Restrictions.in(getPKfieldName(), sb.getKeys()));
			}else if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
			}

			if(Boolean.TRUE.equals(sb.getRootsOnly())) {
				criteria.add(Restrictions.isEmpty("parentResources"));
			}
			if(CollectionUtils.isNotEmpty(sb.getResourceTypeIdSet())) {
				criteria.add(Restrictions.in("resourceType.id", sb.getResourceTypeIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(sb.getExcludeResourceTypes())) {
				criteria.add(Restrictions.not(Restrictions.in("resourceType.id", sb.getExcludeResourceTypes())));
			}

			if(CollectionUtils.isNotEmpty(sb.getAttributes())) {
				criteria.createAlias("resourceProps", "prop");
				for(final Tuple<String, String> attribute : sb.getAttributes()) {
					if(StringUtils.isNotBlank(attribute.getKey()) && StringUtils.isNotBlank(attribute.getValue())) {
						criteria.add(Restrictions.and(Restrictions.eq("prop.name", attribute.getKey()), 
								Restrictions.eq("prop.value", attribute.getValue())));
					} else if(StringUtils.isNotBlank(attribute.getKey())) {
						criteria.add(Restrictions.eq("prop.name", attribute.getKey()));
					} else if(StringUtils.isNotBlank(attribute.getValue())) {
						criteria.add(Restrictions.eq("prop.value", attribute.getValue()));
					}
				}
			}

            if(StringUtils.isNotBlank(sb.getMetadataType())){
                criteria.add(Restrictions.eq("type.id", sb.getMetadataType()));
            }
            
            if(CollectionUtils.isNotEmpty(sb.getResourceIdSet())){    
                criteria.createAlias("resources", "resourceXrefs")
						.createAlias("resourceXrefs.entity", "resource").add(
						Restrictions.in("resource.id", sb.getResourceIdSet()));
            }
            
            if(CollectionUtils.isNotEmpty(sb.getRoleIdSet())){    
                criteria.createAlias("roles", "roleXrefs")
						.createAlias("roleXrefs.entity", "role").add(
						Restrictions.in("role.id", sb.getRoleIdSet()));
            }

            if(CollectionUtils.isNotEmpty(sb.getGroupIdSet())){    
                criteria.createAlias("groups", "groupXrefs")
						.createAlias("groupXrefs.entity", "group").add(
						Restrictions.in("group.id", sb.getGroupIdSet()));
            }
            
            if(CollectionUtils.isNotEmpty(sb.getChildIdSet())) {
            	criteria.createAlias("childResources", "childXrefs")
						.createAlias("childXrefs.memberEntity", "child").add(
						Restrictions.in("child.id", sb.getChildIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(sb.getParentIdSet())) {
				criteria.createAlias("parentResources", "parentXrefs")
						.createAlias("parentXrefs.entity", "parent").add(
						Restrictions.in("parent.id", sb.getParentIdSet()));
			}
			
			if(CollectionUtils.isNotEmpty(sb.getOrganizationIdSet())){    
                criteria.createAlias("organizations", "organizationXrefs")
						.createAlias("organizationXrefs.entity", "organization").add(
						Restrictions.in("organization.id", sb.getOrganizationIdSet()));
            }

            if(CollectionUtils.isNotEmpty(sb.getUserIdSet())){    
                criteria.createAlias("users", "userXrefs")
						.createAlias("userXrefs.memberEntity", "user").add(
						Restrictions.in("user.id", sb.getUserIdSet()));
            }

			if(StringUtils.isNotBlank(sb.getOwnerId())) {
				criteria.createAlias("users", "aru");
				criteria.createAlias("aru.rights", "aruRights");
				criteria.add(Restrictions.eq("aru.memberEntity.id", sb.getOwnerId()));
				criteria.add(Restrictions.eq("aruRights.id", adminRightId));
			}
		}
	}

}
