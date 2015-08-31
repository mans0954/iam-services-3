package org.openiam.service.integration.contentprovider;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPatternErrorMapping;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.am.srvc.dto.URIPatternMethodMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternServer;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.am.srvc.ws.URIFederationWebService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.service.integration.AbstractServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.testng.Assert;

public abstract class AbstractURIFederationTest extends AbstractServiceTest {


	@Autowired
	@Qualifier("uriFederationServiceClient")
	protected URIFederationWebService uriFederationServiceClient;
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	protected ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("resourceServiceClient")
    protected ResourceDataService resourceDataService;
	
	@Autowired
	@Qualifier("authAttributeServiceClient")
	protected AuthResourceAttributeWebService authAttributeServiceClient;

	protected void assertSuccess(final URIFederationResponse response, final boolean shouldHaveMethod) {
		Assert.assertTrue(response.isSuccess());
		Assert.assertTrue(StringUtils.isNotBlank(response.getRedirectTo()));
		Assert.assertNotNull(response.getServer());
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getSubstitutionList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getErrorMappingList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getAuthLevelTokenList()));
		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getRuleTokenList()));
		if(shouldHaveMethod) {
			Assert.assertTrue(StringUtils.isNotBlank(response.getMethodId()));
		}
	}
	
	protected Set<URIPatternServer> getPatternServers() {
		final Set<URIPatternServer> retval = new HashSet<URIPatternServer>();
		for(int i = 0; i < 3; i++) {
			final URIPatternServer val = new URIPatternServer();
			val.setServerURL(getRandomName());
			retval.add(val);
		}
		return retval;
	}
	
	protected Set<URIPatternErrorMapping> getErrorMappings() {
		final Set<URIPatternErrorMapping> retval = new HashSet<URIPatternErrorMapping>();
		for(int i = 0; i < 3; i++) {
			final URIPatternErrorMapping val = new URIPatternErrorMapping();
			val.setErrorCode(RandomUtils.nextInt(500));
			val.setRedirectURL("/" + getRandomName());
			retval.add(val);
		}
		return retval;
	}
	
	protected Set<URIPatternSubstitution> getSubstitutions() {
		final Set<URIPatternSubstitution> substitutions = new HashSet<URIPatternSubstitution>();
		for(int i = 0; i < 3; i++) {
			final URIPatternSubstitution substitution = new URIPatternSubstitution();
			substitution.setQuery(getRandomName());
			substitution.setReplaceWith(getRandomName());
			substitution.setOrder(Integer.valueOf(i));
			substitutions.add(substitution);
		}
		return substitutions;
	}
	
	protected Set<URIPatternMethodMeta> getMethodMetaEntitySet() {
		final Set<URIPatternMethodMeta> retval = new HashSet<URIPatternMethodMeta>();
		contentProviderServiceClient.getAllMetaType().forEach(type -> {
			final URIPatternMethodMeta meta = new URIPatternMethodMeta();
			meta.setContentType(getRandomName());
			meta.setName(getRandomName());
			meta.setMetaType(type);
			
			final Set<URIPatternMethodMetaValue> metaValueSet = new HashSet<URIPatternMethodMetaValue>();
			for(int i = 0; i < 4; i++) {
				final URIPatternMethodMetaValue value = new URIPatternMethodMetaValue();
				value.setName(getRandomName());
				if(i == 0) {
					value.setEmptyValue(true);
				} else if(i == 1) {
					value.setStaticValue(getRandomName());
				} else if(i == 2) {
					value.setFetchedValue(getRandomName());
				} else if(i == 3) {
					value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
				}
				metaValueSet.add(value);
			}
			meta.setMetaValueSet(metaValueSet);
			retval.add(meta);
		});
		return retval;
	}
	
	protected Set<URIPatternMeta> getPatternMetaEntitySet() {
		final Set<URIPatternMeta> retval = new HashSet<URIPatternMeta>();
		contentProviderServiceClient.getAllMetaType().forEach(type -> {
			final URIPatternMeta meta = new URIPatternMeta();
			meta.setContentType(getRandomName());
			meta.setName(getRandomName());
			meta.setMetaType(type);
			
			final Set<URIPatternMetaValue> metaValueSet = new HashSet<URIPatternMetaValue>();
			for(int i = 0; i < 4; i++) {
				final URIPatternMetaValue value = new URIPatternMetaValue();
				value.setName(getRandomName());
				if(i == 0) {
					value.setEmptyValue(true);
				} else if(i == 1) {
					value.setStaticValue(getRandomName());
				} else if(i == 2) {
					value.setFetchedValue(getRandomName());
				} else if(i == 3) {
					value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
				}
				metaValueSet.add(value);
			}
			meta.setMetaValueSet(metaValueSet);
			retval.add(meta);
		});
		return retval;
	}
	
	protected Set<URIPatternMethod> getMethods(final HttpMethod method, final int count) {
		final Set<URIPatternMethod> retVal = new HashSet<URIPatternMethod>();
		for(int i = 0; i < count; i++) {
			final URIPatternMethod patternMethod = new URIPatternMethod();
			patternMethod.setMetaEntitySet(getMethodMetaEntitySet());
			if(i == 0) {
				patternMethod.setMatchMode(PatternMatchMode.IGNORE);
			} else if(i == 1) {
				patternMethod.setMatchMode(PatternMatchMode.NO_PARAMS);
			} else if(i == 2) {
				patternMethod.setMatchMode(PatternMatchMode.ANY_PARAMS);
			} else if(i > 2) {
				patternMethod.setMatchMode(PatternMatchMode.SPECIFIC_PARAMS);
				for(int j = i; j < count + 1; j++) {
					final URIPatternMethodParameter param = new URIPatternMethodParameter();
					param.setName("" + j);
					final List<String> values = new LinkedList<String>();
					values.add("" + j);
					param.setValues(values);
					patternMethod.addParam(param);
				}
			}
			
			patternMethod.setMethod(method);
			retVal.add(patternMethod);
		}
		return retVal;
	}
}
