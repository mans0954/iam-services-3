package org.openiam.idm.srvc.recon.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

/**
 * ReconiliationSituation generated by hbm2java
 */
@DozerDTOCorrespondence(ReconciliationSituation.class)
public class ReconciliationSituationEntity implements java.io.Serializable {

    private static final long serialVersionUID = -8870989951105414407L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "RECON_SITUATION_ID", length = 32)
    private String reconSituationId;
    @Column(name = "RECON_CONFIG_ID", length = 32)
    private String reconConfigId;
    @Column(name = "SITUATION", length = 30)
    private String situation;
    @Column(name = "SITUATION_RESP", length = 40)
    private String situationResp;
    @Column(name = "SCRIPT", length = 80)
    private String script;

    public ReconciliationSituationEntity() {
    }

    public String getReconSituationId() {
        return this.reconSituationId;
    }

    public void setReconSituationId(String reconSituationId) {
        this.reconSituationId = reconSituationId;
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
}
