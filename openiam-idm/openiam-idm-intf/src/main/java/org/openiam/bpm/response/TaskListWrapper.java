package org.openiam.bpm.response;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.openiam.util.DozerMappingType;

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
	
	public void addAssignedTasks(final List<Task> assignedTasks, final RuntimeService runtimeService) {
		if(CollectionUtils.isNotEmpty(assignedTasks)) {
			for(final Task task : assignedTasks) {
				if(this.assignedTasks == null) {
					this.assignedTasks = new LinkedList<TaskWrapper>();
				}
				this.assignedTasks.add(new TaskWrapper(task, runtimeService));
			}
		}
	}
	
	public void addCandidateTasks(final List<Task> candidateTasks, final RuntimeService runtimeService) {
		if(CollectionUtils.isNotEmpty(candidateTasks)) {
			for(final Task task : candidateTasks) {
				if(this.candidateTasks == null) {
					this.candidateTasks = new LinkedList<TaskWrapper>();
				}
				this.candidateTasks.add(new TaskWrapper(task, runtimeService));
			}
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
