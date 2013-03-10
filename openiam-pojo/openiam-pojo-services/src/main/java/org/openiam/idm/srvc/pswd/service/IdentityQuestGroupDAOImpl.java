package org.openiam.idm.srvc.pswd.service;


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.springframework.stereotype.Repository;


/**
 * DAO implementation object for the domain model class IdentityQuestGroup.
 */
@Repository("identityQuestGroupDAO")
public class IdentityQuestGroupDAOImpl extends BaseDaoImpl<IdentityQuestGroupEntity, String> implements IdentityQuestGroupDAO {

	private static final Log log = LogFactory
			.getLog(IdentityQuestGroupDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "identityQuestGrpId";
	}


}
