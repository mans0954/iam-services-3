package org.openiam.idm.srvc.synch.service;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.dto.SynchReview;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public abstract class AbstractTransformScript implements TransformScript {

	protected User user;
	protected List<Login> principalList;
	protected List<Role> userRoleList;
	protected boolean isNewUser = false;
    protected ApplicationContext context;
    protected String synchConfigId;
    protected SynchConfig config;
    protected SynchReview review;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Login> getPrincipalList() {
		return principalList;
	}
	public void setPrincipalList(List<Login> principalList) {
		this.principalList = principalList;
	}
	public List<Role> getUserRoleList() {
		return userRoleList;
	}
	public void setUserRoleList(List<Role> userRoleList) {
		this.userRoleList = userRoleList;
	}
	public boolean isNewUser() {
		return isNewUser;
	}
	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

    public SynchConfig getConfig() {
        return config;
    }

    public void setConfig(SynchConfig config) {
        this.config = config;
    }

    public SynchReview getReview() {
        return review;
    }

    public void setReview(SynchReview review) {
        this.review = review;
    }

    @Override
    public void setSynchConfigId(String synchConfigId) {
        this.synchConfigId = synchConfigId;
    }
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
