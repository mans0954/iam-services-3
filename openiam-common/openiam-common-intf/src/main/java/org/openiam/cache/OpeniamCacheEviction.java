package org.openiam.cache;

import org.springframework.cache.interceptor.CacheOperation;

public class OpeniamCacheEviction extends CacheOperation {

	protected OpeniamCacheEviction(OpeniamCacheEviction.Builder b) {
		super(b);
		// TODO Auto-generated constructor stub
	}

	
	public static class Builder extends CacheOperation.Builder {

		@Override
		protected StringBuilder getOperationDescription() {
			return super.getOperationDescription();
		}

		public OpeniamCacheEviction build() {
			return new OpeniamCacheEviction(this);
		}
	}
}
