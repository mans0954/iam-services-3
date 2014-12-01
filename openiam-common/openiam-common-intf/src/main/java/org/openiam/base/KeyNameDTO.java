package org.openiam.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyNameDTO", propOrder = {
	"name_"
})
public abstract class KeyNameDTO extends KeyDTO implements Serializable {


    private static final long serialVersionUID = -3458341104954863644L;

    protected String name_;

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		this.name_ = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyNameDTO other = (KeyNameDTO) obj;
		if (this.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("KeyNameDTO [name=%s, id=%s]", this.getName(), id);
	}
	
	
}
