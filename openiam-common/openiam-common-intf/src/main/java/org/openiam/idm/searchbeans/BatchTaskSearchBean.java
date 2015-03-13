package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.batch.dto.BatchTask;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchTaskSearchBean", propOrder = {

})
public class BatchTaskSearchBean extends AbstractSearchBean<BatchTask, String>
        implements SearchBean<BatchTask, String> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCacheUniqueBeanKey() {
        return new StringBuilder()
                .append(name != null ? name : "")
                .append(getKey() != null ? getKey() : "")
                .toString();
    }
}
