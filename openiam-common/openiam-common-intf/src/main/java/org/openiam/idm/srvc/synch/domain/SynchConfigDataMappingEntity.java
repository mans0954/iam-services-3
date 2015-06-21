package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.KeyEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchConfigDataMapping;
import javax.persistence.*;

@Entity
@Table(name = "SYNCH_CONFIG_DATA_MAPPING")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchConfigDataMapping.class)
@AttributeOverrides(value= {
        @AttributeOverride(name = "id", column = @Column(name = "MAPPING_ID")),
})
public class SynchConfigDataMappingEntity extends KeyEntity {

    @Column(name="SYNCH_CONFIG_ID", length=32)
    private String synchConfigId;
    @Column(name="IDM_FIELD_NAME",length=40)
    private String idmFieldName;
    @Column(name="SRC_FIELD_NAME",length=40)
    private String srcFieldName;

    public SynchConfigDataMappingEntity() {
    }

    public SynchConfigDataMappingEntity(String mappingId) {
        this.id = mappingId;
    }

    public SynchConfigDataMappingEntity(String mappingId, String synchConfigId,
                                  String idmFieldName, String srcFieldName) {
        this.id = mappingId;
        this.synchConfigId = synchConfigId;
        this.idmFieldName = idmFieldName;
        this.srcFieldName = srcFieldName;
    }

    public String getSynchConfigId() {
        return this.synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

    public String getIdmFieldName() {
        return this.idmFieldName;
    }

    public void setIdmFieldName(String idmFieldName) {
        this.idmFieldName = idmFieldName;
    }

    public String getSrcFieldName() {
        return this.srcFieldName;
    }

    public void setSrcFieldName(String srcFieldName) {
        this.srcFieldName = srcFieldName;
    }

    @Override
    public String toString() {
        return "SynchConfigDataMapping{" +
                "mappingId='" + id + '\'' +
                ", synchConfigId='" + synchConfigId + '\'' +
                ", idmFieldName='" + idmFieldName + '\'' +
                ", srcFieldName='" + srcFieldName + '\'' +
                '}';
    }
}
