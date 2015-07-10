package org.openiam.am.srvc.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.am.srvc.domain.OauthScopeEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.internationalization.Internationalized;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OauthScope", propOrder = {
        "name",
        "description",
        "resourceId"
})
@XmlSeeAlso({
        Resource.class,
})
@DozerDTOCorrespondence(OauthScopeEntity.class)
@Internationalized
public class OauthScope extends KeyNameDTO {

    private String name;
    private String description;
    private Set<String> resourceId;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getResourceId() {
        return resourceId;
    }

    public void setResourceId(Set<String> resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OauthScope)) return false;
        if (!super.equals(o)) return false;

        OauthScope that = (OauthScope) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return !(getResourceId() != null ? !getResourceId().equals(that.getResourceId()) : that.getResourceId() != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getResourceId() != null ? getResourceId().hashCode() : 0);
        return result;
    }
}
