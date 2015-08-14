package org.openiam.idm.srvc.msg.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.msg.dto.MailTemplateDto;
import org.openiam.idm.srvc.msg.dto.MessageBodyType;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "MAIL_TEMPLATE")
@DozerDTOCorrespondence(MailTemplateDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "TMPL_ID")),
	@AttributeOverride(name = "name", column = @Column(name = "TMPL_NAME"))
})
public class MailTemplateEntity extends AbstractKeyNameEntity {

    @Column(name = "TMPL_SUBJECT")
    private String subject;

    @Column(name = "BODY_TYPE")
    @Enumerated(EnumType.STRING)
    private MessageBodyType type;

    @Column(name = "BODY")
    private String body;

    @Column(name = "ATTACHMENT_FILE_PATH")
    private String attachmentFilePath;

    public MailTemplateEntity() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MessageBodyType getType() {
        return type;
    }

    public void setType(MessageBodyType type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachmentFilePath() {
        return attachmentFilePath;
    }

    public void setAttachmentFilePath(String attachmentFilePath) {
        this.attachmentFilePath = attachmentFilePath;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((attachmentFilePath == null) ? 0 : attachmentFilePath
						.hashCode());
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailTemplateEntity other = (MailTemplateEntity) obj;
		if (attachmentFilePath == null) {
			if (other.attachmentFilePath != null)
				return false;
		} else if (!attachmentFilePath.equals(other.attachmentFilePath))
			return false;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return type == other.type;
	}

	@Override
	public String toString() {
		return "MailTemplateEntity [subject=" + subject + ", type=" + type
				+ ", body=" + body + ", attachmentFilePath="
				+ attachmentFilePath + "]";
	}

    
}
