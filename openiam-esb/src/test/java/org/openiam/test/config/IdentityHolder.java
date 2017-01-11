package org.openiam.test.config;

public class IdentityHolder {
	private static ThreadLocal<IdentityHolder> tLocal = new ThreadLocal<>();
	private String userId;
	private String principal;
	
	public IdentityHolder() {}
	
	public static IdentityHolder getInstance() {
		IdentityHolder instance = tLocal.get();
        if (instance == null) {
            instance = new IdentityHolder();
            tLocal.set(instance);
        }
        return instance;
    }
	
	public static void remove(){
        tLocal.remove();
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	
	
}
