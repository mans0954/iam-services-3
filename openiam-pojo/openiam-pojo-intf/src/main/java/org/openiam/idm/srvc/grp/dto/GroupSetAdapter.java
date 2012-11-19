package org.openiam.idm.srvc.grp.dto;


import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.openiam.idm.srvc.grp.domain.GroupEntity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class GroupSetAdapter extends XmlAdapter<GroupSet, Set<GroupEntity>> {

    @Override
    public GroupSet marshal(Set<GroupEntity> b) throws Exception {
        GroupSet v = new GroupSet();
        if (b == null) return v;

        for (Iterator<GroupEntity> iterator = b.iterator(); iterator.hasNext(); ) {
        	GroupEntity group = (GroupEntity) iterator.next();
            GroupSet.GroupObj obj = new GroupSet.GroupObj();
            obj.setGroup(group);
            v.getGroupObj().add(obj);
        }
        return v;
    }

    @Override
    public Set<GroupEntity> unmarshal(GroupSet v) throws Exception {
        Set<GroupEntity> b = new HashSet<GroupEntity>();
        if (v == null) return b;

        List<GroupSet.GroupObj> l = v.getGroupObj();
        for (Iterator<GroupSet.GroupObj> iterator = l.iterator(); iterator.hasNext(); ) {
            GroupSet.GroupObj obj = (GroupSet.GroupObj) iterator.next();
            b.add(obj.getGroup());
        }
        return b;
    }
}

