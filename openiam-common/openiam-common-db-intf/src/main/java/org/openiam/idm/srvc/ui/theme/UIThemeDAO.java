package org.openiam.idm.srvc.ui.theme;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

public interface UIThemeDAO extends BaseDao<UIThemeEntity, String> {

	UIThemeEntity getByName(final String name);
}
