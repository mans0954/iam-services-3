package org.openiam.idm.srvc.synch.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.synch.dto.SynchConfig;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "SYNCH_CONFIG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DozerDTOCorrespondence(SynchConfig.class)
public class SynchConfigEntity implements Serializable {
    private static final long serialVersionUID = -748384789293890253L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "SYNCH_CONFIG_ID", length = 32, nullable = false)
    private String synchConfigId;
    @Column(name = "NAME", length = 60)
    private String name;
    @Column(name = "STATUS", length = 20)
    private String status;
    @Column(name = "SYNCH_SRC", length = 20)
    private String synchAdapter;
    @Column(name = "USE_SYSTEM_PATH")
    @Type(type = "yes_no")
    private boolean useSystemPath;
    @Column(name = "FILE_NAME", length = 80)
    private String fileName;
    @Column(name = "MANAGED_SYS_ID", length = 32)
    private String managedSysId;
    @Column(name = "LOAD_MATCH_ONLY")
    private Integer loadMatchOnly;
    @Column(name = "UPDATE_ATTRIBUTE")
    private Integer updateAttribute;
    @Column(name = "SYNCH_FREQUENCY", length = 20)
    private String synchFrequency;
    @Column(name = "COMPANY_ID", length = 32)
    private String companyId;
    @Column(name = "SYNCH_TYPE", length = 20)
    private String synchType;
    //private String deleteRule;
    @Column(name = "PROCESS_RULE", length = 80)
    private String processRule;
    @Column(name = "PRE_SYNC_SCRIPT", length = 80)
    private String preSyncScript;
    @Column(name = "POST_SYNC_SCRIPT", length = 80)
    private String postSyncScript;
    @Column(name = "VALIDATION_RULE", length = 80)
    private String validationRule;
    @Column(name = "USE_POLICY_MAP")
    @Type(type = "yes_no")
    private boolean usePolicyMap = true;
    @Column(name = "USE_TRANSFORM_SCRIPT")
    @Type(type = "yes_no")
    private boolean useTransformationScript = true;
    @Column(name = "POLICY_MAP_BEFORE_TRANSFORM")
    @Type(type = "yes_no")
    private boolean policyMapBeforeTransformation = true;
    @Column(name = "TRANSFORMATION_RULE", length = 80)
    private String transformationRule;
    @Column(name = "MATCH_FIELD_NAME", length = 40)
    private String matchFieldName;
    @Column(name = "MATCH_MANAGED_SYS_ID", length = 32)
    private String matchManagedSysId;
    @Column(name = "MATCH_SRC_FIELD_NAME", length = 40)
    private String matchSrcFieldName;
    @Column(name = "SRC_LOGIN_ID", length = 100)
    private String srcLoginId;
    @Column(name = "SRC_PASSWORD", length = 100)
    private String srcPassword;
    @Column(name = "SRC_HOST", length = 100)
    private String srcHost;
    @Column(name = "DRIVER", length = 50)
    private String driver;
    @Column(name = "CONNECTION_URL", length = 500)
    private String connectionUrl;
    @Column(name = "QUERY", length = 1000)
    private String query;
    @Column(name = "QUERY_TIME_FIELD", length = 50)
    private String queryTimeField;
    @Column(name = "LAST_EXEC_TIME", length = 19)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastExecTime;
    @Column(name = "LAST_REC_PROCESSED", length = 32)
    private String lastRecProcessed;
    @Column(name = "CUSTOM_MATCH_RULE", length = 10)
    private String customMatchRule;
    @Column(name = "CUSTOM_ADAPTER_SCRIPT", length = 80)
    private String customAdatperScript;
    @Column(name = "CUSTOM_MATCH_ATTR", length = 40)
    private String customMatchAttr;
    @Column(name = "BASE_DN", length = 50)
    private String baseDn;
    @Column(name = "ATTRIBUTE_NAMES_LOOKUP", length = 120)
    private String attributeNamesLookup;
    @Column(name = "SEARCH_SCOPE")
    @Enumerated(EnumType.ORDINAL)
    private SearchScopeType searchScope = SearchScopeType.SUBTREE_SCOPE;
    @Column(name = "WS_URL", length = 100)
    private String wsUrl;
    @Column(name = "WS_URI", length = 100)
    private String wsUri;
    @Column(name = "WS_NAME_SPACE", length = 100)
    private String wsNameSpace;
    @Column(name = "WS_OPERATION", length = 100)
    private String wsOperation;
    @Column(name = "WS_ATTRIBUTES_STRING", length = 255)
    private String wsAttributes;
    @Column(name = "WS_TARGET_ENTITY_PATH", length = 255)
    private String wsTargetEntityPath;
    @OneToMany(mappedBy = "synchConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SynchReviewEntity> synchReviews;

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

    public boolean getUseSystemPath() {
        return useSystemPath;
    }

    public void setUseSystemPath(boolean useSystemPath) {
        this.useSystemPath = useSystemPath;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public boolean getUseTransformationScript() {
        return useTransformationScript;
    }

    public void setUseTransformationScript(boolean useTransformationScript) {
        this.useTransformationScript = useTransformationScript;
    }

    public boolean getPolicyMapBeforeTransformation() {
        return policyMapBeforeTransformation;
    }

    public void setPolicyMapBeforeTransformation(boolean policyMapBeforeTransformation) {
        this.policyMapBeforeTransformation = policyMapBeforeTransformation;
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

    public String getPreSyncScript() {
        return preSyncScript;
    }

    public void setPreSyncScript(String preSyncScript) {
        this.preSyncScript = preSyncScript;
    }

    public String getPostSyncScript() {
        return postSyncScript;
    }

    public void setPostSyncScript(String postSyncScript) {
        this.postSyncScript = postSyncScript;
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

    public String getAttributeNamesLookup() {
        return attributeNamesLookup;
    }

    public void setAttributeNamesLookup(String attributeNamesLookup) {
        this.attributeNamesLookup = attributeNamesLookup;
    }

    public SearchScopeType getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(SearchScopeType searchScope) {
        this.searchScope = searchScope;
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

    public List<SynchReviewEntity> getSynchReviews() {
        return synchReviews;
    }

    public void setSynchReviews(List<SynchReviewEntity> synchReviews) {
        this.synchReviews = synchReviews;
    }

    public String getWsAttributes() {
        return wsAttributes;
    }

    public void setWsAttributes(String wsAttributes) {
        this.wsAttributes = wsAttributes;
    }

    public String getWsUri() {
        return wsUri;
    }

    public void setWsUri(String wsUri) {
        this.wsUri = wsUri;
    }

    public String getWsNameSpace() {
        return wsNameSpace;
    }

    public void setWsNameSpace(String wsNameSpace) {
        this.wsNameSpace = wsNameSpace;
    }

    public String getWsOperation() {
        return wsOperation;
    }

    public void setWsOperation(String wsOperation) {
        this.wsOperation = wsOperation;
    }

    public String getWsTargetEntityPath() {
        return wsTargetEntityPath;
    }

    public void setWsTargetEntityPath(String wsTargetEntityPath) {
        this.wsTargetEntityPath = wsTargetEntityPath;
    }

    @Override
    public String toString() {
        return "SynchConfigEntity{" +
                "synchConfigId='" + synchConfigId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", synchAdapter='" + synchAdapter + '\'' +
                ", useSystemPath=" + useSystemPath +
                ", fileName='" + fileName + '\'' +
                ", managedSysId='" + managedSysId + '\'' +
                ", loadMatchOnly=" + loadMatchOnly +
                ", updateAttribute=" + updateAttribute +
                ", synchFrequency='" + synchFrequency + '\'' +
                ", companyId='" + companyId + '\'' +
                ", synchType='" + synchType + '\'' +
                ", processRule='" + processRule + '\'' +
                ", preSyncScript='" + preSyncScript + '\'' +
                ", postSyncScript='" + postSyncScript + '\'' +
                ", validationRule='" + validationRule + '\'' +
                ", usePolicyMap=" + usePolicyMap +
                ", useTransformationScript=" + useTransformationScript +
                ", policyMapBeforeTransformation=" + policyMapBeforeTransformation +
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
                ", lastRecProcessed='" + lastRecProcessed + '\'' +
                ", customMatchRule='" + customMatchRule + '\'' +
                ", customAdatperScript='" + customAdatperScript + '\'' +
                ", customMatchAttr='" + customMatchAttr + '\'' +
                ", baseDn='" + baseDn + '\'' +
                ", attributeNamesLookup='" + attributeNamesLookup + '\'' +
                ", searchScope=" + searchScope +
                ", wsUrl='" + wsUrl + '\'' +
                ", wsUri='" + wsUri + '\'' +
                ", wsNameSpace='" + wsNameSpace + '\'' +
                ", wsOperation='" + wsOperation + '\'' +
                ", wsAttributes='" + wsAttributes + '\'' +
                ", synchReviews=" + synchReviews +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynchConfigEntity)) return false;

        SynchConfigEntity that = (SynchConfigEntity) o;

        if (policyMapBeforeTransformation != that.policyMapBeforeTransformation) return false;
        if (usePolicyMap != that.usePolicyMap) return false;
        if (useSystemPath != that.useSystemPath) return false;
        if (useTransformationScript != that.useTransformationScript) return false;
        if (attributeNamesLookup != null ? !attributeNamesLookup.equals(that.attributeNamesLookup) : that.attributeNamesLookup != null)
            return false;
        if (baseDn != null ? !baseDn.equals(that.baseDn) : that.baseDn != null) return false;
        if (companyId != null ? !companyId.equals(that.companyId) : that.companyId != null) return false;
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
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (postSyncScript != null ? !postSyncScript.equals(that.postSyncScript) : that.postSyncScript != null)
            return false;
        if (preSyncScript != null ? !preSyncScript.equals(that.preSyncScript) : that.preSyncScript != null)
            return false;
        if (processRule != null ? !processRule.equals(that.processRule) : that.processRule != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (queryTimeField != null ? !queryTimeField.equals(that.queryTimeField) : that.queryTimeField != null)
            return false;
        if (searchScope != that.searchScope) return false;
        if (srcHost != null ? !srcHost.equals(that.srcHost) : that.srcHost != null) return false;
        if (srcLoginId != null ? !srcLoginId.equals(that.srcLoginId) : that.srcLoginId != null) return false;
        if (srcPassword != null ? !srcPassword.equals(that.srcPassword) : that.srcPassword != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (synchAdapter != null ? !synchAdapter.equals(that.synchAdapter) : that.synchAdapter != null) return false;
        if (synchConfigId != null ? !synchConfigId.equals(that.synchConfigId) : that.synchConfigId != null)
            return false;
        if (synchFrequency != null ? !synchFrequency.equals(that.synchFrequency) : that.synchFrequency != null)
            return false;
        if (synchType != null ? !synchType.equals(that.synchType) : that.synchType != null) return false;
        if (transformationRule != null ? !transformationRule.equals(that.transformationRule) : that.transformationRule != null)
            return false;
        if (updateAttribute != null ? !updateAttribute.equals(that.updateAttribute) : that.updateAttribute != null)
            return false;
        if (validationRule != null ? !validationRule.equals(that.validationRule) : that.validationRule != null)
            return false;
        if (wsAttributes != null ? !wsAttributes.equals(that.wsAttributes) : that.wsAttributes != null) return false;
        if (wsNameSpace != null ? !wsNameSpace.equals(that.wsNameSpace) : that.wsNameSpace != null) return false;
        if (wsOperation != null ? !wsOperation.equals(that.wsOperation) : that.wsOperation != null) return false;
        if (wsUri != null ? !wsUri.equals(that.wsUri) : that.wsUri != null) return false;
        if (wsUrl != null ? !wsUrl.equals(that.wsUrl) : that.wsUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = synchConfigId != null ? synchConfigId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (synchAdapter != null ? synchAdapter.hashCode() : 0);
        result = 31 * result + (useSystemPath ? 1 : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (managedSysId != null ? managedSysId.hashCode() : 0);
        result = 31 * result + (loadMatchOnly != null ? loadMatchOnly.hashCode() : 0);
        result = 31 * result + (updateAttribute != null ? updateAttribute.hashCode() : 0);
        result = 31 * result + (synchFrequency != null ? synchFrequency.hashCode() : 0);
        result = 31 * result + (companyId != null ? companyId.hashCode() : 0);
        result = 31 * result + (synchType != null ? synchType.hashCode() : 0);
        result = 31 * result + (processRule != null ? processRule.hashCode() : 0);
        result = 31 * result + (preSyncScript != null ? preSyncScript.hashCode() : 0);
        result = 31 * result + (postSyncScript != null ? postSyncScript.hashCode() : 0);
        result = 31 * result + (validationRule != null ? validationRule.hashCode() : 0);
        result = 31 * result + (usePolicyMap ? 1 : 0);
        result = 31 * result + (useTransformationScript ? 1 : 0);
        result = 31 * result + (policyMapBeforeTransformation ? 1 : 0);
        result = 31 * result + (transformationRule != null ? transformationRule.hashCode() : 0);
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
        result = 31 * result + (attributeNamesLookup != null ? attributeNamesLookup.hashCode() : 0);
        result = 31 * result + (searchScope != null ? searchScope.hashCode() : 0);
        result = 31 * result + (wsUrl != null ? wsUrl.hashCode() : 0);
        result = 31 * result + (wsUri != null ? wsUri.hashCode() : 0);
        result = 31 * result + (wsNameSpace != null ? wsNameSpace.hashCode() : 0);
        result = 31 * result + (wsOperation != null ? wsOperation.hashCode() : 0);
        result = 31 * result + (wsAttributes != null ? wsAttributes.hashCode() : 0);
        return result;
    }
}
