package org.openiam.idm.srvc.ui.theme;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.UIThemeSearchBean;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UIThemeServiceImpl implements UIThemeService {
	
	@Autowired
	private UIThemeDAO uiThemeDAO;
	
	@Autowired
	private ContentProviderDao contentProviderDAO;
	
	@Autowired
	private URIPatternDao uriPatternDAO;
	
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
			if(CollectionUtils.isNotEmpty(entity.getContentProviders())) {
				for(final ContentProviderEntity cp : entity.getContentProviders()) {
					cp.setUiTheme(null);
					contentProviderDAO.update(cp);
				}
			}
			if(CollectionUtils.isNotEmpty(entity.getUriPatterns())) {
				for(final URIPatternEntity pattern : entity.getUriPatterns()) {
					pattern.setUiTheme(null);
					uriPatternDAO.update(pattern);
				}
			}
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
		
		if(StringUtils.isBlank(entity.getUrl())) {
			throw new BasicDataServiceException(ResponseCode.URL_REQUIRED);
		}
		
		final UIThemeEntity existing = uiThemeDAO.getByName(entity.getName());
		if(existing != null && !StringUtils.equals(entity.getId(), existing.getId())) {
			throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
		}
		
		entityValidator.isValid(entity);
	}

	@Override
	public void validateDelete(String id) throws BasicDataServiceException {
		
	}
}
