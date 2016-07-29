package org.openiam.authmanager.common.model;

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
import org.openiam.authmanager.common.xref.AbstractGroupXref;
import org.openiam.authmanager.common.xref.AbstractOrgXref;
import org.openiam.authmanager.common.xref.AbstractResourceXref;
import org.openiam.authmanager.common.xref.AbstractRoleXref;
import org.openiam.authmanager.common.xref.GroupGroupXref;
import org.openiam.authmanager.common.xref.ResourceGroupXref;
import org.openiam.base.KeyDTO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationGroup", propOrder = {
})
public class AuthorizationGroup extends AbstractAuthorizationEntity implements Serializable {

    private static final Log log = LogFactory.getLog(AuthorizationGroup.class);

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private Set<GroupGroupXref> parentGroups = null;

    @XmlTransient
    private Set<ResourceGroupXref> resources = null;
//

    private BitSet linearGroupBitSet = null;
    private BitSet linearResourceBitSet = null;
    private String typeId;

    public AuthorizationGroup() {

    }

    public AuthorizationGroup(final AuthorizationGroup dto, final int bitIdx) {
        super(dto);
        super.setBitSetIdx(bitIdx);
        this.typeId = dto.getTypeId();
    }

    public void addParentGroup(final GroupGroupXref group) {
        if (parentGroups == null) {
            parentGroups = new HashSet<GroupGroupXref>();
        }
        parentGroups.add(group);
    }

    public void addResource(final ResourceGroupXref resource) {
        if (resources == null) {
            resources = new HashSet<ResourceGroupXref>();
        }
        resources.add(resource);
    }

    public boolean hasResource(final String id) {
        return (resources != null) ? resources.stream().map(e -> e.getResource().getId()).filter(e -> e.equals(id)).findFirst().isPresent() : false;
    }

    public Set<AbstractResourceXref> getResources() {
        Set<AbstractResourceXref> retVal = null;
        if (resources != null) {
            retVal = new HashSet<AbstractResourceXref>(resources);
        }
        return retVal;
    }

    public Set<AbstractGroupXref> visitGroups(final Set<AuthorizationGroup> visitedSet) {
        final Set<AbstractGroupXref> compiledGroupSet = new HashSet<AbstractGroupXref>();
        if (!visitedSet.contains(this)) {
            if (parentGroups != null) {
                visitedSet.add(this);
                for (final AbstractGroupXref xref : parentGroups) {
                    compiledGroupSet.add(xref);
                    compiledGroupSet.addAll(xref.getGroup().visitGroups(visitedSet));
                }
            }
        }
        return compiledGroupSet;
    }

    public Set<AbstractResourceXref> visitResources(final Set<AuthorizationGroup> visitedSet) {
        final Set<AbstractResourceXref> compiledResourceSet = new HashSet<AbstractResourceXref>();
        if (!visitedSet.contains(this)) {
            visitedSet.add(this);
            if (parentGroups != null) {
                for (final AbstractGroupXref xref : parentGroups) {
                    compiledResourceSet.addAll(xref.getGroup().visitResources(visitedSet));
                }
            }

            if (resources != null) {
                for (final AbstractResourceXref xref : resources) {
                    compiledResourceSet.add(xref);
                    compiledResourceSet.addAll(xref.getResource().visitResources(new HashSet<AuthorizationResource>()));
                }
            }
        }
        return compiledResourceSet;
    }

    public AuthorizationGroup shallowCopy() {
        final AuthorizationGroup copy = new AuthorizationGroup();
        super.makeCopy(copy);
        return copy;
    }


    /**
     * Compiles this Group against it's Group and Resource memberships
     */
    public void compile(final int numOfRights) {
        linearGroupBitSet = new BitSet();
        linearResourceBitSet = new BitSet();

        final StopWatch sw = new StopWatch();
        sw.start();
        final StringBuilder sb = (log.isDebugEnabled()) ? new StringBuilder(String.format("Group ID: %s", getId())) : null;

        final StopWatch innerSW = new StopWatch();
        innerSW.start();

        final Set<AbstractGroupXref> compiledGroupSet = visitGroups(new HashSet<AuthorizationGroup>());
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

        final Set<AbstractResourceXref> compiledResourceSet = visitResourcesInternal(compiledGroupSet);
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

    private Set<AbstractResourceXref> visitResourcesInternal(final Set<AbstractGroupXref> compiledGroups) {
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
