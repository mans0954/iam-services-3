package org.openiam.srvc.batch;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.Date;
import java.util.List;

/**
 * Service for accessing and managing batch oriented tasks.
 *
 * @author suneet shah
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/batch/service", name = "BatchDataWebService")
public interface BatchDataService {

	@WebMethod
    List<BatchTask> findBeans(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskSearchBean searchBean,
                              @WebParam(name = "from", targetNamespace = "") int from,
                              @WebParam(name = "size", targetNamespace = "") int size);
	
	@WebMethod
    int getNumOfSchedulesForTask(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskScheduleSearchBean searchBean);
	
	@WebMethod
    List<BatchTaskSchedule> getSchedulesForTask(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskScheduleSearchBean searchBean,
                                                @WebParam(name = "from", targetNamespace = "") int from,
                                                @WebParam(name = "size", targetNamespace = "") int size);
	

	@WebMethod
    int count(@WebParam(name = "searchBean", targetNamespace = "") BatchTaskSearchBean searchBean);

	
    @WebMethod
    Response save(
            @WebParam(name = "task", targetNamespace = "")
            BatchTask task);

    @WebMethod
    BatchTask getBatchTask(
            @WebParam(name = "taskId", targetNamespace = "")
            String taskId);

    Response removeBatchTask(
            @WebParam(name = "taskID", targetNamespace = "")
            String taskId);
    
    Response run(@WebParam(name = "taskId", targetNamespace = "") String id, @WebParam(name = "synchronous", targetNamespace = "") boolean synchronous);
    
    Response schedule(@WebParam(name = "taskId", targetNamespace = "") String id, @WebParam(name = "date", targetNamespace = "") Date when);
    
    Response deleteScheduledTask(@WebParam(name = "taskId", targetNamespace = "") String id);
}
