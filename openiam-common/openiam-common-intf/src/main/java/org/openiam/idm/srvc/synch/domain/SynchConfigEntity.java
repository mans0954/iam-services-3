package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchConfig;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "SYNCH_CONFIG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchConfig.class)
public class SynchConfigEntity implements Serializable {
    private static final long serialVersionUID = -748384789293890253L;
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="SYNCH_CONFIG_ID", length=32, nullable = false)
    private String synchConfigId;
    @Column(name="NAME",length=60)
    private String name;
    @Column(name="STATUS",length=20)
    private String status;
    @Column(name="SYNCH_SRC",length=20)
    private String synchAdapter;
    @Column(name="FILE_NAME",length=80)
    private String fileName;
    @Column(name="MANAGED_SYS_ID",length=32)
    private String managedSysId;
    @Column(name="LOAD_MATCH_ONLY")
    private Integer loadMatchOnly;
    @Column(name="UPDATE_ATTRIBUTE")
    private Integer updateAttribute;
    @Column(name="SYNCH_FREQUENCY",length=20)
    private String synchFrequency;
    @Column(name="SYNCH_TYPE",length=20)
    private String synchType;
    //private String deleteRule;
    @Column(name="PROCESS_RULE",length=80)
    private String processRule;
    @Column(name="VALIDATION_RULE",length=80)
    private String validationRule;
    @Column(name="USE_POLICY_MAP")
    @Type(type = "yes_no")
    private boolean usePolicyMap = false;
    @Column(name="TRANSFORMATION_RULE",length=80)
    private String transformationRule;
    @Column(name="MATCH_FIELD_NAME",length=40)
    private String matchFieldName;
    @Column(name="MATCH_MANAGED_SYS_ID",length=32)
    private String matchManagedSysId;
    @Column(name="MATCH_SRC_FIELD_NAME",length=40)
    private String matchSrcFieldName;
    @Column(name="SRC_LOGIN_ID",length=100)
    private String srcLoginId;
    @Column(name="SRC_PASSWORD",length=100)
    private String srcPassword;
    @Column(name="SRC_HOST",length=100)
    private String srcHost;
    @Column(name="DRIVER",length=50)
    private String driver;
    @Column(name="CONNECTION_URL",length=100)
    private String connectionUrl;
    @Column(name="QUERY",length=100)
    private String query;
    @Column(name="QUERY_TIME_FIELD",length=50)
    private String queryTimeField;
    @Column(name="LAST_EXEC_TIME",length=19)
    @Temporal(TemporalType.DATE)
    private java.util.Date lastExecTime;
    @Column(name="LAST_REC_PROCESSED",length=32)
    private String lastRecProcessed;
    @Column(name="CUSTOM_MATCH_RULE",length=10)
    private String customMatchRule;
    @Column(name="CUSTOM_ADAPTER_SCRIPT",length=80)
    private String customAdatperScript;
    @Column(name="CUSTOM_MATCH_ATTR",length=40)
    private String customMatchAttr;
    @Column(name="BASE_DN",length=40)
    private String baseDn;
    @Column(name="WS_URL",length=100)
    private String wsUrl;
    @Column(name="WS_SCRIPT",length=100)
    private String wsScript;

    public SynchConfigEntity() {
    }

    public SynchConfigEntity(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

    public String getSynchConfigId() {
        return this.synchConfigId;
    }

    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getManagedSysId() {
        return this.managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public Integer getLoadMatchOnly() {
        return this.loadMatchOnly;
    }

    public void setLoadMatchOnly(Integer loadMatchOnly) {
        this.loadMatchOnly = loadMatchOnly;
    }

    public Integer getUpdateAttribute() {
        return this.updateAttribute;
    }

    public void setUpdateAttribute(Integer updateAttribute) {
        this.updateAttribute = updateAttribute;
    }

    public String getSynchFrequency() {
        return this.synchFrequency;
    }

    public void setSynchFrequency(String synchFrequency) {
        this.synchFrequency = synchFrequency;
    }

    public String getProcessRule() {
        return this.processRule;
    }

    public void setProcessRule(String processRule) {
        this.processRule = processRule;
    }

    public String getTransformationRule() {
        return this.transformationRule;
    }

    public void setTransformationRule(String transformationRule) {
        this.transformationRule = transformationRule;
    }

    public boolean getUsePolicyMap() {
        return usePolicyMap;
    }

    public void setUsePolicyMap(boolean usePolicyMap) {
        this.usePolicyMap = usePolicyMap;
    }

    public String getMatchFieldName() {
        return this.matchFieldName;
    }

    public void setMatchFieldName(String matchFieldName) {
        this.matchFieldName = matchFieldName;
    }

    public String getMatchSrcFieldName() {
        return this.matchSrcFieldName;
    }

    public void setMatchSrcFieldName(String matchSrcFieldName) {
        this.matchSrcFieldName = matchSrcFieldName;
    }

    public String getMatchManagedSysId() {
        return matchManagedSysId;
    }

    public void setMatchManagedSysId(String matchManagedSysId) {
        this.matchManagedSysId = matchManagedSysId;
    }

    public String getSrcLoginId() {
        return srcLoginId;
    }

    public void setSrcLoginId(String srcLoginId) {
        this.srcLoginId = srcLoginId;
    }

    public String getSrcPassword() {
        return srcPassword;
    }

    public void setSrcPassword(String srcPassword) {
        this.srcPassword = srcPassword;
    }

    public String getSrcHost() {
        return srcHost;
    }

    public void setSrcHost(String srcHost) {
        this.srcHost = srcHost;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryTimeField() {
        return queryTimeField;
    }

    public void setQueryTimeField(String queryTimeField) {
        this.queryTimeField = queryTimeField;
    }

    public String getCustomMatchRule() {
        return customMatchRule;
    }

    public void setCustomMatchRule(String customMatchRule) {
        this.customMatchRule = customMatchRule;
    }

    public String getCustomAdatperScript() {
        return customAdatperScript;
    }

    public void setCustomAdatperScript(String customAdatperScript) {
        this.customAdatperScript = customAdatperScript;
    }

    public String getSynchAdapter() {
        return synchAdapter;
    }

    public void setSynchAdapter(String synchAdapter) {
        this.synchAdapter = synchAdapter;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public String getSynchType() {
        return synchType;
    }

    public void setSynchType(String synchType) {
        this.synchType = synchType;
    }

    public String getCustomMatchAttr() {
        return customMatchAttr;
    }

    public void setCustomMatchAttr(String customMatchAttr) {
        this.customMatchAttr = customMatchAttr;
    }

    public java.util.Date getLastExecTime() {
        return lastExecTime;
    }

    public void setLastExecTime(java.util.Date lastExecTime) {
        this.lastExecTime = lastExecTime;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getLastRecProcessed() {
        return lastRecProcessed;
    }

    public void setLastRecProcessed(String lastRecProcessed) {
        this.lastRecProcessed = lastRecProcessed;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    public String getWsScript() {
        return wsScript;
    }

    public void setWsScript(String wsScript) {
        this.wsScript = wsScript;
    }

    @Override
    public String toString() {
        return "SynchConfig{" +
                "synchConfigId='" + synchConfigId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", synchAdapter='" + synchAdapter + '\'' +
                ", fileName='" + fileName + '\'' +
                ", managedSysId='" + managedSysId + '\'' +
                ", loadMatchOnly=" + loadMatchOnly +
                ", updateAttribute=" + updateAttribute +
                ", synchFrequency='" + synchFrequency + '\'' +
                ", synchType='" + synchType + '\'' +
                ", processRule='" + processRule + '\'' +
                ", validationRule='" + validationRule + '\'' +
                ", transformationRule='" + transformationRule + '\'' +
                ", matchFieldName='" + matchFieldName + '\'' +
                ", matchManagedSysId='" + matchManagedSysId + '\'' +
                ", matchSrcFieldName='" + matchSrcFieldName + '\'' +
                ", srcLoginId='" + srcLoginId + '\'' +
                ", srcPassword='" + srcPassword + '\'' +
                ", srcHost='" + srcHost + '\'' +
                ", driver='" + driver + '\'' +
                ", connectionUrl='" + connectionUrl + '\'' +
                ", query='" + query + '\'' +
                ", queryTimeField='" + queryTimeField + '\'' +
                ", lastExecTime=" + lastExecTime +
                ", customMatchRule='" + customMatchRule + '\'' +
                ", customAdatperScript='" + customAdatperScript + '\'' +
                ", customMatchAttr='" + customMatchAttr + '\'' +
                ", baseDn='" + baseDn + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynchConfigEntity that = (SynchConfigEntity) o;

        if (baseDn != null ? !baseDn.equals(that.baseDn) : that.baseDn != null) return false;
        if (connectionUrl != null ? !connectionUrl.equals(that.connectionUrl) : that.connectionUrl != null)
            return false;
        if (customAdatperScript != null ? !customAdatperScript.equals(that.customAdatperScript) : that.customAdatperScript != null)
            return false;
        if (customMatchAttr != null ? !customMatchAttr.equals(that.customMatchAttr) : that.customMatchAttr != null)
            return false;
        if (customMatchRule != null ? !customMatchRule.equals(that.customMatchRule) : that.customMatchRule != null)
            return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        if (lastExecTime != null ? !lastExecTime.equals(that.lastExecTime) : that.lastExecTime != null) return false;
        if (lastRecProcessed != null ? !lastRecProcessed.equals(that.lastRecProcessed) : that.lastRecProcessed != null)
            return false;
        if (loadMatchOnly != null ? !loadMatchOnly.equals(that.loadMatchOnly) : that.loadMatchOnly != null)
            return false;
        if (managedSysId != null ? !managedSysId.equals(that.managedSysId) : that.managedSysId != null) return false;
        if (matchFieldName != null ? !matchFieldName.equals(that.matchFieldName) : that.matchFieldName != null)
            return false;
        if (matchManagedSysId != null ? !matchManagedSysId.equals(that.matchManagedSysId) : that.matchManagedSysId != null)
            return false;
        if (matchSrcFieldName != null ? !matchSrcFieldName.equals(that.matchSrcFieldName) : that.matchSrcFieldName != null)
            return false;
        if (!name.equals(that.name)) return false;
        if (processRule != null ? !processRule.equals(that.processRule) : that.processRule != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (queryTimeField != null ? !queryTimeField.equals(that.queryTimeField) : that.queryTimeField != null)
            return false;
        if (srcHost != null ? !srcHost.equals(that.srcHost) : that.srcHost != null) return false;
        if (srcLoginId != null ? !srcLoginId.equals(that.srcLoginId) : that.srcLoginId != null) return false;
        if (srcPassword != null ? !srcPassword.equals(that.srcPassword) : that.srcPassword != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (!synchAdapter.equals(that.synchAdapter)) return false;
        if (synchFrequency != null ? !synchFrequency.equals(that.synchFrequency) : that.synchFrequency != null)
            return false;
        if (synchType != null ? !synchType.equals(that.synchType) : that.synchType != null) return false;
        if (!transformationRule.equals(that.transformationRule)) return false;
        if (updateAttribute != null ? !updateAttribute.equals(that.updateAttribute) : that.updateAttribute != null)
            return false;
        if (!validationRule.equals(that.validationRule)) return false;
        if (wsScript != null ? !wsScript.equals(that.wsScript) : that.wsScript != null) return false;
        if (wsUrl != null ? !wsUrl.equals(that.wsUrl) : that.wsUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + synchAdapter.hashCode();
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (loadMatchOnly != null ? loadMatchOnly.hashCode() : 0);
        result = 31 * result + (updateAttribute != null ? updateAttribute.hashCode() : 0);
        result = 31 * result + (synchFrequency != null ? synchFrequency.hashCode() : 0);
        result = 31 * result + (synchType != null ? synchType.hashCode() : 0);
        result = 31 * result + (processRule != null ? processRule.hashCode() : 0);
        result = 31 * result + validationRule.hashCode();
        result = 31 * result + transformationRule.hashCode();
        result = 31 * result + (matchFieldName != null ? matchFieldName.hashCode() : 0);
        result = 31 * result + (matchManagedSysId != null ? matchManagedSysId.hashCode() : 0);
        result = 31 * result + (matchSrcFieldName != null ? matchSrcFieldName.hashCode() : 0);
        result = 31 * result + (srcLoginId != null ? srcLoginId.hashCode() : 0);
        result = 31 * result + (srcPassword != null ? srcPassword.hashCode() : 0);
        result = 31 * result + (srcHost != null ? srcHost.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (connectionUrl != null ? connectionUrl.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (queryTimeField != null ? queryTimeField.hashCode() : 0);
        result = 31 * result + (lastExecTime != null ? lastExecTime.hashCode() : 0);
        result = 31 * result + (lastRecProcessed != null ? lastRecProcessed.hashCode() : 0);
        result = 31 * result + (customMatchRule != null ? customMatchRule.hashCode() : 0);
        result = 31 * result + (customAdatperScript != null ? customAdatperScript.hashCode() : 0);
        result = 31 * result + (customMatchAttr != null ? customMatchAttr.hashCode() : 0);
        result = 31 * result + (baseDn != null ? baseDn.hashCode() : 0);
        result = 31 * result + (wsUrl != null ? wsUrl.hashCode() : 0);
        result = 31 * result + (wsScript != null ? wsScript.hashCode() : 0);
        return result;
    }
}
