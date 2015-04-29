package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TreeObjectId", propOrder = {
        "value",
        "children"
})
public class TreeObjectId implements Serializable {

    private String value;
    private List<TreeObjectId> children = new LinkedList<TreeObjectId>();

    public TreeObjectId() {
    }

    public TreeObjectId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<TreeObjectId> getChildren() {
        return children;
    }

    public void setChildren(List<TreeObjectId> children) {
        this.children = children;
    }

    public void addChild(TreeObjectId child) {
        children.add(child);
    }
    public void removeChild(TreeObjectId child) {
        children.remove(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeObjectId that = (TreeObjectId) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TreeObjectId{" +
                "value='" + value + '\'' +
                "children.size='" + children.size() + '\'' +
                '}';
    }
}
