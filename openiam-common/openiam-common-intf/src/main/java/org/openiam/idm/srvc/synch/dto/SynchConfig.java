package org.openiam.idm.srvc.synch.dto;

import org.openiam.am.srvc.constants.SearchScopeType;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.recon.dto.MatchConfig;
import org.openiam.idm.srvc.synch.domain.SynchConfigEntity;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchConfig",
        propOrder = {"synchConfigId",
                "name",
                "status",
                "synchAdapter",
                "useSystemPath",
                "fileName",
                "managedSysId",
                "loadMatchOnly",
                "updateAttribute",
                "synchFrequency",
                "companyId",
                "synchType",
                "processRule",
                "preSyncScript",
                "postSyncScript",
                "validationRule",
                "usePolicyMap",
                "useTransformationScript",
                "policyMapBeforeTransformation",
                "transformationRule",
                "matchFieldName",
                "matchManagedSysId",
                "matchSrcFieldName",
                "srcLoginId",
                "srcPassword",
                "srcHost",
                "driver",
                "connectionUrl",
                "query",
                "queryTimeField",
                "lastExecTime",
                "customMatchRule",
                "customMatchAttr",
                "customAdatperScript",
                "baseDn",
                "attributeNamesLookup",
                "searchScope",
                "lastRecProcessed",
                "wsUrl",
                "wsUri",
                "wsNameSpace",
                "wsOperation",
                "wsAttributes",
                "wsTargetEntityPath",
                "synchReviews",
                "parentAuditLogId"
        })
/**
 * Object containing the configuration for a synchronization task
 */
@DozerDTOCorrespondence(SynchConfigEntity.class)
public class SynchConfig implements MatchConfig, java.io.Serializable {

    private String synchConfigId;
    private String name;
    private String status;
    private String synchAdapter;
    private Boolean useSystemPath;
    private String fileName;
    private String managedSysId;
    private Integer loadMatchOnly;
    private Integer updateAttribute;
    private String synchFrequency;
    private String companyId;
    private String synchType;
    //private String deleteRule;
    private String processRule;
    private String preSyncScript;
    private String postSyncScript;
    private String validationRule;
    private Boolean usePolicyMap;
    private Boolean useTransformationScript;
    private Boolean policyMapBeforeTransformation;
    private String transformationRule;
    private String matchFieldName;
    private String matchManagedSysId;
    private String matchSrcFieldName;
    private String srcLoginId;
    private String srcPassword;
    private String srcHost;
    private String driver;
    private String connectionUrl;
    private String query;
    private String queryTimeField;
    @XmlSchemaType(name = "dateTime")
    private java.util.Date lastExecTime;
    private String lastRecProcessed;
    private String customMatchRule;
    private String customAdatperScript;
    private String customMatchAttr;
    private String baseDn;
    private String attributeNamesLookup;
    private SearchScopeType searchScope = SearchScopeType.SUBTREE_SCOPE;
    private String wsUrl;
    private String wsUri;
    private String wsNameSpace;
    private String wsOperation;
    private String wsAttributes;
    private String wsTargetEntityPath;

    private List<SynchReview> synchReviews;
    private String parentAuditLogId;

    public SynchConfig() {
    }

    public SynchConfig(String synchConfigId) {
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

    public String getParentAuditLogId() {
        return parentAuditLogId;
    }

    public void setParentAuditLogId(String parentAuditLogId) {
        this.parentAuditLogId = parentAuditLogId;
    }

    public Boolean getUseSystemPath() {
        return useSystemPath;
    }

    public void setUseSystemPath(Boolean useSystemPath) {
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

    public Boolean getUsePolicyMap() {
        return usePolicyMap;
    }

    public void setUsePolicyMap(Boolean usePolicyMap) {
        this.usePolicyMap = usePolicyMap;
    }

    public Boolean getUseTransformationScript() {
        return useTransformationScript;
    }

    public void setUseTransformationScript(Boolean useTransformationScript) {
        this.useTransformationScript = useTransformationScript;
    }

    public Boolean getPolicyMapBeforeTransformation() {
        return policyMapBeforeTransformation;
    }

    public void setPolicyMapBeforeTransformation(Boolean policyMapBeforeTransformation) {
        this.policyMapBeforeTransformation = policyMapBeforeTransformation;
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

    public String getWsNameSpace() {
        return wsNameSpace;
    }

    public String getWsUri() {
        return wsUri;
    }

    public void setWsUri(String wsUri) {
        this.wsUri = wsUri;
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

    public String getWsAttributes() {
        return wsAttributes;
    }

    public void setWsAttributes(String wsAttributes) {
        this.wsAttributes = wsAttributes;
    }

    public List<SynchReview> getSynchReviews() {
        return synchReviews;
    }

    public void setSynchReviews(List<SynchReview> synchReviews) {
        this.synchReviews = synchReviews;
    }

    public String getWsTargetEntityPath() {
        return wsTargetEntityPath;
    }

    public void setWsTargetEntityPath(String wsTargetEntityPath) {
        this.wsTargetEntityPath = wsTargetEntityPath;
    }

    @Override
    public String toString() {
        return "SynchConfig{" +
                "synchConfigId='" + synchConfigId + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", synchAdapter='" + synchAdapter + '\'' +
                ", useSystemPath='" + useSystemPath.toString() + '\'' +
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
                ", usePolicyMap='" + usePolicyMap.toString() + '\'' +
                ", useTransformationScript='" + useTransformationScript.toString() + '\'' +
                ", policyMapBeforeTransformation='" + policyMapBeforeTransformation.toString() + '\'' +
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
                ", attributeNamesLookup='" + attributeNamesLookup + '\'' +
                ", searchScope='" + searchScope + '\'' +
                '}';
    }
}
