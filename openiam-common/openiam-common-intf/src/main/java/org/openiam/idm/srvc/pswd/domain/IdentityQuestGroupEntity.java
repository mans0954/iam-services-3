package org.openiam.idm.srvc.pswd.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;

@Entity
@Table(name = "IDENTITY_QUEST_GRP")
@DozerDTOCorrespondence(IdentityQuestGroup.class)
public class IdentityQuestGroupEntity {


	@Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "IDENTITY_QUEST_GRP_ID", length = 32)
	private String id;
	
	@Column(name = "NAME", length = 60)
	private String name;
	
	@Column(name = "STATUS", length = 20)
	private String status;
	
	@Column(name = "COMPANY_OWNER_ID", length = 32)
	private String companyOwnerId;
	 
	@Column(name = "CREATE_DATE", length = 19)
	private Date createDate;
	
	@Column(name = "CREATED_BY", length = 20)
	private String createdBy;
	
	@Column(name = "LAST_UPDATE", length = 19)
	private Date lastUpdate;
	
	@Column(name = "LAST_UPDATED_BY", length = 20)
	private String lastUpdatedBy;
	
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "identityQuestGrp")
	private Set<IdentityQuestionEntity> identityQuestions;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompanyOwnerId() {
		return companyOwnerId;
	}

	public void setCompanyOwnerId(String companyOwnerId) {
		this.companyOwnerId = companyOwnerId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Set<IdentityQuestionEntity> getIdentityQuestions() {
		return identityQuestions;
	}

	public void setIdentityQuestions(Set<IdentityQuestionEntity> identityQuestions) {
		this.identityQuestions = identityQuestions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((companyOwnerId == null) ? 0 : companyOwnerId.hashCode());
		result = prime * result
				+ ((createDate == null) ? 0 : createDate.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime
				* result
				+ ((id == null) ? 0 : id
						.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lastUpdatedBy == null) ? 0 : lastUpdatedBy.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		IdentityQuestGroupEntity other = (IdentityQuestGroupEntity) obj;
		if (companyOwnerId == null) {
			if (other.companyOwnerId != null)
				return false;
		} else if (!companyOwnerId.equals(other.companyOwnerId))
			return false;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lastUpdatedBy == null) {
			if (other.lastUpdatedBy != null)
				return false;
		} else if (!lastUpdatedBy.equals(other.lastUpdatedBy))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("IdentityQuestGroupEntity [id=%s, name=%s, status=%s, companyOwnerId=%s, createDate=%s, createdBy=%s, lastUpdate=%s, lastUpdatedBy=%s]",
						id, name, status, companyOwnerId,
						createDate, createdBy, lastUpdate, lastUpdatedBy);
	}
	
	
}
