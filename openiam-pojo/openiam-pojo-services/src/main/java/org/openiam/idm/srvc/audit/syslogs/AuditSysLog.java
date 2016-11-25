package org.openiam.idm.srvc.audit.syslogs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

@Service("auditSysLogService")
public class AuditSysLog
{
    private static final Log log = LogFactory.getLog(AuditSysLog.class);


    @Value("${org.openiam.syslog.actions}")
    private String logActions;

    @Value("${org.openiam.syslog.ident}")
    private String defIdent;

    @Value("${org.openiam.syslog.logopt}")
    private String defLogopt;

    @Value("${org.openiam.syslog.facility}")
    private String defFacility;

    @Value("${org.openiam.syslog.priority}")
    private String defPriority;

    @Value("${org.openiam.syslog.hostName}")
    private String defHostName;

    @Value("${org.openiam.syslog.port}")
    private String defPort;

    @Value("${org.openiam.syslog.enable}")
    private String defIsEnable;

    @Value("${org.openiam.syslog.message.order}")
    private String messageOrder;

    // Option flags.
    public static final int LOG_PID	= 0x01; // log the pid with each message
    public static final int LOG_CONS	= 0x02; // log on the console if errors
    public static final int LOG_NDELAY	= 0x08; // don't delay open
    public static final int LOG_NOWAIT	= 0x10; // don't wait for console forks

    public static final int LOG_PRIMASK = 0x0007; // mask to extract priority
    public static final int LOG_FACMASK	= 0x03F8; // mask to extract facility

    private String		ident;
    private int			logopt;
    private int			facility;
    private int			priority;

    private Set<String> actions;
    private boolean     isEnable = false;
    private int         port = 514;
    private String[]    dataOrder;

    private InetAddress		address;
    private DatagramPacket	packet;
    private DatagramSocket	socket;

    public AuditSysLog() {
    }

    @PostConstruct
    public void init(){
        actions = new HashSet<String>();
        String[] splitActions = logActions.replaceAll(" ","").split(",");
        if (splitActions.length > 0) {
            for (String act : splitActions) {
                actions.add(AuditAction.valueOf(act).value());
            }
        }

        this.dataOrder = messageOrder.replace(" ","").toLowerCase().split(",");

        if ("true".equalsIgnoreCase(defIsEnable)) {
            isEnable = true;
        }

        if ( defIdent == null ) {
            this.ident = new String(Thread.currentThread().getName());
        } else {
            this.ident = defIdent;
        }
        this.logopt = Integer.parseInt(defLogopt);

        SysLogFacility slf = SysLogFacility.fromLabel(defFacility);
        if (slf == null) {
            slf = SysLogFacility.fromNumericalCode(Integer.getInteger(defFacility));
            if (slf == null) {
                slf = SysLogFacility.LOCAL0;
            }
        }
        this.facility = slf.numericalCode();

        SysLogSeverity sls = SysLogSeverity.fromLabel(defPriority);
        if (sls == null) {
            sls = SysLogSeverity.fromNumericalCode(Integer.getInteger(defPriority));
            if (sls == null) {
                sls = SysLogSeverity.INFORMATIONAL;
            }
        }
        this.priority = sls.numericalCode();

        if (defPort != null) {
            this.port = Integer.parseInt(defPort);
        }

        try
        {
            if (defHostName == null) {
                address = InetAddress.getLocalHost();
            } else {
                address = InetAddress.getByName(defHostName);
            }
        }
        catch ( Exception e )
        {
            log.error("error locating localhost: " + e.getMessage() );
        }

        try
        {
            socket = new DatagramSocket();
        }
        catch ( Exception e )
        {
            log.error("error creating syslog socket: " + e.getMessage() );
        }
    }

     public void sendSysLog(final IdmAuditLog log) {
        StringBuilder logMessage = new StringBuilder();
        if (log != null) {
            for (String st : dataOrder) {
                switch (st) {
                    case "action":
                        logMessage.append(" Action:[").append(log.getAction()).append("]");
                        break;
                    case "clientip":
                        logMessage.append(" ClientIP:[").append(log.getClientIP()).append("]");
                        break;
                    case "principal":
                        logMessage.append(" Principal:[").append(log.getPrincipal()).append("]");
                        break;
                    case "result":
                        logMessage.append(" Result:[").append(log.getResult()).append("]");
                        break;
                    case "target":
                        if (log.getTargets() != null && log.getTargets().size() > 0) {
                            logMessage.append(" Targets:[");
                            for (AuditLogTarget alt : log.getTargets()) {
                                logMessage.append("[").append(alt.getTargetType()).append(":").append(alt.getObjectPrincipal()).append("]");
                            }
                            logMessage.append("]");
                        }
                        break;
                    case "datetime":
                        logMessage.append(" DateTime:[").append(log.getTimestamp().toString()).append("]");
                        break;
                    case "description":
                        if (log.getCustomRecords() != null && log.getCustomRecords().size() > 0) {
                            StringBuilder addInfo = new StringBuilder();
                            for (IdmAuditLogCustom ialc : log.getCustomRecords()) {
                                if ("DESCRIPTION".equalsIgnoreCase(ialc.getKey())) {
                                    addInfo.append("[").append(ialc.getValue()).append("]");
                                }
                            }
                            if (addInfo.length() > 0) {
                                logMessage.append(" Description:[").append(addInfo.toString()).append("]");
                            }

                            addInfo = new StringBuilder();
                            for (IdmAuditLogCustom ialc : log.getCustomRecords()) {
                                if (!"DESCRIPTION".equalsIgnoreCase(ialc.getKey())) {
                                    addInfo.append(ialc.getKey()).append(":[").append(ialc.getValue()).append("]");
                                }
                            }
                            if (addInfo.length() > 0) {
                                logMessage.append(" Additional_info:[").append(addInfo.toString()).append("]");
                            }
                        }
                        break;
                }
            }
        }
        sendSysLog(SysLogSeverity.INFORMATIONAL.numericalCode(), logMessage.toString());
    }

    public void sendSysLog( int priority, String msg ) {
        int		pricode;
        int		length;
        int		idx;
        byte[]	data;
        String	strObj;

        pricode = MakePriorityCode( facility, priority );
        Integer priObj = new Integer( pricode );

        length = 4 + ident.length() + msg.length() + 1;
        length += ( pricode > 99 ) ? 3 : ( ( pricode > 9 ) ? 2 : 1 );

        data = new byte[length];

        idx = 0;
        data[idx++] = '<';

        strObj = priObj.toString( priObj.intValue() );
        System.arraycopy(strObj.getBytes(), 0, data, idx, strObj.length());
        idx += strObj.length();

        data[idx++] = '>';

        System.arraycopy(ident.getBytes(), 0, data, idx, ident.length());
        idx += ident.length();

        data[idx++] = ':';
        data[idx++] = ' ';

        System.arraycopy(msg.getBytes(), 0, data, idx, msg.length());
        idx += msg.length();

        data[idx] = 0;

        packet = new DatagramPacket( data, length, address, port );

        try
        {
            socket.send( packet );
        }
        catch ( IOException e )
        {
            log.error("error sending message: '" + e.getMessage() + "'");
        }
    }

    private int MakePriorityCode( int facility, int priority )
    {
        return ( ( facility & LOG_FACMASK ) | priority );
    }

    public boolean hasAction (String action) {
        return this.actions.contains(action);
    }

    public boolean isEnable () {
        return isEnable;
    }

}