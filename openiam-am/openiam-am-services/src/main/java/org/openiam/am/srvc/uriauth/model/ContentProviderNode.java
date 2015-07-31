package org.openiam.am.srvc.uriauth.model;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.protocol.UriPatternMatcher;
import org.openiam.am.srvc.dto.AbstractParameter;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.model.URIPatternSearchResult;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.base.Tuple;
import org.springframework.http.HttpMethod;

public class ContentProviderNode {
	
	private static final Log LOG = LogFactory.getLog(ContentProviderNode.class);

	private ContentProvider contentProvider;
	private UriPatternMatcher<URIPattern> patternMatcher = new UriPatternMatcher<>();
	
	public static void validate(final String pattern) throws InvalidPatternException {
		boolean isValid = false;
		if(pattern != null) {
			if(!pattern.contains("*")) {
				isValid = true;
			} else {
				if(pattern.startsWith("*")) {
					if(!pattern.endsWith("*")) {
						isValid = true;
					}
				} else if(pattern.endsWith("*")) {
					isValid = true;
				}
			}
		}
		if(!isValid) {
			throw new InvalidPatternException(String.format("'%s' is an invalid pattern", pattern));
		}
	}
	
	public ContentProviderNode(final ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
		if(contentProvider != null) {
			if(CollectionUtils.isNotEmpty(contentProvider.getPatternSet())) {
				for(final URIPattern pattern : contentProvider.getPatternSet()) {
					try {
						final String patten = pattern.getPattern();
						validate(patten);
						patternMatcher.register(StringUtils.lowerCase(patten), pattern);
					} catch (InvalidPatternException e) {
						LOG.error(String.format("URI Pattern %s for CP %s not valid", pattern, contentProvider), e);
					}
				}
			}
		}
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}
	
	private boolean methodMatchesParams(final URIPatternMethod method, final Map<String, Set<String>> queryMap) {
		boolean matches = true;
		if(CollectionUtils.isNotEmpty(method.getParams())) {
			for(final URIPatternMethodParameter param : method.getParams()) {
				if(!matches(param, queryMap)) {
					matches = false;
					break;
				}
			}
		}
		return matches;
	}

