package org.openiam.idm.srvc.grp.dto;

import java.util.List;

import org.openiam.base.BaseTemplateRequestModel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by alexander on 28/12/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupRequestModel",
	propOrder = {
		"group",
		"groupParents"
	})
public class GroupRequestModel extends BaseTemplateRequestModel<Group> {
    private Group group;
    private List<Group> groupParents;

    public List<Group> getGroupParents() {
		return groupParents;
	}
	public void setGroupParents(List<Group> groupParents) {
		this.groupParents = groupParents;
	}
	public Group getTargetObject(){
        return group;
    }
    public void setTargetObject(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupRequestModel that = (GroupRequestModel) o;

        return group != null ? group.equals(that.group) : that.group == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GroupRequestModel{" +
                "group=" + group +
                "} " + super.toString();
    }
}
