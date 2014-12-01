package org.openiam.service.integration.contentprovider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXrefId;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXrefId;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternErrorMapping;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.am.srvc.dto.URIPatternMethodMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.dto.URIPatternServer;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.am.srvc.ws.AuthProviderWebService;
import org.openiam.am.srvc.ws.AuthResourceAttributeWebService;
import org.openiam.am.srvc.ws.ContentProviderWebService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.policy.dto.PolicyConstants;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class URIPatternServiceTest extends AbstractContentProviderServiceTest<URIPattern, URIPatternSearchBean> {
	
	@Autowired
	@Qualifier("contentProviderServiceClient")
	private ContentProviderWebService contentProviderServiceClient;
	
	@Autowired
	@Qualifier("authProviderServiceClient")
	private AuthProviderWebService authProviderServiceClient;
	
	@Autowired
	@Qualifier("authAttributeServiceClient")
	private AuthResourceAttributeWebService authAttributeServiceClient;
	
	private ContentProvider cp = null;
	
	@BeforeClass
	protected void _setUp() throws Exception {
	    cp = super.createContentProvider();
	    final Response response = contentProviderServiceClient.saveContentProvider(cp);
	    Assert.assertNotNull(response);
	    Assert.assertTrue(response.isSuccess());
	    final String id = (String)response.getResponseValue();
	    cp = contentProviderServiceClient.getContentProvider(id);
	    Assert.assertNotNull(cp);
	}
	
	@AfterClass
    public void _tearDown() throws Exception {
    	if(cp != null) {
    		final Response response = contentProviderServiceClient.deleteContentProvider(cp.getId());
    		Assert.assertNotNull(response);
    		Assert.assertTrue(response.isSuccess());
    	}
    }
	
	private void addToCollection(final int howMany, final Collection collection, final EntityGenerator generator) {
		IntStream.range(0, howMany).forEach( (int nbr) -> {
			collection.add(generator.generate());
		});
	}
	
	private URIPattern assertCollection(final URIPattern pattern, final CollectionOperation assertion, final int size) {
		final Response response = saveAndAssert(pattern);
		final URIPattern serviceObject = get((String)response.getResponseValue());
		Assert.assertNotNull(serviceObject);
		if(size == 0) {
			Assert.assertTrue(CollectionUtils.isEmpty(assertion.get(serviceObject)));
		} else {
			Assert.assertNotNull(assertion.get(serviceObject));
			Assert.assertEquals(assertion.get(serviceObject).size(), size);
		}
		
		return serviceObject;
	}
	
	private void testCollection(URIPattern targetObject, Set targetSet, final CollectionOperation operation, final EntityGenerator generator) {
		/* add 4 */
		addToCollection(4, targetSet, generator);
		operation.set(targetObject, targetSet);
		targetObject = assertCollection(targetObject, operation, targetSet.size());
		targetSet = operation.get(targetObject);
		
		/* delete all but 1 */
		int itSize = targetSet.size();
		final Iterator it1 = targetSet.iterator();
		while(itSize > 1) {
			it1.next();
			it1.remove();
			itSize--;
		}
		targetObject = assertCollection(targetObject, operation, targetSet.size());
		targetSet = operation.get(targetObject);
		
		/* add 2 */
		addToCollection(2, targetSet, generator);
		targetObject = assertCollection(targetObject, operation, targetSet.size());
		targetSet = operation.get(targetObject);
		
		/* add 2 more */
		addToCollection(2, targetSet, generator);
		targetObject = assertCollection(targetObject, operation, targetSet.size());
		targetSet = operation.get(targetObject);
		
		/* remove 2, and add 3 more */
		final Iterator it2 = targetSet.iterator();
		itSize = targetSet.size();
		while(itSize > 2) {
			it2.next();
			it2.remove();
			itSize--;
		}
		
		addToCollection(3, targetSet, generator);
		targetObject = assertCollection(targetObject, operation, targetSet.size());
	}
	
	@Test
	public void testMethods() {
		URIPattern pattern = createBean();
		pattern.setErrorMappings(null);
		final Set<URIPatternMethod> targetSet = new HashSet<URIPatternMethod>();
		
		final CollectionOperation<URIPattern, URIPatternMethod> operation = new CollectionOperation<URIPattern, URIPatternMethod>() {
			@Override
			public Set<URIPatternMethod> get(final URIPattern t) {
				return t.getMethods();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternMethod> set) {
				t.setMethods(set);
			}
		};
		
		final EntityGenerator<URIPatternMethod> generator = new EntityGenerator<URIPatternMethod>() {
			int idx = 0;
			
			@Override
			public URIPatternMethod generate() {
				final URIPatternMethod generated = new URIPatternMethod();
				generated.setMethod(HttpMethod.values()[idx++ % HttpMethod.values().length]);
				
				final Set<URIPatternMethodParameter> parameters = new HashSet<URIPatternMethodParameter>();
				for(int i = 0; i < 10; i++) {
					final URIPatternMethodParameter param = new URIPatternMethodParameter();
					param.setName(getRandomName());
					final List<String> values = new LinkedList<String>();
					for(int j = 0; j < 10; j++) {
						values.add(getRandomName());
					}
					param.setValues(values);
					parameters.add(param);
				}
				generated.setParams(parameters);
				
				final List<URIPatternMetaType> metaTypes = contentProviderServiceClient.getAllMetaType();
				final Set<URIPatternMethodMeta> metaEntitySet = new HashSet<URIPatternMethodMeta>();
				for(int i = 0; i < 10; i++) {
					final URIPatternMethodMeta meta = new URIPatternMethodMeta();
					meta.setName(getRandomName());
					meta.setContentType(getRandomName());
					meta.setMetaType(metaTypes.get(i % metaTypes.size()));
					
					final Set<URIPatternMethodMetaValue> metaValueSet = new HashSet<URIPatternMethodMetaValue>();
					for(int j = 0; j < 10; j++) {
						final URIPatternMethodMetaValue value = new URIPatternMethodMetaValue();
						value.setName(getRandomName());
						if(j % 2 == 0) {
							value.setFetchedValue(getRandomName());
						} else if(j % 1 == 0) {
							value.setStaticValue(getRandomName());
						} else {
							value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
						}
						metaValueSet.add(value);
					}
					meta.setMetaValueSet(metaValueSet);
				}
				generated.setMetaEntitySet(metaEntitySet);
				return generated;
			}
		};
		
		testCollection(pattern, targetSet, operation, generator);
	}
	
	@Test
	public void testMetaEntitySet() {
		URIPattern pattern = createBean();
		pattern.setErrorMappings(null);
		final Set<URIPatternMeta> targetSet = new HashSet<URIPatternMeta>();
		
		final CollectionOperation<URIPattern, URIPatternMeta> operation = new CollectionOperation<URIPattern, URIPatternMeta>() {
			@Override
			public Set<URIPatternMeta> get(final URIPattern t) {
				return t.getMetaEntitySet();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternMeta> set) {
				t.setMetaEntitySet(set);
			}
		};
		
		final EntityGenerator<URIPatternMeta> generator = new EntityGenerator<URIPatternMeta>() {
			int idx = 0;
			
			@Override
			public URIPatternMeta generate() {
				final List<URIPatternMetaType> metaTypes = contentProviderServiceClient.getAllMetaType();
				
				final URIPatternMeta generated = new URIPatternMeta();
				generated.setContentType(getRandomName());
				generated.setName(getRandomName());
				generated.setMetaType(metaTypes.get(idx++ % (metaTypes.size() -1 )));
				final Set<URIPatternMetaValue> metaValueSet = new HashSet<URIPatternMetaValue>();
				for(int j = 0; j < 10; j++) {
					final URIPatternMetaValue value = new URIPatternMetaValue();
					value.setName(getRandomName());
					if(j % 2 == 0) {
						value.setFetchedValue(getRandomName());
					} else if(j % 1 == 0) {
						value.setStaticValue(getRandomName());
					} else {
						value.setAmAttribute(authAttributeServiceClient.getAmAttributeList().get(0));
					}
					metaValueSet.add(value);
				}
				generated.setMetaValueSet(metaValueSet);
				return generated;
			}
		};
		
		testCollection(pattern, targetSet, operation, generator);
	}
	
	@Test
	public void testParams() {
		URIPattern pattern = createBean();
		pattern.setErrorMappings(null);
		final Set<URIPatternParameter> targetSet = new HashSet<URIPatternParameter>();
		
		final CollectionOperation<URIPattern, URIPatternParameter> operation = new CollectionOperation<URIPattern, URIPatternParameter>() {
			@Override
			public Set<URIPatternParameter> get(final URIPattern t) {
				return t.getParams();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternParameter> set) {
				t.setParams(set);
			}
		};
		
		final EntityGenerator<URIPatternParameter> generator = new EntityGenerator<URIPatternParameter>() {

			@Override
			public URIPatternParameter generate() {
				final URIPatternParameter generated = new URIPatternParameter();
				generated.setName(getRandomName());
				final List<String> values = new LinkedList<String>();
				for(int i = 0; i < 3; i++) {
					values.add(getRandomName());
				}
				generated.setValues(values);
				return generated;
			}
		};
		
		testCollection(pattern, targetSet, operation, generator);
	}
	
	@Test
	public void testSubstitutions() {
		URIPattern pattern = createBean();
		pattern.setErrorMappings(null);
		final Set<URIPatternSubstitution> targetSet = new HashSet<URIPatternSubstitution>();
		
		final CollectionOperation<URIPattern, URIPatternSubstitution> operation = new CollectionOperation<URIPattern, URIPatternSubstitution>() {
			@Override
			public Set<URIPatternSubstitution> get(final URIPattern t) {
				return t.getSubstitutions();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternSubstitution> set) {
				t.setSubstitutions(set);
			}
		};
		
		final EntityGenerator<URIPatternSubstitution> generator = new EntityGenerator<URIPatternSubstitution>() {

			@Override
			public URIPatternSubstitution generate() {
				final URIPatternSubstitution generated = new URIPatternSubstitution();
				generated.setOrder(RandomUtils.nextInt(100));
				generated.setReplaceWith(getRandomName());
				generated.setQuery(getRandomName());
				return generated;
			}
		};
		
		testCollection(pattern, targetSet, operation, generator);
	}
	
	@Test
	public void testErrorMappings() {
		URIPattern pattern = createBean();
		pattern.setErrorMappings(null);
		final Set<URIPatternErrorMapping> targetSet = new HashSet<URIPatternErrorMapping>();
		
		final CollectionOperation<URIPattern, URIPatternErrorMapping> operation = new CollectionOperation<URIPattern, URIPatternErrorMapping>() {
			@Override
			public Set<URIPatternErrorMapping> get(final URIPattern t) {
				return t.getErrorMappings();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternErrorMapping> set) {
				t.setErrorMappings(set);
			}
		};
		
		final EntityGenerator<URIPatternErrorMapping> generator = new EntityGenerator<URIPatternErrorMapping>() {

			@Override
			public URIPatternErrorMapping generate() {
				final URIPatternErrorMapping mapping = new URIPatternErrorMapping();
				mapping.setErrorCode(RandomUtils.nextInt(500));
				mapping.setRedirectURL("/" + getRandomName());
				return mapping;
			}
		};
		
		testCollection(pattern, targetSet, operation, generator);
	}
	
	@Test
	public void testServers() {
		URIPattern pattern = createBean();
		pattern.setServers(null);
		Set<URIPatternServer> targetSet = new HashSet<URIPatternServer>();
		
		final CollectionOperation<URIPattern, URIPatternServer> operation = new CollectionOperation<URIPattern, URIPatternServer>() {
			@Override
			public Set<URIPatternServer> get(final URIPattern t) {
				return t.getServers();
			}

			@Override
			public void set(URIPattern t, Set<URIPatternServer> set) {
				t.setServers(set);
			}
		};
		
		final EntityGenerator<URIPatternServer> generator = new EntityGenerator<URIPatternServer>() {

			@Override
			public URIPatternServer generate() {
				final URIPatternServer server = new URIPatternServer();
				server.setServerURL(getRandomName());
				return server;
			}
		};
			
		testCollection(pattern, targetSet, operation, generator);
	}
	
	/*
	@Test
	public void testGroupings() {
		final CollectionOperation<URIPattern, AuthLevelGroupingURIPatternXref> operation = new CollectionOperation<URIPattern, AuthLevelGroupingURIPatternXref>() {
			@Override
			public Set<AuthLevelGroupingURIPatternXref> get(final URIPattern t) {
				return t.getGroupingXrefs();
			}

			@Override
			public void set(final URIPattern t, Set<AuthLevelGroupingURIPatternXref> set) {
				t.setGroupingXrefs(set);
			}
		};
		
		final EntityGenerator<AuthLevelGroupingURIPatternXref> generator = new EntityGenerator<AuthLevelGroupingURIPatternXref>() {
			
			int currentIdx = 0;

			@Override
			public AuthLevelGroupingURIPatternXref generate() {
				final List<AuthLevelGrouping> groupings = contentProviderServiceClient.getAuthLevelGroupingList();
				final AuthLevelGroupingURIPatternXref xref = new AuthLevelGroupingURIPatternXref();
				final AuthLevelGroupingURIPatternXrefId id = new AuthLevelGroupingURIPatternXrefId();
				id.setGroupingId(groupings.get(currentIdx++ % (groupings.size() - 1)).getId());
				xref.setId(id);
				return xref;
			}
		};
		
		URIPattern pattern = createBean();
		pattern.setGroupingXrefs(null);
		final Set<AuthLevelGroupingURIPatternXref> groupingXrefs = new HashSet<AuthLevelGroupingURIPatternXref>();
		
		testCollection(pattern, groupingXrefs, operation, generator);
	}
	*/
	
	@Test
	public void testErrors() {
		final URIPattern pattern = createBean();
		pattern.setRedirectTo(getRandomName());
		Response response = save(pattern);
		assertResponseCode(response, ResponseCode.INVALID_PATTERN_REDIRECT_URL);
		
		pattern.setRedirectTo(null);
		pattern.setRedirectToGroovyScript(getRandomName());
		response = save(pattern);
		assertResponseCode(response, ResponseCode.INVALID_ERROR_REDIRECT_URL_GROOVY_SCRIPT);
		
		pattern.setRedirectToGroovyScript(null);
		pattern.setPattern(null);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.CONTENT_PROVIDER_URI_PATTERN_NOT_SET);
		
		pattern.setPattern("/a/*/b/*/c/");
		response = save(pattern);
		assertResponseCode(response, ResponseCode.URI_PATTERN_INVALID);
		
		pattern.setPattern(getRandomName());
		final URIPatternErrorMapping errorMapping = new URIPatternErrorMapping();
		errorMapping.setErrorCode(404);
		errorMapping.setRedirectURL(getRandomName());
		final Set<URIPatternErrorMapping> errorMappings = new HashSet<URIPatternErrorMapping>();
		errorMappings.add(errorMapping);
		pattern.setErrorMappings(errorMappings);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.INVALID_ERROR_REDIRECT_URL);
		
		errorMapping.setRedirectURL("/foobar");
		final URIPatternSubstitution substitution = new URIPatternSubstitution();
		final Set<URIPatternSubstitution> substitutions = new HashSet<URIPatternSubstitution>();
		substitutions.add(substitution);
		pattern.setSubstitutions(substitutions);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.ORDER_REQUIRED);
		
		substitution.setOrder(0);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.URI_PATTTERN_SUBSTITUTION_QUERY_REQUIRED);
		
		substitution.setQuery(getRandomName());
		response = save(pattern);
		assertResponseCode(response, ResponseCode.URI_PATTTERN_SUBSTITUTION_REPLACE_WITH_REQUIRED);
		
		substitution.setReplaceWith(getRandomName());
		final URIPatternServer server = new URIPatternServer();
		final Set<URIPatternServer> servers = new HashSet<URIPatternServer>();
		servers.add(server);
		pattern.setServers(servers);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.SERVER_URL_NOT_SET);
		
		server.setServerURL(getRandomName());
		final URIPatternParameter param = new URIPatternParameter();
		final Set<URIPatternParameter> params = new HashSet<URIPatternParameter>();
		params.add(param);
		pattern.setParams(params);
		response = save(pattern);
		assertResponseCode(response, ResponseCode.PATTERN_URI_PARAM_NAME_REQUIRED);
	}

	@Override
	protected URIPattern newInstance() {
		final URIPattern pattern = new URIPattern();
		pattern.setContentProviderId(cp.getId());
		pattern.setPattern(getRandomName());
		return pattern;
	}

	@Override
	protected URIPatternSearchBean newSearchBean() {
		final URIPatternSearchBean sb = new URIPatternSearchBean();
		return sb;
	}

	@Override
	protected Response save(URIPattern t) {
		return contentProviderServiceClient.saveURIPattern(t);
	}

	@Override
	protected Response delete(URIPattern t) {
		return contentProviderServiceClient.deleteProviderPattern(t.getId());
	}

	@Override
	protected URIPattern get(String key) {
		return contentProviderServiceClient.getURIPattern(key);
	}

	@Override
	public List<URIPattern> find(URIPatternSearchBean searchBean, int from, int size) {
		return contentProviderServiceClient.findUriPatterns(searchBean, from, size);
	}

}
