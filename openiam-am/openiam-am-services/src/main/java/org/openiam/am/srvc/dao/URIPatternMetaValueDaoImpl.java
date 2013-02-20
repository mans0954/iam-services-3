package org.openiam.am.srvc.dao;

import org.hibernate.Query;
import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class URIPatternMetaValueDaoImpl extends BaseDaoImpl<URIPatternMetaValueEntity, String> implements URIPatternMetaValueDao {

	@Override
	protected String getPKfieldName() {
		return "id";
	}

    @Override
    @Transactional
    public void deleteById(String id) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.id=:id ");
        qry.setString("id", id);
        qry.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteByMeta(String metaId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.metaEntity.id=:id ");
        qry.setString("id", metaId);
        qry.executeUpdate();
    }
}
