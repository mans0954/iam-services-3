package org.openiam.am.srvc.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractAuthLevelGroupingXref", propOrder = {
        "order"
})
public abstract class AbstractAuthLevelGroupingXref implements Serializable {

	private int order;
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAuthLevelGroupingXref other = (AbstractAuthLevelGroupingXref) obj;
		return order == other.order;
	}
	@Override
	public String toString() {
		return String.format("AbstractAuthLevelGroupingXref [order=%s]", order);
	}
	
	
}
