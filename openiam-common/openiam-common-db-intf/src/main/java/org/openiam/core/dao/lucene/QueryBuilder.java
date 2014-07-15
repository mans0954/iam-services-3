package org.openiam.core.dao.lucene;

import org.apache.log4j.Logger;

//import org.apache.lucene.analysis.StopAnalyzer;
//import org.apache.lucene.analysis.Token;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.search.BooleanClause;
//import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.PrefixQuery;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.util.Version;

public final class QueryBuilder {

	private static final Logger LOGGER = Logger.getLogger(QueryBuilder.class);

	private QueryBuilder() {
		
	}
//    public static Query buildQuery(final String property, final BooleanClause.Occur occur, final String keyword) {
//        final BooleanQuery query = new BooleanQuery();
//        final String trimmedKeyword = StringUtils.trimToEmpty(keyword.toLowerCase());
//        final Set<String> terms = separateTerms(trimmedKeyword);
//        //when user types 'a' it will be removed from search terms by StandardAnalyzer,
//        //because 'a' is in StopAnalyzer.ENGLISH_STOP_WORDS list
//        //so put it back into terms list
//        if (terms.isEmpty() && StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(trimmedKeyword)) {
//        	terms.add(trimmedKeyword);
//        }
//        for (final Iterator<String> iterator = terms.iterator(); iterator.hasNext();) {
//        	String term = iterator.next();
//        	//allows search by non-empty words
//        	if (StringUtils.isNotEmpty(term)) {
//        		final boolean ignoreWord = StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(term);
//        		query.add(new PrefixQuery(new Term(property, term)),
//        				ignoreWord ? BooleanClause.Occur.SHOULD : occur);
//        	}
//        	iterator.remove();
//        }
//        return query;
//    }
//
//    private static Set<String> separateTerms(final String keyword) {
//    	final Set<String> result = new HashSet<String>();
//    	TokenStream tokenStream = null;
//    	StandardAnalyzer analyzer = null;
//    	try {
//    		analyzer = new StandardAnalyzer(Version.LUCENE_31);
//    		tokenStream = analyzer.tokenStream(null, new StringReader(keyword));
//    		final OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
//    		final CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
//    		while (tokenStream.incrementToken()) {
//    			int startOffset = offsetAttribute.startOffset();
//    			int endOffset = offsetAttribute.endOffset();
//    			final String term = charTermAttribute.toString();
//    			result.add(term);
//    		}
//    	} catch (IOException e) {
//			LOGGER.error(String.format("can't parse '%s'", keyword), e);
//		} finally {
//			try {tokenStream.close();} catch (Throwable e) {}
//			try {analyzer.close();} catch (Throwable e) {}
//		}
//    	return result;
//    }
}