	//TODO:  rewrite this using weights.  It will be less code, and more OOP.  
	//		 A Full iteration of hte set won't hurt performance much
	public URIPatternSearchResult getURIPattern(final URI uri, final HttpMethod method) {
		final URIPatternSearchResult retval = new URIPatternSearchResult();
		String path = uri.getPath();
		
		/* if there is no path, assume you're requesting '/' */
		if(StringUtils.isBlank(path)) {
			path = "/";
		}
		final Map<String, Set<String>> queryMap = getQueryMap(uri);
		if(path != null) {
			boolean patternFound = false;
			URIPatternMethod patternMethod = null;
			
			final URIPattern tempPattern = patternMatcher.lookup(StringUtils.lowerCase(path));
			/* if there are methods defined for this URI pattern, and there is a pattern passed in, do a search on the Methods */
			if(tempPattern != null) {
				retval.setUriPatternFound(true);
				if(method != null && tempPattern.isMethodsDefined()) {
					final TreeSet<URIPatternMethod> treeSet = tempPattern.getMethodTreeSet(method);
					if(CollectionUtils.isNotEmpty(treeSet)) {
						URIPatternMethod lastPatternMatched = null;
						for(final Iterator<URIPatternMethod> it = treeSet.iterator(); it.hasNext();) {
							boolean foundMatch = false;
							URIPatternMethod tempPatternMethod = it.next();
							if(PatternMatchMode.IGNORE.equals(tempPatternMethod.getMatchMode())) {
								foundMatch = true;
							} else if(PatternMatchMode.NO_PARAMS.equals(tempPatternMethod.getMatchMode())) {
								if(queryMap.isEmpty()) {
									foundMatch = true;
								}
							} else if(PatternMatchMode.ANY_PARAMS.equals(tempPatternMethod.getMatchMode())) {
								if(!queryMap.isEmpty()) {
									foundMatch = true;
								}
							/* for methods, the MOST specific match is always the parameters */
							} else if(PatternMatchMode.SPECIFIC_PARAMS.equals(tempPatternMethod.getMatchMode())) {
								if(tempPatternMethod.isHasSimiliarMethodInParentURI()) {
									final List<URIPatternMethod> matched = new LinkedList<URIPatternMethod>();
									while(tempPatternMethod.isHasSimiliarMethodInParentURI()) {
										if(methodMatchesParams(tempPatternMethod, queryMap)) {
											matched.add(tempPatternMethod);
										}
										
										if(!it.hasNext()) {
											break;
										}
										tempPatternMethod = it.next();
									}
									
									if(CollectionUtils.isNotEmpty(matched)) {
										foundMatch = true;
										tempPatternMethod = matched.get(0);
									}
								}
								if(methodMatchesParams(tempPatternMethod, queryMap)) {
									foundMatch = true;
								}
							} else { /* this should never happen.  The match mode is not nullable */
								throw new IllegalArgumentException(String.format("URI Pattern %s has a null matching mode", tempPattern));
							}
							
							if(foundMatch) {
								lastPatternMatched = tempPatternMethod;
							}
						}
						
						if(lastPatternMatched != null) {
							patternFound = true;
							patternMethod = lastPatternMatched;
						}
					}
				} else { /* if there are no methods defined, or the method is unkonwn */
					if(PatternMatchMode.IGNORE.equals(tempPattern.getMatchMode())) {
						patternFound = true;
					} else if(PatternMatchMode.NO_PARAMS.equals(tempPattern.getMatchMode())) {
						if(queryMap.isEmpty()) {
							patternFound = true;
						}
					} else if(PatternMatchMode.SPECIFIC_PARAMS.equals(tempPattern.getMatchMode())) {
						boolean matches = true;
						if(CollectionUtils.isNotEmpty(tempPattern.getParams())) {
							for(final URIPatternParameter param : tempPattern.getParams()) {
								if(!matches(param, queryMap)) {
									matches = false;
									break;
								}
							}
						}
						if(matches) {
							patternFound = true;
						}
					} else if(PatternMatchMode.ANY_PARAMS.equals(tempPattern.getMatchMode())) {
						if(!queryMap.isEmpty()) {
							patternFound = true;
						}
					} else { /* this should never happen.  The match mode is not nullable */
						throw new IllegalArgumentException(String.format("URI Pattern %s has a null matching mode", tempPattern));
					}
				}
				if(patternFound) {
					retval.setPattern(tempPattern);
					retval.setMethod(patternMethod);
				}
			}
		}
		return retval;
	}
	
	private boolean matches(final AbstractParameter param, final Map<String, Set<String>> queryMap) {
		boolean matches = true;
		if(!queryMap.containsKey(param.getName())) {
			matches = false;
		} else {
			if(CollectionUtils.isNotEmpty(param.getValues())) {
				final Set<String> values = queryMap.get(param.getName());
				for(final String value : param.getValues()) {
					if(!values.contains(value)) {
						matches = false;
						break;
					}
				}
			}
		}
		return matches;
	}
	
	private Map<String, Set<String>> getQueryMap(final URI uri) {
		String query = uri.getQuery();
		final Map<String, Set<String>> queryMap = new HashMap<String, Set<String>>();
		if(StringUtils.isNotBlank(query)) {
			query = query.toLowerCase();
			final Set<String> parts = new HashSet<String>();
			if(!query.contains("&")) { /* 1 param */
				if(query.contains("=")) {
					parts.add(query);
				}
			} else {
				for(final String part : query.split("&")) {
					if(part.contains("=")) {
						parts.add(part);
					}
				}
			}
			
			parts.forEach(part -> {
				final String[] split = part.split("=");
				String key = split[0];
				if(key != null) {
					key = StringUtils.trimToNull(key.toLowerCase());
				}
				
				String value = null;
				if(split.length > 1) {
					value = split[1];
					if(value != null) {
						value = StringUtils.trimToNull(value.toLowerCase());
					}
				}
				if(key != null) {
					if(!queryMap.containsKey(key)) {
						queryMap.put(key, new HashSet<String>());
					}
					if(value != null) {
						queryMap.get(key).add(value);
					}
				}
			});
		}
		return queryMap;
	}
}
