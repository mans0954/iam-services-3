package org.openiam.idm.srvc.meta.dto;

import java.util.LinkedList;
import java.util.List;

import org.openiam.idm.srvc.user.domain.UserAttributeEntity;

public class PageTemplateAttributeToken {

	private List<UserAttributeEntity> deleteList;
	private List<UserAttributeEntity> updateList;
	private List<UserAttributeEntity> saveList;
	public List<UserAttributeEntity> getDeleteList() {
		return deleteList;
	}
	public void setDeleteList(List<UserAttributeEntity> deleteList) {
		this.deleteList = deleteList;
	}
	public List<UserAttributeEntity> getUpdateList() {
		return updateList;
	}
	public void setUpdateList(List<UserAttributeEntity> updateList) {
		this.updateList = updateList;
	}
	public List<UserAttributeEntity> getSaveList() {
		return saveList;
	}
	public void setSaveList(List<UserAttributeEntity> saveList) {
		this.saveList = saveList;
	}
	
	
}
