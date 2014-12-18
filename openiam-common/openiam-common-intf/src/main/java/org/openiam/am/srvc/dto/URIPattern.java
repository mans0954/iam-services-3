package org.openiam.am.srvc.dto;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.am.srvc.comparator.AuthLevelGroupingXrefComparator;
import org.openiam.am.srvc.comparator.URIPatternMethodComparator;
import org.openiam.am.srvc.comparator.URIPatternSubstitutionComparator;
import org.openiam.am.srvc.domain.AuthLevelGroupingURIPatternXrefEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.domain.URIPatternErrorMappingEntity;
import org.openiam.am.srvc.domain.URIPatternMethodEntity;
import org.openiam.am.srvc.domain.URIPatternParameterEntity;
import org.openiam.am.srvc.domain.URIPatternSubstitutionEntity;
import org.openiam.am.srvc.groovy.AbstractRedirectURLGroovyProcessor;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.dto.MetadataElementPageTemplate;
import org.springframework.http.HttpMethod;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "URIPattern", propOrder = {
        "contentProviderId",
        "contentProviderName",
        "pattern",
        "isPublic",
        "resourceId",
        "resourceName",
        "metaEntitySet",
        "pageTemplates",
        "themeId",
        "groupingXrefs",
        "resourceCoorelatedName",
        "servers",
        "authProviderId",
        "methods",
        "params",
        "substitutions",
        "redirectTo",
        "errorMappings",
        "redirectToGroovyScript",
        "showOnApplicationPage",
        "url",
        "applicationName"
})
@DozerDTOCorrespondence(URIPatternEntity.class)
public class URIPattern extends AbstractMatchMode {

	private String contentProviderId;
	private String contentProviderName;
	private String pattern;
	private boolean isPublic;
	private String resourceId;
    private String resourceName;
	private Set<URIPatternMeta> metaEntitySet;
	private Set<MetadataElementPageTemplate> pageTemplates;
	private String themeId;
	private Set<AuthLevelGroupingURIPatternXref> groupingXrefs;
	private String resourceCoorelatedName;
	private Set<URIPatternServer> servers;
	private String authProviderId;
	private Set<URIPatternMethod> methods;
	private Set<URIPatternParameter> params;
	private Set<URIPatternSubstitution> substitutions;
	private String redirectTo;
	private Set<URIPatternErrorMapping> errorMappings;
	private String redirectToGroovyScript;
	private boolean showOnApplicationPage;
	private String url;
	private String applicationName;
	
	@Transient
	@XmlTransient
	private AbstractRedirectURLGroovyProcessor redirectProcessor;
	
	@Transient
	@XmlTransient
	private Map<HttpMethod, TreeSet<URIPatternMethod>> methodMap;
	
	@Transient
	@XmlTransient
	private TreeSet<URIPatternSubstitution> substititonOrderedSet;
	
	/*
	 * federation variables.  Internal use only
	 */
	@XmlTransient
	private int serverIdx = 0;
	
	@XmlTransient
	private List<RoundRobinServer> serverList;
	
	public void initTreeSet() {
		if(this.methods != null && !this.methods.isEmpty()) {
			if(this.methodMap == null) {
				this.methodMap = new HashMap<HttpMethod, TreeSet<URIPatternMethod>>();
			}
			for(final URIPatternMethod method : this.methods) {
				if(method.getMethod() != null) {
					if(!this.methodMap.containsKey(method.getMethod())) {
						this.methodMap.put(method.getMethod(), new TreeSet<URIPatternMethod>(new URIPatternMethodComparator()));
					}
					this.methodMap.get(method.getMethod()).add(method);
				}
			}
		}
		
		if(this.substitutions != null && !this.substitutions.isEmpty()) {
			if(this.substititonOrderedSet == null) {
				this.substititonOrderedSet = new TreeSet<URIPatternSubstitution>(new URIPatternSubstitutionComparator());
			}
			this.substititonOrderedSet.addAll(substitutions);
		}
	}
	
