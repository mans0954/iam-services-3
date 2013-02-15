package org.openiam.am.srvc.domain;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.dozer.DozerDTOCorrespondence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AUTH_LEVEL")
@DozerDTOCorrespondence(AuthLevel.class)
public class AuthLevelEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "AUTH_LEVEL_ID", length = 32, nullable = false)
	private String id;
	
	@Column(name="AUTH_LEVEL_NAME", length = 100, nullable = false)
	private String name;
	
	@Column(name="LEVEL", nullable = false)
	private int level;

//    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "minAuthLevel")
//    private Set<ContentProviderEntity> contentProviderSet;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

//    public Set<ContentProviderEntity> getContentProviderSet() {
//        return contentProviderSet;
//    }
//
//    public void setContentProviderSet(Set<ContentProviderEntity> contentProviderSet) {
//        this.contentProviderSet = contentProviderSet;
//    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + level;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AuthLevelEntity other = (AuthLevelEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (level != other.level)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("AuthLevelEntity [id=%s, name=%s, level=%s]", id,
				name, level);
	}
	
	
}
