package org.openiam.mq.constants;

/**
 * Created by alexander on 25/08/16.
 */
public enum ActivitiAPI implements OpenIAMAPI  {
    InitiateNewHireRequest,
    ClaimRequest,
    InitiateEditUserWorkflow,
    InitiateWorkflow,
    ProcessInstanceIdByExecutionId,
    MakeDecision,
    GetTask,
    TaskFromHistory,
    NumOfAssignedTasksWithFilter,
    NumOfCandidateTasksWithFilter,
    TasksForCandidateUserWithFilter,
    TasksForAssignedUserWithFilter,
    HistoryForInstance,
    GetHistory,
    Count,
    DeleteTask,
    UnclaimTask,
    GetTasksForUser,
    DeleteTasksForUser,
    FindTasks,
    CountTasks
}