	public TreeSet<URIPatternMethod> getMethodTreeSet(final HttpMethod method) {
		return (methodMap != null) ? methodMap.get(method) : null;
	}
	
	
	
	/*
	public boolean isMethodDefined(final HttpMethod method) {
		return (methodMap != null) ? (methodMap.containsKey(method) && !methodMap.get(method).isEmpty()) : false;
	}
	*/
	
	public TreeSet<URIPatternSubstitution> getSubstititonOrderedSet() {
		return substititonOrderedSet;
	}

	public boolean isMethodsDefined() {
		return (methodMap != null) ? !methodMap.isEmpty() : false;
	}
	
	public void addParam(final URIPatternParameter param) {
		if(param != null) {
			if(this.params == null) {
				this.params = new HashSet<URIPatternParameter>();
			}
			this.params.add(param);
		}
	}
	
	public Set<URIPatternParameter> getParams() {
		return params;
	}
	public void setParams(Set<URIPatternParameter> params) {
		this.params = params;
	}
	public Set<URIPatternMethod> getMethods() {
		return methods;
	}
	public void setMethods(Set<URIPatternMethod> methods) {
		this.methods = methods;
	}
	public void addMethod(final URIPatternMethod method) {
		if(method != null) {
			if(this.methods == null) {
				this.methods = new HashSet<URIPatternMethod>();
			}
			this.methods.add(method);
		}
	}
	public String getContentProviderId() {
		return contentProviderId;
	}
	public void setContentProviderId(String contentProviderId) {
		this.contentProviderId = contentProviderId;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public boolean getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public Set<URIPatternMeta> getMetaEntitySet() {
		return metaEntitySet;
	}
	public void setMetaEntitySet(Set<URIPatternMeta> metaEntitySet) {
		this.metaEntitySet = metaEntitySet;
	}

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
	public Set<MetadataElementPageTemplate> getPageTemplates() {
		return pageTemplates;
	}
	public void setPageTemplates(Set<MetadataElementPageTemplate> pageTemplates) {
		this.pageTemplates = pageTemplates;
	}
	public String getContentProviderName() {
		return contentProviderName;
	}
	public void setContentProviderName(String contentProviderName) {
		this.contentProviderName = contentProviderName;
	}
	
	public String getThemeId() {
		return themeId;
	}
	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}
	
	public List<AuthLevelGroupingURIPatternXref> getOrderedGroupingXrefs() {
		List<AuthLevelGroupingURIPatternXref> sorted = null;
		if(groupingXrefs != null) {
			sorted = new ArrayList<AuthLevelGroupingURIPatternXref>(groupingXrefs);
			Collections.sort(sorted, new AuthLevelGroupingXrefComparator());
		}
		return sorted;
	}
	
	public Set<AuthLevelGroupingURIPatternXref> getGroupingXrefs() {
		return groupingXrefs;
	}
	public void setGroupingXrefs(Set<AuthLevelGroupingURIPatternXref> groupingXrefs) {
		this.groupingXrefs = groupingXrefs;
	}
	
	public String getResourceCoorelatedName() {
		return resourceCoorelatedName;
	}
	public void setResourceCoorelatedName(String resourceCoorelatedName) {
		this.resourceCoorelatedName = resourceCoorelatedName;
	}
	
	public Set<URIPatternServer> getServers() {
		return servers;
	}
	public void setServers(Set<URIPatternServer> servers) {
		this.servers = servers;
		if(CollectionUtils.isNotEmpty(servers)) {
			this.serverList = new LinkedList<>();
			for(final URIPatternServer server : servers) {
				this.serverList.add(new RoundRobinServer(server));
			}
		}
	}
	
	public synchronized RoundRobinServer getNextServer() {
		RoundRobinServer retVal = null;
		if(CollectionUtils.isNotEmpty(serverList)) {
			final int size = serverList.size();
			retVal = serverList.get(serverIdx % size);
			serverIdx++;
		}
		return retVal;
	}
	
