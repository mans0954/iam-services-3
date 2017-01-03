package org.openiam.elasticsearch.model;

import org.openiam.elasticsearch.annotation.EntityRepresentation;
import org.openiam.elasticsearch.constants.ESIndexName;
import org.openiam.elasticsearch.constants.ESIndexType;
import org.openiam.elasticsearch.converter.LoginDocumentToEntityConverter;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@EntityRepresentation(value=LoginEntity.class, converter=LoginDocumentToEntityConverter.class)
@Document(indexName = ESIndexName.LOGIN, type= ESIndexType.LOGIN)
public class LoginDoc extends AbstractKeyDoc {
	
	public LoginDoc() {}

	@Field(type = FieldType.String, index = FieldIndex.analyzed, store= true)
	private String login;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= false)
	private String managedSysId;
	
	@Field(type = FieldType.String, index = FieldIndex.not_analyzed, store= true)
	protected String userId;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getManagedSysId() {
		return managedSysId;
	}

	public void setManagedSysId(String managedSysId) {
		this.managedSysId = managedSysId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((managedSysId == null) ? 0 : managedSysId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginDoc other = (LoginDoc) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (managedSysId == null) {
			if (other.managedSysId != null)
				return false;
		} else if (!managedSysId.equals(other.managedSysId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
}
