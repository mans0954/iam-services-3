package org.openiam.idm.srvc.batch.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Service for accessing and managing batch oriented tasks.
 *
 * @author suneet shah
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/batch/service", name = "BatchDataWebService")
public interface BatchDataService {

	@WebMethod
	public List<BatchTask> findBeans(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskSearchBean searchBean,
									 @WebParam(name = "from", targetNamespace = "") int from,
									 @WebParam(name = "size", targetNamespace = "") int size);
	
	

	@WebMethod
	public int count(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskSearchBean searchBean);

	
    @WebMethod
    public Response save(
            @WebParam(name = "task", targetNamespace = "")
            BatchTask task);

    @WebMethod
    public BatchTask getBatchTask(
            @WebParam(name = "taskId", targetNamespace = "")
            String taskId);

    public Response removeBatchTask(
            @WebParam(name = "taskID", targetNamespace = "")
            String taskId);
    
    public Response run(@WebParam(name = "taskId", targetNamespace = "") String id, @WebParam(name = "synchronous", targetNamespace = "") boolean synchronous);
}
