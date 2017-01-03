package org.openiam.elasticsearch.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.elasticsearch.model.LoginDoc;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.stereotype.Component;

@Component("loginDocumentToEntityConverter")
public class LoginDocumentToEntityConverter extends AbstractKeyDocumentToEntityConverter<LoginDoc, LoginEntity> {

	@Override
	protected LoginDoc newDocument() {
		return new LoginDoc();
	}

	@Override
	protected LoginEntity newEntity() {
		return new LoginEntity();
	}

	@Override
	public Class<LoginDoc> getDocumentClass() {
		return LoginDoc.class;
	}

	@Override
	public Class<LoginEntity> getEntityClass() {
		return LoginEntity.class;
	}

	@Override
	public LoginDoc convertToDocument(LoginEntity entity) {
		final LoginDoc doc = super.convertToDocument(entity);
		doc.setLogin(StringUtils.trimToNull(entity.getLogin()));
		doc.setManagedSysId(StringUtils.trimToNull(entity.getManagedSysId()));
		doc.setUserId(StringUtils.trimToNull(entity.getUserId()));
		return doc;
	}

	@Override
	public LoginEntity convertToEntity(LoginDoc doc) {
		final LoginEntity entity = super.convertToEntity(doc);
		entity.setUserId(StringUtils.trimToNull(doc.getUserId()));
		entity.setManagedSysId(StringUtils.trimToNull(doc.getManagedSysId()));
		entity.setLogin(StringUtils.trimToNull(doc.getLogin()));
		return entity;
	}

	
}
