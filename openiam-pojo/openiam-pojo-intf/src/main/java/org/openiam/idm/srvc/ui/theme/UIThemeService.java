package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

public interface UIThemeService {

	void save(final UIThemeEntity entity);
	void delete(final String id);
	UIThemeEntity get(final String id);
	List<UIThemeEntity> findBeans(final UIThemeSearchBean searchBean, final int from, final int size);
	void validateSave(final UIThemeEntity entity) throws BasicDataServiceException;
	void validateDelete(final String id) throws BasicDataServiceException;
}
