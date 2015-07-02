package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("resourceToResourceMembershipHibernateDAO")
public class ResourceToResourceMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<ResourceToResourceMembershipXrefEntity> 
	   implements ResourceToResourceMembershipHibernateDAO {

	@Override
	protected Class<ResourceToResourceMembershipXrefEntity> getEntityClass() {
		return ResourceToResourceMembershipXrefEntity.class;
	}

}
