package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UIThemeServiceImpl implements UIThemeService {
	
	@Autowired
	private UIThemeDAO uiThemeDAO;
	
	@Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;

	public void save(final UIThemeEntity entity) {
		if(StringUtils.isNotBlank(entity.getId())) {
			final UIThemeEntity dbObject = uiThemeDAO.findById(entity.getId());
			if(dbObject != null) {
				entity.setContentProviders(dbObject.getContentProviders());
				entity.setUriPatterns(dbObject.getUriPatterns());
				uiThemeDAO.merge(entity);
			}
		} else {
			uiThemeDAO.save(entity);
		}
	}
	
	public void delete(final String id) {
		final UIThemeEntity entity = get(id);
		if(entity != null) {
			uiThemeDAO.delete(entity);
		}
	}
	
	public UIThemeEntity get(final String id) {
		return uiThemeDAO.findById(id);
	}
	
	public List<UIThemeEntity> findBeans(final UIThemeSearchBean searchBean, final int from, final int size) {
		return uiThemeDAO.getByExample(searchBean, from, size);
	}

	@Override
	public void validateSave(UIThemeEntity entity) throws BasicDataServiceException {
		if(StringUtils.isBlank(entity.getName())) {
			throw new BasicDataServiceException(ResponseCode.NAME_MISSING);
		}
		
		if(StringUtils.isNotBlank(entity.getId())) {
			final UIThemeEntity existing = uiThemeDAO.getByName(entity.getName());
			if(existing != null && !StringUtils.equals(entity.getId(), existing.getId())) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
			}
		}
		
		entityValidator.isValid(entity);
	}

	@Override
	public void validateDelete(String id) throws BasicDataServiceException {
		
	}
}
