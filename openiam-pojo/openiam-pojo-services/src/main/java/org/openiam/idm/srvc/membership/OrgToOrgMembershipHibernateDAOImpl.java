package org.openiam.idm.srvc.membership;

import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.springframework.stereotype.Repository;

@Repository("orgToOrgMembershipHibernateDAO")
public class OrgToOrgMembershipHibernateDAOImpl 
	   extends AbstractMembershipHibernateDAO<OrgToOrgMembershipXrefEntity>
	   implements OrgToOrgMembershipHibernateDAO {

	@Override
	protected Class<OrgToOrgMembershipXrefEntity> getEntityClass() {
		return OrgToOrgMembershipXrefEntity.class;
	}

}
