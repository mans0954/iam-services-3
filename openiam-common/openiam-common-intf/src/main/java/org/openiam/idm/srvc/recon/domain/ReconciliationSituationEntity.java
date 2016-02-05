package org.openiam.idm.srvc.recon.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

/**
 * ReconiliationSituation generated by hbm2java
 */
@Entity
@Table(name = "RECONCILIATION_SITUATION")
@DozerDTOCorrespondence(ReconciliationSituation.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverride(name = "id", column = @Column(name = "RECON_SITUATION_ID"))
public class ReconciliationSituationEntity extends KeyEntity {

    private static final long serialVersionUID = -8870989951105414407L;
    @Column(name = "RECON_CONFIG_ID", length = 32)
    private String reconConfigId;
    @Column(name = "SITUATION", length = 30)
    private String situation;
    @Column(name = "SITUATION_RESP", length = 40)
    private String situationResp;
    @Column(name = "SCRIPT", length = 80)
    private String script;

    @Column(name = "CUSTOM_COMMAND_SCRIPT", length = 80)
    private String customCommandScript;

    public ReconciliationSituationEntity() {
    }

    public String getCustomCommandScript() {
        return customCommandScript;
    }

    public void setCustomCommandScript(String customCommandScript) {
        this.customCommandScript = customCommandScript;
    }

    public String getReconConfigId() {
        return this.reconConfigId;
    }

    public void setReconConfigId(String reconConfigId) {
        this.reconConfigId = reconConfigId;
    }

    public String getSituation() {
        return this.situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getSituationResp() {
        return this.situationResp;
    }

    public void setSituationResp(String situationResp) {
        this.situationResp = situationResp;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((customCommandScript == null) ? 0 : customCommandScript
						.hashCode());
		result = prime * result
				+ ((reconConfigId == null) ? 0 : reconConfigId.hashCode());
		result = prime * result + ((script == null) ? 0 : script.hashCode());
		result = prime * result
				+ ((situation == null) ? 0 : situation.hashCode());
		result = prime * result
				+ ((situationResp == null) ? 0 : situationResp.hashCode());
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
		ReconciliationSituationEntity other = (ReconciliationSituationEntity) obj;
		if (customCommandScript == null) {
			if (other.customCommandScript != null)
				return false;
		} else if (!customCommandScript.equals(other.customCommandScript))
			return false;
		if (reconConfigId == null) {
			if (other.reconConfigId != null)
				return false;
		} else if (!reconConfigId.equals(other.reconConfigId))
			return false;
		if (script == null) {
			if (other.script != null)
				return false;
		} else if (!script.equals(other.script))
			return false;
		if (situation == null) {
			if (other.situation != null)
				return false;
		} else if (!situation.equals(other.situation))
			return false;
		if (situationResp == null) {
			if (other.situationResp != null)
				return false;
		} else if (!situationResp.equals(other.situationResp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReconciliationSituationEntity [reconConfigId=" + reconConfigId
				+ ", situation=" + situation + ", situationResp="
				+ situationResp + ", script=" + script
				+ ", customCommandScript=" + customCommandScript + ", id=" + id
				+ "]";
	}
    
    
}
