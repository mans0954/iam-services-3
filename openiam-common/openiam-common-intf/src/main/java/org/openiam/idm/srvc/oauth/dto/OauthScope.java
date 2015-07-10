package org.openiam.idm.srvc.oauth.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.oauth.domain.OauthScopeEntity;
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
        "resources"
})
@XmlSeeAlso({
        Resource.class,
})
@DozerDTOCorrespondence(OauthScopeEntity.class)
@Internationalized
public class OauthScope extends KeyNameDTO {

    private String name;
    private String description;
    private Set<Resource> resources;

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

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "OauthScope{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", resources=" + resources +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OauthScope)) return false;
        if (!super.equals(o)) return false;

        OauthScope that = (OauthScope) o;

        if (!getName().equals(that.getName())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        return getResources().equals(that.getResources());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getResources().hashCode();
        return result;
    }
}
