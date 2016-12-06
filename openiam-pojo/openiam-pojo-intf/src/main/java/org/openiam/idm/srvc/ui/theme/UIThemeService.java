package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;

public interface UIThemeService {

	String save(final UITheme dto)throws BasicDataServiceException;
	void delete(final String id)throws BasicDataServiceException;
	UITheme get(final String id);
	List<UITheme> findBeans(final UIThemeSearchBean searchBean, final int from, final int size);
//	void validateSave(final UIThemeEntity entity) throws BasicDataServiceException;
//	void validateDelete(final String id) throws BasicDataServiceException;
}
