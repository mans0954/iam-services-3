package org.openiam.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.openiam.am.srvc.dto.jdbc.AbstractAuthorizationEntity;
import org.openiam.am.srvc.dto.jdbc.AuthorizationAccessRight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexander on 27/08/15.
 */
public abstract class AbstractEntitlementToken<Entity extends AbstractAuthorizationEntity> {

    private Map<Entity, Set<AuthorizationAccessRight>> directEntitlements=new HashMap<Entity, Set<AuthorizationAccessRight>>();
    private Map<Entity, Set<AuthorizationAccessRight>> allEntitlements=new HashMap<Entity, Set<AuthorizationAccessRight>>();
    private Map<Entity, Set<AuthorizationAccessRight>> indirectEntitlements=new HashMap<Entity, Set<AuthorizationAccessRight>>();


    public Map<Entity, Set<AuthorizationAccessRight>> getDirectEntitlements() {
        return directEntitlements;
    }
    public void setDirectEntitlements(
            Map<Entity, Set<AuthorizationAccessRight>> directEntitlements) {
        this.directEntitlements = directEntitlements;
    }
    public Map<Entity, Set<AuthorizationAccessRight>> getAllEntitlements() {
        return allEntitlements;
    }
    public void setAllEntitlements(
            Map<Entity, Set<AuthorizationAccessRight>> allEntitlements) {
        this.allEntitlements = allEntitlements;
    }

    public void addDirectEntitlement(final Entity entity, final Set<AuthorizationAccessRight> rights) {
        add(entity, rights, directEntitlements);
    }

    public void addEntitlement(final Entity entity, final Set<AuthorizationAccessRight> rights) {
        add(entity, rights, allEntitlements);
    }

    public void addIndirectEntitlement(final Entity entity, final Set<AuthorizationAccessRight> rights) {
        add(entity, rights, indirectEntitlements);
    }


    public Map<String, Entity> getEntitlementMap(){
        Map<String, Entity> retVal = new HashMap<>();
        this.allEntitlements.forEach(((entity, authorizationAccessRights) -> {
            retVal.put(entity.getId(), entity);
        }));
        return retVal;
    }

    public Map<String, Set<String>> getDirectEntitlementIds(){
        return getIds(this.directEntitlements);
    }
    public Map<String, Set<String>> getIndirectEntitlementIds(){
        return getIds(this.indirectEntitlements);
    }
    public Map<String, Set<String>> getEntitlementIds(){
        return getIds(this.allEntitlements);
    }

    public boolean isDirect(final String id) {
        return (directEntitlements != null) ? directEntitlements.keySet().stream().map(e -> e.getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
    }

    public boolean isIndirect(final String id) {
        return (indirectEntitlements != null) ? indirectEntitlements.keySet().stream().map(e -> e.getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
    }
    public Map<Entity, Set<AuthorizationAccessRight>> getIndirectEntitlements() {
        return indirectEntitlements;
    }
    public void setIndirectEntitlements(
            Map<Entity, Set<AuthorizationAccessRight>> indirectEntitlements) {
        this.indirectEntitlements = indirectEntitlements;
    }

    private void add(final Entity entity, Set<AuthorizationAccessRight> rights, Map<Entity, Set<AuthorizationAccessRight>> target){
        if(entity != null) {
            if(!target.containsKey(entity)) {
                target.put(entity, new HashSet<AuthorizationAccessRight>());
            }
            if(rights != null) {
                target.get(entity).addAll(rights);
            }
        }
    }

    private Map<String, Set<String>> getIds(Map<Entity, Set<AuthorizationAccessRight>> entitlements){
        Map<String, Set<String>> retVal = new HashMap<>();
        if(MapUtils.isNotEmpty(entitlements)){
            entitlements.forEach(((entity, authorizationAccessRights) -> {
                retVal.put(entity.getId(), new HashSet<>());

                if(CollectionUtils.isNotEmpty(authorizationAccessRights)){
                    authorizationAccessRights.forEach(right ->{
                        retVal.get(entity.getId()).add(right.getId());
                    });
                }
            }));
        }
        return retVal;
    }
}
