package org.openiam.idm.srvc.meta.dto;

import java.util.LinkedList;
import java.util.List;

import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;

public class PageTemplateAttributeToken {

	private List<? extends AbstractAttributeEntity> deleteList;
	private List<? extends AbstractAttributeEntity> updateList;
	private List<? extends AbstractAttributeEntity> saveList;
	public List<? extends AbstractAttributeEntity> getDeleteList() {
		return deleteList;
	}
	public void setDeleteList(List<? extends AbstractAttributeEntity> deleteList) {
		this.deleteList = deleteList;
	}
	public List<? extends AbstractAttributeEntity> getUpdateList() {
		return updateList;
	}
	public void setUpdateList(List<? extends AbstractAttributeEntity> updateList) {
		this.updateList = updateList;
	}
	public List<? extends AbstractAttributeEntity> getSaveList() {
		return saveList;
	}
	public void setSaveList(List<? extends AbstractAttributeEntity> saveList) {
		this.saveList = saveList;
	}
	
	
}
