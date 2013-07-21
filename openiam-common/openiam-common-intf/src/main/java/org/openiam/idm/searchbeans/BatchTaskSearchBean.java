package org.openiam.idm.searchbeans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.idm.srvc.batch.dto.BatchTask;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchTaskSearchBean", propOrder = {

})
public class BatchTaskSearchBean extends AbstractSearchBean<BatchTask, String> implements SearchBean<BatchTask, String> {

}
