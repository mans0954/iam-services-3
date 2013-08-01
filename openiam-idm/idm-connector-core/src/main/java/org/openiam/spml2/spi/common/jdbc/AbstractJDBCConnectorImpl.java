package org.openiam.spml2.spi.common.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.*;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.base.AbstractSpml2Complete;
import org.openiam.spml2.spi.common.*;
import org.springframework.beans.factory.annotation.Required;

import javax.jws.WebParam;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractJDBCConnectorImpl extends AbstractSpml2Complete  {

    private static final Log log = LogFactory.getLog(AbstractJDBCConnectorImpl.class);

    /* these have Spring setters (not annotations) to allow the caller to provide a unique implementation of the commands */
    private JDBCConnectionMgr connectionMgr;
    private AddCommand addCommand;
    private DeleteCommand deleteCommand;
    private LookupCommand lookupCommand;
    private ModifyCommand modifyCommand;
    private ResumeCommand resumeCommand;
    private PasswordCommand setPasswordCommand;
    private SuspendCommand suspendCommand;

    public ResponseType reconcileResource(@WebParam(name = "config", targetNamespace = "") ReconciliationConfig config) {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.FAILURE);
        response.setError(ErrorCode.UNSUPPORTED_OPERATION);
        return response;
    }

    public ResponseType testConnection(ManagedSysDto managedSys) {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        Connection con = null;

        try {
            con = connectionMgr.connect(managedSys);
        } catch (SQLException se) {
            log.error(se);
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.SQL_ERROR);
            response.addErrorMessage(se.toString());

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.INVALID_CONFIGURATION);
            response.addErrorMessage(cnfe.toString());
        } catch(Throwable e) {
            log.error(e);
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.OTHER_ERROR);
            response.addErrorMessage(e.toString());
        } finally {
            /* close the connection to the directory */
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception n) {
                log.error(n);
            }

        }

        return response;
    }

    public ObjectResponse add(CrudRequest reqType) {
        return addCommand.add(reqType);
    }

    public ObjectResponse modify(CrudRequest reqType) {
        return modifyCommand.modify(reqType);
    }

    public ObjectResponse delete(CrudRequest reqType) {

        return deleteCommand.delete(reqType);
    }

    public SearchResponse lookup(LookupRequest reqType) {
        return lookupCommand.lookup(reqType);
    }

    public ResponseType setPassword( PasswordRequest request) {
        return setPasswordCommand.setPassword(request);
    }

    public ResponseType expirePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType resetPassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType validatePassword(@WebParam(name = "request", targetNamespace = "") PasswordRequest request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ResponseType suspend(final SuspendRequest request) {
        return suspendCommand.suspend(request);
    }

    public ResponseType resume(final SuspendResumeRequest request) {
        return resumeCommand.resume(request);
    }

    @Required
    public void setConnectionMgr(final JDBCConnectionMgr connectionMgr) {
        this.connectionMgr = connectionMgr;
    }

    @Required
    public void setAddCommand(final AddCommand addCommand) {
        this.addCommand = addCommand;
    }

    @Required
    public void setDeleteCommand(final DeleteCommand deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    @Required
    public void setLookupCommand(final LookupCommand lookupCommand) {
        this.lookupCommand = lookupCommand;
    }

    @Required
    public void setModifyCommand(final ModifyCommand modifyCommand) {
        this.modifyCommand = modifyCommand;
    }

    @Required
    public void setResumeCommand(final ResumeCommand resumeCommand) {
        this.resumeCommand = resumeCommand;
    }

    @Required
    public void setSetPasswordCommand(final PasswordCommand setPasswordCommand) {
        this.setPasswordCommand = setPasswordCommand;
    }

    @Required
    public void setSuspendCommand(final SuspendCommand suspendCommand) {
        this.suspendCommand = suspendCommand;
    }
}
