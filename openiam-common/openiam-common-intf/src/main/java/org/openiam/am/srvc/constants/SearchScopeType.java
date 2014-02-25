package org.openiam.am.srvc.constants;

import javax.naming.directory.SearchControls;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "NotificationType")
@XmlEnum
public enum SearchScopeType {

    /**
     *  method defines the search method only within the named object that is defined with ContextName.
     *  The object scope compares the named object for some particular attribute or value.
     */
    @XmlEnumValue("OBJECT_SCOPE")
    OBJECT_SCOPE(SearchControls.OBJECT_SCOPE, "Object"),

    /**
     *  defines the search method for entries that are one level below the named object
     */
    @XmlEnumValue("ONELEVEL_SCOPE")
    ONELEVEL_SCOPE(SearchControls.ONELEVEL_SCOPE, "Onelevel"),

    /**
     * method defines the search method for all entries starting from
     * the named object and all descendants below the named object.
     */
    @XmlEnumValue("SUBTREE_SCOPE")
    SUBTREE_SCOPE(SearchControls.SUBTREE_SCOPE, "Subtree");

    private int value;
    private String label;

    SearchScopeType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }

}
