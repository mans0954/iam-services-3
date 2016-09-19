package org.openiam.am.srvc.dto.jdbc;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractResourceXref;
import org.openiam.am.srvc.dto.jdbc.xref.AbstractRoleXref;
import org.openiam.am.srvc.dto.jdbc.xref.ResourceRoleXref;
import org.openiam.am.srvc.dto.jdbc.xref.RoleGroupXref;
import org.openiam.am.srvc.dto.jdbc.xref.RoleRoleXref;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationRole", propOrder = {
})
public class AuthorizationRole extends AbstractAuthorizationEntity {

    private static final Log log = LogFactory.getLog(AuthorizationRole.class);

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private Set<RoleRoleXref> parentRoles;

    @XmlTransient
    private Set<ResourceRoleXref> resources;

    @XmlTransient
    private Set<RoleGroupXref> groups = null;

    private BitSet linearGroupBitSet = null;
    private BitSet linearResourceBitSet = null;
    private BitSet linearRoleBitSet = null;

    private String typeId;

    public AuthorizationRole() {

    }

    public AuthorizationRole(final AuthorizationRole entity, final int bitIdx) {
        super(entity);
        super.setBitSetIdx(bitIdx);
        this.typeId = entity.getTypeId();
    }

    public void addParentRole(final RoleRoleXref role) {
        if (parentRoles == null) {
            parentRoles = new HashSet<RoleRoleXref>();
        }
        parentRoles.add(role);
    }

    public Set<AbstractResourceXref> getResources() {
        Set<AbstractResourceXref> retVal = null;
        if (resources != null) {
            retVal = new HashSet<AbstractResourceXref>(resources);
        }
        return retVal;
    }

    public Set<AbstractGroupXref> getGroups() {
        Set<AbstractGroupXref> retVal = null;
        if (groups != null) {
            retVal = new HashSet<AbstractGroupXref>(groups);
        }
        return retVal;
    }

    public void addGroup(final RoleGroupXref group) {
        if (groups == null) {
            groups = new HashSet<RoleGroupXref>();
        }
        groups.add(group);
    }


    public void addResource(final ResourceRoleXref resource) {
        if (resources == null) {
            resources = new HashSet<ResourceRoleXref>();
        }
        resources.add(resource);
    }

    public boolean hasResource(final String id) {
        return (resources != null) ? resources.stream().map(e -> e.getResource().getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
    }

    public Set<AbstractRoleXref> visitRoles(final Set<AuthorizationRole> visitedSet) {
        final Set<AbstractRoleXref> compiledRoles = new HashSet<AbstractRoleXref>();
        if (!visitedSet.contains(this)) {
            visitedSet.add(this);
            if (parentRoles != null) {
                for (final AbstractRoleXref xref : parentRoles) {
                    compiledRoles.add(xref);
                    compiledRoles.addAll(xref.getRole().visitRoles(visitedSet));
                }
            }
        }
        return compiledRoles;
    }


    public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationRole> visitedSet) {
        final Set<AbstractGroupXref> compiledGroupSet = new HashSet<AbstractGroupXref>();
        if (!visitedSet.contains(this)) {
            visitedSet.add(this);
            if (parentRoles != null) {
                for (final AbstractRoleXref xref : parentRoles) {
                    compiledGroupSet.addAll(xref.getRole().visitGroups(visitedSet));
                }
            }

            if (groups != null) {
                for (final AbstractGroupXref xref : groups) {
                    compiledGroupSet.add(xref);
                    compiledGroupSet.addAll(xref.getGroup().visitGroups(new HashSet<AuthorizationGroup>()));
                }
            }
        }
        return compiledGroupSet;
    }


    public Set<AbstractResourceXref> visitResources(final Set<AuthorizationRole> visitedRoles) {
        final Set<AbstractResourceXref> compiledResources = new HashSet<AbstractResourceXref>();
        if (!visitedRoles.contains(this)) {
            visitedRoles.add(this);

            if (parentRoles != null) {
                for (final AbstractRoleXref xref : parentRoles) {
                    compiledResources.addAll(xref.getRole().visitResources(visitedRoles));
                }
            }

            if (groups != null) {
                for (final AbstractGroupXref xref : groups) {
                    compiledResources.addAll(xref.getGroup().visitResources(new HashSet<AuthorizationGroup>()));
                }
            }

            if (resources != null) {
                for (final AbstractResourceXref xref : resources) {
                    compiledResources.add(xref);
                    compiledResources.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
                }
            }
        }
        return compiledResources;
    }

    public AuthorizationRole shallowCopy() {
        final AuthorizationRole copy = new AuthorizationRole();
        super.makeCopy(copy);
        return copy;
    }

