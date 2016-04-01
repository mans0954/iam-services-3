package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.batch.dto.BatchTask;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchTaskSearchBean", propOrder = {
	"enabled"
})
public class BatchTaskSearchBean extends AbstractKeyNameSearchBean<BatchTask, String> implements SearchBean {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
   
    private Boolean enabled;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(name != null ? name : "")
				.append(getKey() != null ? getKey() : "")
				.toString();	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
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
		BatchTaskSearchBean other = (BatchTaskSearchBean) obj;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		return true;
	}
    
    
}
