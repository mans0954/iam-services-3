package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;

public interface UIThemeService {

	public void save(final UIThemeEntity entity);
	public void delete(final String id);
	public UIThemeEntity get(final String id);
	public List<UIThemeEntity> findBeans(final UIThemeSearchBean searchBean, final int from, final int size);
	public void validateSave(final UIThemeEntity entity) throws BasicDataServiceException;
	public void validateDelete(final String id) throws BasicDataServiceException;
}
