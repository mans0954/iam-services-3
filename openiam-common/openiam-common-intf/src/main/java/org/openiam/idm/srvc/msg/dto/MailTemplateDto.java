package org.openiam.idm.srvc.msg.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailTemplateDto", propOrder = {
        "subject",
        "type",
        "body",
        "attachmentFilePath"
})

@DozerDTOCorrespondence(MailTemplateEntity.class)
public class MailTemplateDto extends KeyNameDTO {

    private static final long serialVersionUID = -406594689219258805L;

    private String subject;
    private MessageBodyType type;

    private String body;
    private String attachmentFilePath;

    public MailTemplateDto() {
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
		MailTemplateDto other = (MailTemplateDto) obj;
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
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MailTemplateDto [subject=" + subject + ", type=" + type
				+ ", body=" + body + ", attachmentFilePath="
				+ attachmentFilePath + ", getName()=" + getName()
				+ ", getId()=" + getId() + "]";
	}

    
}
