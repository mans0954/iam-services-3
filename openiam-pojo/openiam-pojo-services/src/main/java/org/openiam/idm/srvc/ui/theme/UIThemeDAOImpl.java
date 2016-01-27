package org.openiam.idm.srvc.ui.theme;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UIThemeDAOImpl extends BaseDaoImpl<UIThemeEntity, String> implements UIThemeDAO {

	@Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if(searchBean != null && (searchBean instanceof UIThemeSearchBean)) {
        	final UIThemeSearchBean uiThemeSearchBean = (UIThemeSearchBean)searchBean;
        }
        return criteria;
	}
	
	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public UIThemeEntity getByName(String name) {
		return (UIThemeEntity)getCriteria().add(Restrictions.eq("name", name)).uniqueResult();
	}

}