    private Set<AbstractResourceXref> visitResources(final Set<AbstractRoleXref> compiledRoleSet, final Set<AbstractGroupXref> compiledGroups) {
        final Set<AbstractResourceXref> tempCompiledSet = new HashSet<AbstractResourceXref>();
        if (resources != null) {
            tempCompiledSet.addAll(resources);
        }

        if (CollectionUtils.isNotEmpty(compiledGroups)) {
            for (final AbstractGroupXref xref : compiledGroups) {
                final Set<AbstractResourceXref> resources = xref.getGroup().getResources();
                if (resources != null) {
                    tempCompiledSet.addAll(resources);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(compiledRoleSet)) {
            compiledRoleSet.forEach(xref -> {
                final Set<AbstractResourceXref> resources = xref.getRole().getResources();
                if (resources != null) {
                    tempCompiledSet.addAll(resources);
                }
            });
        }

        final Set<AbstractResourceXref> compiledSet = new HashSet<AbstractResourceXref>();
        final Set<AuthorizationResource> visitedSet = new HashSet<AuthorizationResource>();
        for (final AbstractResourceXref xref : tempCompiledSet) {
            final Set<AbstractResourceXref> justVisited = xref.getResource().visitResources(visitedSet);
            compiledSet.addAll(justVisited);
            visitedSet.addAll(justVisited.stream().map(e -> e.getResource()).collect(Collectors.toSet()));
        }
        compiledSet.addAll(tempCompiledSet);
        return compiledSet;
    }

    private Set<AbstractGroupXref> visitGroupsInternal(final Set<AbstractRoleXref> compiledRoles) {
        final Set<AbstractGroupXref> tempCompiledSet = new HashSet<AbstractGroupXref>();
        if (groups != null) {
            tempCompiledSet.addAll(groups);
        }


        for (final AbstractRoleXref xref : compiledRoles) {
            final Set<AbstractGroupXref> groups = xref.getRole().getGroups();
            if (groups != null) {
                tempCompiledSet.addAll(groups);
            }
        }

        final Set<AuthorizationGroup> visitedSet = new HashSet<AuthorizationGroup>();
        final Set<AbstractGroupXref> compiledSet = new HashSet<AbstractGroupXref>();
        for (final AbstractGroupXref xref : tempCompiledSet) {
            final Set<AbstractGroupXref> justVisited = xref.getGroup().visitGroups(visitedSet);
            compiledSet.addAll(justVisited);
            visitedSet.addAll(justVisited.stream().map(e -> e.getGroup()).collect(Collectors.toSet()));
        }
        compiledSet.addAll(tempCompiledSet);

        return compiledSet;
    }

    /**
     * Compiles this Group against it's Group and Resource memberships
     */
    public void compile(final int numOfRights) {
        linearGroupBitSet = new BitSet();
        linearResourceBitSet = new BitSet();
        linearRoleBitSet = new BitSet();

        final StopWatch sw = new StopWatch();
        sw.start();
        final StringBuilder sb = (log.isDebugEnabled()) ? new StringBuilder(String.format("Group ID: %s", getId())) : null;

        final StopWatch innerSW = new StopWatch();
        innerSW.start();

        final Set<AbstractRoleXref> compiledRoleSet = visitRoles(new HashSet<AuthorizationRole>());
        for (final AbstractRoleXref xref : compiledRoleSet) {
            if (CollectionUtils.isNotEmpty(xref.getRights())) {
                xref.getRights().forEach(right -> {
                    linearRoleBitSet.set(getBitIndex(right, xref.getRole(), numOfRights));
                });
            }
            linearRoleBitSet.set(getBitIndex(null, xref.getRole(), numOfRights));
        }

        final Set<AbstractGroupXref> compiledGroupSet = visitGroupsInternal(compiledRoleSet);
        for (final AbstractGroupXref xref : compiledGroupSet) {
            if (CollectionUtils.isNotEmpty(xref.getRights())) {
                xref.getRights().forEach(right -> {
                    linearGroupBitSet.set(getBitIndex(right, xref.getGroup(), numOfRights));
                });
            }
            linearGroupBitSet.set(getBitIndex(null, xref.getGroup(), numOfRights));
        }
        innerSW.stop();
        if (log.isDebugEnabled()) {
            sb.append(String.format("Compiled Groups: %s.  ", innerSW.getTime()));
        }
        innerSW.reset();
        innerSW.start();

        final Set<AbstractResourceXref> compiledResourceSet = visitResources(compiledRoleSet, compiledGroupSet);
        for (final AbstractResourceXref xref : compiledResourceSet) {
            if (CollectionUtils.isNotEmpty(xref.getRights())) {
                xref.getRights().forEach(right -> {
                    linearResourceBitSet.set(getBitIndex(right, xref.getResource(), numOfRights));
                });
            }
            linearResourceBitSet.set(getBitIndex(null, xref.getResource(), numOfRights));
        }
        innerSW.stop();
        if (log.isDebugEnabled()) {
            sb.append(String.format("Compiled Resources: %s.  ", innerSW.getTime()));
        }
        sw.stop();
    }

    public List<Integer> getLinearResources() {
        final List<Integer> linearBitSet = new LinkedList<Integer>();
        for (int i = 0; i < linearResourceBitSet.size(); i++) {
            if (linearResourceBitSet.get(i)) {
                linearBitSet.add(new Integer(i));
            }
        }
        return linearBitSet;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
