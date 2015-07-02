package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.ResourceToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToResourceMembershipHibernateDAO")
public class OrgToResourceMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<ResourceToOrgMembershipXrefEntity>
	   implements OrgToResourceMembershipHibernateDAO {

	@Override
	protected Class<ResourceToOrgMembershipXrefEntity> getEntityClass() {
		return ResourceToOrgMembershipXrefEntity.class;
	}

}
