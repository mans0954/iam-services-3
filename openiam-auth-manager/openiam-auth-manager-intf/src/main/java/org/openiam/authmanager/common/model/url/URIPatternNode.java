package org.openiam.authmanager.common.model.url;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.authmanager.common.model.AuthorizationResource;

@Deprecated
public class URIPatternNode {
	private static final Comparator<URIPatternNode> nodeComparator = new URIPatternNodeComparator();
	
	private int level;
	private URIPatternToken pattern;
	private TreeSet<URIPatternNode> childNodes = new TreeSet<URIPatternNode>(nodeComparator);
	
	private Set<AuthorizationURIPattern> protectedPatterns = null;
	private Set<String> sourceURIs;
	
	public URIPatternNode() {
		
	}
	
	private boolean isLeaf() {
		return CollectionUtils.isEmpty(childNodes);
	}
	
	private boolean isProtected() {
		return CollectionUtils.isNotEmpty(protectedPatterns);
	}
	
	public Set<AuthorizationURIPattern> find(final String URI) {
		final Set<AuthorizationURIPattern> retVal = new HashSet<AuthorizationURIPattern>();
		if(URI != null) {
			final String lowerCaseURI = URI.trim().toLowerCase();
			final String[] slashSeparatedLowercaseURI = StringUtils.split(lowerCaseURI, "/");
			final List<String> patternList = new LinkedList<String>();
			for(final String pattern : slashSeparatedLowercaseURI) {
				patternList.add(pattern);
			}
			find(patternList, retVal);
		}
		return retVal;
	}
	
	private void find(final List<String> patternList, final Set<AuthorizationURIPattern> retVal) {
		if(CollectionUtils.isEmpty(retVal)) {
			if(CollectionUtils.isNotEmpty(patternList)) {
				final String currentPattern = patternList.get(0);
				final String lastPattern = patternList.get(patternList.size() - 1);
				final List<String> adjustedList = patternList.subList(1, patternList.size());
				
				/* check the current Token against the current level nodes */
				if(isLeaf()) {
					if(matches(lastPattern) && isProtected()) {
						retVal.addAll(protectedPatterns);
					}
				} else {
					boolean foundMatch = false;
					Iterator<URIPatternNode> it = childNodes.descendingIterator();
					while(it.hasNext()) {
						URIPatternNode node = it.next();
						if(node.matches(currentPattern)) {
							boolean isMatchWithoutFullURITransversal = false;
							boolean isMatchOnLastURIToken = false;
							boolean isMatchOnNonLeafNode = false;
							
							/* signifies that we've reached the last node in the tree via a full-match */
							isMatchOnLastURIToken = ((adjustedList.size() == 0) && (node.isLeaf()));
							
							if(isMatchOnLastURIToken) {
								node.find(patternList, retVal);
							} else {
								node.find(adjustedList, retVal);
							}
							
							foundMatch = CollectionUtils.isNotEmpty(retVal);
							
							if(!foundMatch) {
								final URIPatternNode containsWildCardMatchNode = node.getContainsWildCardMatchNode(lastPattern);
								if(containsWildCardMatchNode != null && containsWildCardMatchNode.isProtected()) {
									retVal.addAll(containsWildCardMatchNode.protectedPatterns);
									foundMatch = CollectionUtils.isNotEmpty(retVal);;
								}
							}
							
							if(!foundMatch) { 
								
								/*
								 * signifies that, even though we haven't reached the end of the tree, we have a match,
								 * as we've reached the end of the input URL pattern, and the current node is protected by a resource
								 * 
								 */
								isMatchOnNonLeafNode = ((adjustedList.size() == 0) && CollectionUtils.isNotEmpty(node.protectedPatterns) && !node.isLeaf());
								
								/*
								 * Check if the current matching node is protected.  IF it is, you've found the most specific match
								 */
								isMatchWithoutFullURITransversal = (adjustedList.size() > 0 && node.pattern.isEndsWithWildCard() && CollectionUtils.isNotEmpty(node.protectedPatterns)/* && node.isLeaf()*/);
								if(isMatchWithoutFullURITransversal || isMatchOnNonLeafNode) {
									if(node.isProtected()) {
										retVal.addAll(node.protectedPatterns);
										foundMatch = CollectionUtils.isNotEmpty(retVal);;
									}
								}
							}
							
							if(foundMatch) {
								break;
							}
						}
					}
				}
			}
		}
	}
	
	private URIPatternNode getContainsWildCardMatchNode(final String endPattern) {
		URIPatternNode retVal = null;
		if(pattern.isSeparatedByWildCard()) {
			if(CollectionUtils.isNotEmpty(childNodes)) {
				for(final URIPatternNode node : childNodes) {
					if(node.pattern.isSeparatedByWildCard() && node.matches(endPattern) && node.isLeaf()) {
						retVal = node;
						break;
					}
				}
			}
		}
		return retVal;
	}
	
	private boolean matches(final String currentPattern) {
		boolean retVal = false;
		final String token = pattern.getToken();
		if(pattern.isWildCard()) {
			retVal = true;
		} else if(pattern.hasWildCard()) {
			if(pattern.isStartsWithWildCard()) {
				retVal = currentPattern.endsWith(token);
			} else { //pattern.isEndsWithWildCard()
				retVal = currentPattern.startsWith(token);
			}
		} else {
			retVal = token.equals(currentPattern);
		}
		return retVal;
	}
	