	public String getAuthProviderId() {
		return authProviderId;
	}
	public void setAuthProviderId(String authProviderId) {
		this.authProviderId = authProviderId;
	}
	
	public Set<URIPatternSubstitution> getSubstitutions() {
		return substitutions;
	}
	public void setSubstitutions(Set<URIPatternSubstitution> substitutions) {
		this.substitutions = substitutions;
	}
	
	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	public Set<URIPatternErrorMapping> getErrorMappings() {
		return errorMappings;
	}
	public void setErrorMappings(Set<URIPatternErrorMapping> errorMappings) {
		this.errorMappings = errorMappings;
	}
	
	public String getRedirectToGroovyScript() {
		return redirectToGroovyScript;
	}
	public void setRedirectToGroovyScript(String redirectToGroovyScript) {
		this.redirectToGroovyScript = redirectToGroovyScript;
	}
	
	public AbstractRedirectURLGroovyProcessor getRedirectProcessor() {
		return redirectProcessor;
	}
	public void setRedirectProcessor(
			AbstractRedirectURLGroovyProcessor redirectProcessor) {
		this.redirectProcessor = redirectProcessor;
	}
	
	public boolean isShowOnApplicationPage() {
		return showOnApplicationPage;
	}
	public void setShowOnApplicationPage(boolean showOnApplicationPage) {
		this.showOnApplicationPage = showOnApplicationPage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((authProviderId == null) ? 0 : authProviderId.hashCode());
		result = prime
				* result
				+ ((contentProviderId == null) ? 0 : contentProviderId
						.hashCode());
		result = prime * result + (isPublic ? 1231 : 1237);
		result = prime * result + (showOnApplicationPage ? 1231 : 1237);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((redirectTo == null) ? 0 : redirectTo.hashCode());
		result = prime
				* result
				+ ((redirectToGroovyScript == null) ? 0
						: redirectToGroovyScript.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
		result = prime * result
				+ ((applicationName == null) ? 0 : applicationName.hashCode());
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
		URIPattern other = (URIPattern) obj;
		if (authProviderId == null) {
			if (other.authProviderId != null)
				return false;
		} else if (!authProviderId.equals(other.authProviderId))
			return false;
		if (contentProviderId == null) {
			if (other.contentProviderId != null)
				return false;
		} else if (!contentProviderId.equals(other.contentProviderId))
			return false;
		if (isPublic != other.isPublic)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (redirectTo == null) {
			if (other.redirectTo != null)
				return false;
		} else if (!redirectTo.equals(other.redirectTo))
			return false;
		if (redirectToGroovyScript == null) {
			if (other.redirectToGroovyScript != null)
				return false;
		} else if (!redirectToGroovyScript.equals(other.redirectToGroovyScript))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		if (themeId == null) {
			if (other.themeId != null)
				return false;
		} else if (!themeId.equals(other.themeId))
			return false;
		
		if(showOnApplicationPage != other.showOnApplicationPage) {
			return false;
		}
		
		if (applicationName == null) {
			if (other.applicationName != null)
				return false;
		} else if (!applicationName.equals(other.applicationName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "URIPattern [contentProviderId=" + contentProviderId
				+ ", pattern=" + pattern + ", isPublic=" + isPublic
				+ ", resourceId=" + resourceId + ", themeId=" + themeId
				+ ", authProviderId=" + authProviderId + ", redirectTo="
				+ redirectTo + ", redirectToGroovyScript="
				+ redirectToGroovyScript + ", matchMode=" + matchMode + ", id="
				+ id + ", objectState=" + objectState + ", requestorSessionID="
				+ requestorSessionID + ", requestorUserId=" + requestorUserId
				+ ", requestorLogin=" + requestorLogin + ", requestClientIP="
				+ requestClientIP + "]";
	}
	
	
}
