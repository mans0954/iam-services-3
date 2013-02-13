package org.openiam.authmanager.common.model.url;

import java.util.Comparator;

@Deprecated
public class URIPatternNodeComparator implements Comparator<URIPatternNode> {

	/**
	 * return if(node1 < node2) -1; if(node1 == node2) 0; if (node1 > node2) 1;
	 */
	@Override
	public int compare(URIPatternNode node1, URIPatternNode node2) {
		final URIPatternToken token1 = node1.getPattern();
		final URIPatternToken token2 = node2.getPattern();
		if(token1.equals(token2)) {
			return 0;
			
		/* if the patterns are equal, check the wildcards */
		} else if(token1.getToken().equals(token2.getToken())) {
			
			/* they are both wildcards.  Note that at this point, a token can be EITEHR a startsWithToken OR endsWithTokne, NOT both */
			if(token1.hasWildCard() && token2.hasWildCard()) {
				/* at this points, they are the same pattern, but the wildcards are in different spots.  No other option is posible */
				if(token1.isStartsWithWildCard() && token2.isEndsWithWildCard()) {
					return -1;
				} else if(token1.isEndsWithWildCard() && token2.isStartsWithWildCard()) {
					return 1;
				} else {
					throw new IllegalArgumentException(String.format("Token1: %s, Token2: %s => unknown equality", token1, token2));
				}
			/* token1 is not a wildcard, but token2 is - token1 > token2 */
			} else if(!token1.hasWildCard() && token2.hasWildCard()) {
				return 1;
				
			/* token2 is not a wildcard, but token1 is - token2 > token1 */
			} else if(token1.hasWildCard() && !token2.hasWildCard()) {
				return -1;
			} else {
				throw new IllegalArgumentException(String.format("Token1: %s, Token2: %s => unknown equality", token1, token2));
			}
		} else if(token1.getToken().contains(token2.getToken())) {
			/* they are both wildcards, but token1 is more specific (longer).  Token1 > Token2 */
			if(token1.hasWildCard() && token2.hasWildCard()) {
				return 1;
			/* token 2 is more specific.  token2 > token1 */
			} else if(token1.hasWildCard() && !token2.hasWildCard()) {
				return -1;
			/* token 1 is more specific.  token1 > token2 */
			} else if(!token1.hasWildCard() && token2.hasWildCard()) {
				return 1;
			} else { /* token1 is longer (more specific) than token2.  token1 > token 2 */
				return 1;
			}
		} else if(token2.getToken().contains(token1.getToken())) {
			if(token1.hasWildCard() && token2.hasWildCard()) { /* they are both wildcards, but token2 is more specific (longer).  Token2 > Token1 */
				return -1;
			} else if(token1.hasWildCard() && !token2.hasWildCard()) { /* token 2 is more specific.  token2 > token1 */
				return -1;
			} else if(!token1.hasWildCard() && token2.hasWildCard()) { /* token 1 is more specific.  token1 > token2 */
				return 1;
			} else { /* token2 is longer (more specific) than token1.  token2 > token 1 */
				return -1;
			}
		} else { /* the patterns have nothing to do with each other */
			if(token1.hasWildCard() && token2.hasWildCard()) {
				if(token1.isWildCard()) {
					return -1;
				} else if (token2.isWildCard()) {
					return 1;
				} else { /* they both have wildcards, but are not pure wildcards */
					if(token1.isEndsWithWildCard() && token2.isStartsWithWildCard()) {
						return 1;
					} else if(token1.isStartsWithWildCard() && token2.isEndsWithWildCard()) {
						return -1;
					} else {
						return token1.getToken().compareTo(token2.getToken());
					}
				}
			} else if(token1.hasWildCard() && !token2.hasWildCard()) { /* token2 is more specific */
				return -1;
			} else if(!token1.hasWildCard() && token2.hasWildCard()) { /* token1 is more specific */
				return 1;
			} else { /* neither is a wild card - just compare their lengths. */
				if(token1.getToken().length() > token2.getToken().length()) {
					return 1;
				} else if(token2.getToken().length() > token1.getToken().length()) {
					return -1;
				} else { /* equal lengths, but different Strings - DO NOT RETURN 0!!!! */
					return token1.getToken().compareTo(token2.getToken());
				}
			}
		}
	}
}
