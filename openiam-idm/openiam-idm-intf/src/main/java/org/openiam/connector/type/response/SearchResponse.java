package org.openiam.connector.type.response;

import org.openiam.connector.type.ObjectValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResponse", propOrder = {
    "objectList"
})
public class SearchResponse extends ResponseType
{
    List<ObjectValue> objectList = new LinkedList<ObjectValue>();

    public List<ObjectValue> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<ObjectValue> objectList) {
        this.objectList = objectList;
    }
}
