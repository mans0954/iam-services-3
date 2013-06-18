package org.openiam.provision.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericProvisionObject", propOrder = {
        "object","provisionObjectType"
})
public abstract class GenericProvisionObject<T> {
    protected ProvisionObjectType provisionObjectType;
    private T object;

    public  GenericProvisionObject(T object){
        setObject(object);
    }

    public ProvisionObjectType getProvisionObjectType() {
        return provisionObjectType;
    }

    protected T getObject(){
        return this.object;
    }
    protected void setObject(T object){
      this.object=object;
    }
}
