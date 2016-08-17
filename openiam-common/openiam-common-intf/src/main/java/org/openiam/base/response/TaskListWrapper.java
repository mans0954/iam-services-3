package org.openiam.base.response;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskListWrapper", propOrder = {
	"candidateTasks",
	"assignedTasks"
})
public class TaskListWrapper {
	private List<TaskWrapper> candidateTasks;
	private List<TaskWrapper> assignedTasks;
	
	public List<TaskWrapper> getCandidateTasks() {
		return candidateTasks;
	}
	
	public void setCandidateTasks(List<TaskWrapper> candidateTasks) {
		this.candidateTasks = candidateTasks;
	}
	
	public List<TaskWrapper> getAssignedTasks() {
		return assignedTasks;
	}
	public void setAssignedTasks(List<TaskWrapper> assignedTasks) {
		this.assignedTasks = assignedTasks;
	}
	
	public void addAssignedTasks(final List<TaskWrapper> assignedTasks) {
		if(CollectionUtils.isNotEmpty(assignedTasks)) {
			if(this.assignedTasks == null) {
				this.assignedTasks = new LinkedList<TaskWrapper>();
			}
			this.assignedTasks.addAll(assignedTasks);
		}
    }

    public void addCandidateTasks(final List<TaskWrapper> candidateTasks) {
		if(CollectionUtils.isNotEmpty(assignedTasks)) {
			if(this.candidateTasks == null) {
				this.candidateTasks = new LinkedList<TaskWrapper>();
			}
			this.candidateTasks.addAll(candidateTasks);
		}
    }
	public int size() {
		int size = 0;
		if(candidateTasks != null) {
			size += candidateTasks.size();
		}
		
		if(assignedTasks != null) {
			size += assignedTasks.size();
		}
		return size;
	}

	@Override
	public String toString() {
		return String.format(
				"TaskListWrapper [candidateTasks=%s, assignedTasks=%s]",
				candidateTasks, assignedTasks);
	}
	
	
}
