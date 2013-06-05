package org.openiam.am.srvc.uriauth.model;

public class URIPatternToken {

	private String token;
	private boolean hasWildCard;
	private boolean isStartsWithWildCard;
	private boolean isEndsWithWildCard;
	private boolean isWildCard;
	private boolean isSeparatedByWildCard;
	
	/* these two strings are for debugging only */
	private String originalToken;
	
	public URIPatternToken(final String uriPattern) {
		originalToken = uriPattern.trim().toLowerCase();
		token = originalToken;
		hasWildCard = token.contains("*");
		if(token.equals("*")) {
			isWildCard = true;
		} else if(hasWildCard) {
			isStartsWithWildCard = token.startsWith("*");
			isEndsWithWildCard = token.endsWith("*");
		}
		
		if(isStartsWithWildCard) {
			token = token.substring(1, token.length());
		}
		
		if(isEndsWithWildCard) {
			token = token.substring(0, token.length() - 1);
		}
		
		if(isStartsWithWildCard && isEndsWithWildCard) {
			throw new IllegalArgumentException("URI Pattern cannot start and end with a wildcard");
		}
	}

	public String getToken() {
		return token;
	}

	public boolean hasWildCard() {
		return hasWildCard;
	}

	public boolean isStartsWithWildCard() {
		return isStartsWithWildCard;
	}

	public boolean isEndsWithWildCard() {
		return isEndsWithWildCard;
	}
	
	public boolean isWildCard() {
		return isWildCard;
	}
	
	public void setSeparatedByWildCard(final boolean isSeparatedByWildCard) {
		this.isSeparatedByWildCard = isSeparatedByWildCard;
	}
	
	public boolean isSeparatedByWildCard() {
		return isSeparatedByWildCard;
	}

	@Override
	public String toString() {
		return String
				.format("URIPatternToken [token=%s, originalToken=%s, hasWildCard=%s, isStartsWithWildCard=%s, isEndsWithWildCard=%s, isWildCard=%s, isSeparatedByWildCard=%s]",
						token, originalToken,  hasWildCard,
						isStartsWithWildCard, isEndsWithWildCard, isWildCard, isSeparatedByWildCard);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasWildCard ? 1231 : 1237);
		result = prime * result + (isEndsWithWildCard ? 1231 : 1237);
		result = prime * result + (isStartsWithWildCard ? 1231 : 1237);
		result = prime * result + (isWildCard ? 1231 : 1237);
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternToken other = (URIPatternToken) obj;
		if (hasWildCard != other.hasWildCard)
			return false;
		if (isEndsWithWildCard != other.isEndsWithWildCard)
			return false;
		if (isStartsWithWildCard != other.isStartsWithWildCard)
			return false;
		if (isWildCard != other.isWildCard)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

}