	private void addSourceURI(final String source) {
		if(source != null) {
			if(sourceURIs == null) {
				sourceURIs = new HashSet<String>();
			}
			sourceURIs.add(source);
		}
	}
	
	private void addPattern(final AuthorizationURIPattern pattern) {
		if(pattern != null) {
			if(protectedPatterns == null) {
				protectedPatterns = new HashSet<AuthorizationURIPattern>();
			}
			protectedPatterns.add(pattern);
		}
	}
	
	public URIPatternToken getPattern() {
		return pattern;
	}
	
	private URIPatternNode(URIPatternToken uriPattern, final int level, final String sourceURI) {
		this.pattern = uriPattern;
		this.level = level;
		addSourceURI(sourceURI);
	}
	
	public void addURI(final AuthorizationURIPattern pattern) throws InvalidPatternException {
		if(pattern != null && StringUtils.isNotBlank(pattern.getPattern())) {
			final String lowerCaseURI = StringUtils.lowerCase(StringUtils.trimToNull(pattern.getPattern()));
			
			if(StringUtils.countMatches(lowerCaseURI, "*") > 1) {
				throw new InvalidPatternException("More than one Wildcard in uri pattern - current implementation does not support this feature");
			}
			
			final List<URIPatternToken> patternList = new LinkedList<URIPatternToken>();
			final String[] arraySplit = StringUtils.split(lowerCaseURI, "/");
			for(int i = 0; i < arraySplit.length; i++) {
				final String entry = arraySplit[i];
				final URIPatternToken entryToken = new URIPatternToken(entry);
				if(entry.contains("*")) {
					if(i < arraySplit.length - 1) {
						throw new InvalidPatternException("Found a 'contains' wildcard, but not at the end of the pattern.  Current implementaion only supports contains patterns such as '/foo/bar/*.html'.  '/foo/bar/*/wee' is not supported");
					}
					
					final int indexOfStar = entry.indexOf("*");
					if(indexOfStar == 0 || indexOfStar == entry.length() - 1) {
						patternList.add(entryToken);
					} else {
						final URIPatternToken beforeStarToken = new URIPatternToken(entry.substring(0, indexOfStar + 1));
						beforeStarToken.setSeparatedByWildCard(true);
						final URIPatternToken afterStarToken = new URIPatternToken(entry.substring(indexOfStar, entry.length()));
						afterStarToken.setSeparatedByWildCard(true);
						patternList.add(beforeStarToken);
						patternList.add(afterStarToken);
					}
				} else {
					patternList.add(entryToken);
				}
			}
			add(patternList, 0, lowerCaseURI, pattern);
		}
	}
	
	private void add(final List<URIPatternToken> uriPatternList, final int level, final String sourceURI, final AuthorizationURIPattern pattern) {
		if(CollectionUtils.isNotEmpty(uriPatternList)) {
			boolean foundMatchAtCurrentIdx = false;
			final URIPatternToken currentPartToken = uriPatternList.get(0);
			final URIPatternNode node = new URIPatternNode(currentPartToken, level + 1, sourceURI);
			final List<URIPatternToken> adjustedPatterns = uriPatternList.subList(1, uriPatternList.size());
			
			for(final Iterator<URIPatternNode> it = childNodes.iterator(); it.hasNext();) {
				final URIPatternNode slashNode = it.next();
				if(slashNode.pattern.equals(currentPartToken)) {
					if(currentPartToken.isSeparatedByWildCard()) {
						slashNode.pattern.setSeparatedByWildCard(true);
					}
					foundMatchAtCurrentIdx = true;
					slashNode.addSourceURI(sourceURI);
					slashNode.add(adjustedPatterns, level + 1, sourceURI, pattern);
					slashNode.addResourceIfAtEndOfPatternTransversal(adjustedPatterns, pattern);
					break;
				}
			}
			
			if(!foundMatchAtCurrentIdx) {
				childNodes.add(node);
				node.add(adjustedPatterns, level + 1, sourceURI, pattern);
				node.addResourceIfAtEndOfPatternTransversal(adjustedPatterns, pattern);
			}
		}
	}
	
	private void addResourceIfAtEndOfPatternTransversal(final List<URIPatternToken> patternList, final AuthorizationURIPattern pattern) {
		if(CollectionUtils.isEmpty(patternList)) {
			addPattern(pattern);
		}
	}

	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		String tab = "";
		for(int i = 0; i < level; i++) {
			tab += "  ";
		}
		final StringBuilder sb = new StringBuilder(tab).append("Node:").append(ls);
		if(sourceURIs != null) {
			sb.append(tab).append("Source patterns:").append(ls);
			for(final String sourceURI: sourceURIs) {
				sb.append(tab).append(sourceURI).append(ls);
			}
		}
		if(protectedPatterns != null) {
			sb.append(tab).append("Protected patterns:").append(protectedPatterns).append(ls);
		}
		sb.append(tab).append("Pattern:").append(pattern).append(ls);
		sb.append(tab).append("Level:").append(level).append(ls);
		sb.append(ls);
		for(final URIPatternNode node : childNodes) {
			sb.append(tab).append("Slash Node:").append(ls);
			sb.append(node);
		}
		return sb.toString();
	}
}
