package org.openiam.connector.linux.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: kevin Date: 2/26/12 Time: 8:09 PM
 */
public class LinuxGroup {
    private String groupName;

    public LinuxGroup(String name) {
        this.groupName = name;
    }

    public String getAddGroupCommand() {
        StringBuilder cmd = new StringBuilder();
        cmd.append("groupadd ");
        cmd.append(groupName);
        cmd.append(";");
        return cmd.toString();
    }

    public String getDeleteGroupCommand() {
        StringBuilder cmd = new StringBuilder();
        cmd.append("groupdel ");
        cmd.append(groupName);
        cmd.append(";");
        return cmd.toString();
    }

    public String getModifyGroupCommand(String oldGroupName) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("groupmod -n ");
        cmd.append(groupName);
        cmd.append(" ");
        cmd.append(oldGroupName);
        cmd.append(";");
        return cmd.toString();
    }

    public String getLookupGroupCommand() {
        StringBuilder cmd = new StringBuilder();
        cmd.append("grep \"^");
        cmd.append(groupName);
        cmd.append(":\" /etc/group");
        return cmd.toString();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
