package org.openiam.idm.srvc.batch.service;

import org.openiam.base.ws.Response;
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

    /**
     * Returns a list of all batch tasks in the system
     *
     * @param cat
     */
    @WebMethod
    public List<BatchTask> getAllTasks();

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
}
