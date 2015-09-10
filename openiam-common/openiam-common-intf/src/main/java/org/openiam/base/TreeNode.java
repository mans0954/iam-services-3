package org.openiam.base;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 12/31/13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TreeNode", propOrder = {
//        "parent",
        "data",
        "children",
        "icon",
        "iconType",
        "iconDescription",
        "isException",
        "isDeletable",
        "isTerminate"
})
public class TreeNode<Bean extends KeyDTO> implements Serializable {
    @JsonIgnore
    @XmlTransient
    private TreeNode<Bean> parent;
    private List<TreeNode<Bean>> children = new ArrayList<TreeNode<Bean>>();
    private Bean data;
    private String icon;
    private String iconType;
    private String iconDescription;
    private boolean isException=false;
    private boolean isDeletable=false;
    private boolean isTerminate=false;

    public TreeNode(){
    }

    public TreeNode(Bean data){
        this(data, null);
    }
    public TreeNode(Bean data, String icon){
        this(data, icon, null);
    }
    public TreeNode(Bean data, String icon, String iconType){
        this.data=data;
        this.icon=icon;
        this.iconType=iconType;
    }

    public void add(TreeNode child){
        this.add(child, -1);
    }
    public void add(TreeNode child, int index) {
        if(child==null){
            throw new IllegalArgumentException("Child is null. Cannot add nothing to this element");
        }
        if( index < 0 || index > children.size()) {
            children.add(child);
        } else {
            children.add(index, child);
        }
        child.parent = this;
    }
    public void add(List<TreeNode<Bean>> children){
        if(CollectionUtils.isNotEmpty(children)){
            for(TreeNode<Bean> child: children){
                this.add(child);
            }
        }
    }


    public TreeNode remove(int index) {
        if ( index < 0 || index >= children.size() )
            throw new IllegalArgumentException("Cannot remove element with index " + index + " when there are " + children.size() + " elements.");

        // Get a handle to the node being removed.
        TreeNode node = children.get(index);
        node.parent = null;
        children.remove(index);
        return node;
    }
    public TreeNode remove(TreeNode node) {
        if(node==null)
            throw new IllegalArgumentException("Node is null. Cannot remove nothing.");

        Iterator<TreeNode<Bean>> iter = children.iterator();
        while(iter.hasNext()){
            TreeNode child = iter.next();
            if(node.data.getId().equals(child.data.getId())){
                iter.remove();
                return child;
            }
        }
//        for(int i=children.size()-1; i>=0; i--){
//            if(node.data.getId().equals(children.get(i).data.getId())){
//                return remove(i);
//            }
//        }
        return node;
    }

    public void bubleNode(){
        if(isRoot())
            return;
        TreeNode<Bean> newParent = (this.parent.isRoot())? null : this.parent.parent;
        this.parent.remove(this);
        newParent.add(this);
    }

    public void bubleChildrenNodes(){
        if(CollectionUtils.isNotEmpty(children)){
            Iterator<TreeNode<Bean>> iter = children.iterator();
            TreeNode<Bean> newParent = (isRoot())? null : this.parent;

            while(iter.hasNext()){
                TreeNode<Bean> child = iter.next();
                newParent.add(child);
                iter.remove();
            }
        }
    }

    public int getIndex(){
        if(!this.isRoot()){
            for(int i=0; i<parent.children.size(); i++){
                TreeNode<Bean> node = parent.children.get(i);
                if(node.data!=null && this.data!=null){
                    if(node.data.getId().equals(this.data.getId())){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public int getDepth(){
        return recurseDepth(parent, 0);
    }

    public boolean isRoot () {
        return parent == null;
    }

    public boolean isLeaf () {
        return CollectionUtils.isEmpty(children);
    }


    private int recurseDepth(TreeNode node, int depth){
        if ( node == null ){
            return depth;
        } else {
            return recurseDepth(node.parent, depth + 1);
        }
    }

    public TreeNode<Bean> getParent() {
        return parent;
    }

    public List<TreeNode<Bean>> getChildren() {
        return children;
    }

    public Bean getData() {
        return data;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getIconDescription() {
        return iconDescription;
    }

    public void setIconDescription(String iconDescription) {
        this.iconDescription = iconDescription;
    }

    @Override
    public String toString() {
        int depth = this.getDepth();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<depth;i++){
            sb.append("\t");
        }
        sb.append("Node{").append(data).append("}\n");
        if(CollectionUtils.isNotEmpty(children)){
            for(TreeNode<Bean> child: children){
                sb.append(child);
            }
        }
        return sb.toString();
    }

    public boolean getIsException() {
        return isException;
    }

    public void setIsException(boolean isException) {
        this.isException = isException;
    }

    public boolean getIsDeletable() {
        return isDeletable;
    }

    public void setIsDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }

    public boolean getIsTerminate() {
        return isTerminate;
    }

    public void setIsTerminate(boolean isTerminate) {
        this.isTerminate = isTerminate;
    }
}
