package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "URI_PATTERN_META")
@DozerDTOCorrespondence(URIPatternMeta.class)
public class URIPatternMetaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "URI_PATTERN_META_ID", length = 32, nullable = false)
	private String id;
    @Column(name = "URI_PATTERN_NAME", length = 100, nullable = false)
    private String name;
	
	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_ID", referencedColumnName = "URI_PATTERN_ID")
	private URIPatternEntity pattern;

	@ManyToOne(fetch = FetchType.LAZY,cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="URI_PATTERN_META_TYPE_ID", referencedColumnName = "URI_PATTERN_META_TYPE_ID")
	private URIPatternMetaTypeEntity metaType;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "metaEntity")
	private Set<URIPatternMetaValueEntity> metaValueSet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public URIPatternEntity getPattern() {
		return pattern;
	}
	
	public void setPattern(URIPatternEntity pattern) {
		this.pattern = pattern;
	}
	
	public URIPatternMetaTypeEntity getMetaType() {
		return metaType;
	}
	
	public void setMetaType(URIPatternMetaTypeEntity metaType) {
		this.metaType = metaType;
	}

    public Set<URIPatternMetaValueEntity> getMetaValueSet() {
        return metaValueSet;
    }

    public void setMetaValueSet(Set<URIPatternMetaValueEntity> metaValueSet) {
        this.metaValueSet = metaValueSet;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((metaType == null) ? 0 : metaType.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIPatternMetaEntity other = (URIPatternMetaEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metaType == null) {
			if (other.metaType != null)
				return false;
		} else if (!metaType.equals(other.metaType))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}
	
	
}
