package org.openiam.idm.srvc.user.dao;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.core.dao.lucene.SortType;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.stereotype.Repository;

@Repository("userSearchDAO")
public class UserSearchDAO extends AbstractHibernateSearchDao<UserEntity, UserSearchBean, String> {
	
	@Override
	protected Query parse(UserSearchBean query) {
		final BooleanQuery luceneQuery = new BooleanQuery();
		Query clause = buildTokenizedClause("firstName", query.getFirstName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildTokenizedClause("lastName", query.getLastName());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("userStatus", query.getUserStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("accountStatus", query.getAccountStatus());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildTokenizedClause("email", query.getEmailAddress());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("areaCd", query.getPhoneAreaCd());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("phoneNbr", query.getPhoneNbr());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildExactClause("organization", query.getOrganizationId());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildPrincipalClause(query.getPrincipal());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildGroupQuery(query.getGroupIdSet());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		clause = buildRoleQuery(query.getRoleIdSet());
		if(clause != null) {
			luceneQuery.add(clause, BooleanClause.Occur.MUST);
		}
		
		return luceneQuery;
	}
	
	private Query buildGroupQuery(final Collection<String> groupIdSet) {
		BooleanQuery paramsQuery = null;
		if(CollectionUtils.isNotEmpty(groupIdSet)) {
			paramsQuery = new BooleanQuery();
			for(final String groupId : groupIdSet) {
				final Query query = buildExactClause("groups.groupId", groupId);
				if(query != null) {
					paramsQuery.add(query, BooleanClause.Occur.SHOULD);
				}
			}
		}
		return paramsQuery;
	}
	
	private Query buildRoleQuery(final Collection<String> roleIdSet) {
		BooleanQuery paramsQuery = null;
		if(CollectionUtils.isNotEmpty(roleIdSet)) {
			paramsQuery = new BooleanQuery();
			for(final String roleId : roleIdSet) {
				final Query query = buildExactClause("roles.roleId", roleId);
				if(query != null) {
					paramsQuery.add(query, BooleanClause.Occur.SHOULD);
				}
			}
		}
		return paramsQuery;
	}
	
	private Query buildPrincipalClause(final String principalName) {
		if(StringUtils.isNotBlank(principalName)) {
			return buildTokenizedClause("principal.login", principalName);
		}
		return null;
    }

	@Override
	protected Class<UserEntity> getEntityClass() {
		return UserEntity.class;
	}
}