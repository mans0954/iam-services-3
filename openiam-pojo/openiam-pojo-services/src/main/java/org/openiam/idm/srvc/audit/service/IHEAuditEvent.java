package org.openiam.idm.srvc.audit.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.dto.AuditLogTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.IdmAuditLogCustom;
import org.openiam.idm.srvc.audit.service.ExportAuditEvent;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.security.KeyStore;
import java.io.File;
import java.io.FileInputStream;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * Implementation to export IHE (Healthcare) Audit Events
 * User: suneetshah
 * Date: 9/18/11
 * Time: 10:47 PM
 */


@Service("iheAuditEvent")
public class IHEAuditEvent implements ExportAuditEvent {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private GroupDataService groupDataService;

    @Autowired
    private RoleDataService roleDataService;

    @Value("${ATNA_EXCLUDE_PRINCIPAL}")
    private String ATNA_EXCLUDE_PRINCIPAL;

    @Value("${ATNA_KEYSTORE_PATH}")
    private String ATNA_KEYSTORE_PATH;

    @Value("${ATNA_STORE_PASSWORD}")
    private String ATNA_STORE_PASSWORD;

    @Value("${ATNA_CLIENT_PASSWORD}")
    private String ATNA_CLIENT_PASSWORD;

    @Value("${ATNA_HOST}")
    private String ATNA_HOST;

    @Value("${ATNA_PORT}")
    private String ATNA_PORT;


//    static protected ResourceBundle res = ResourceBundle.getBundle("securityconf");

    private static final Log l = LogFactory.getLog(IHEAuditEvent.class);


    public void event(IdmAuditLog log) {
        l.debug("IHEAuditEvent Audit Event called...");

        l.debug("Linked Log Id =" + getParentLogId(log));

        if (getParentLogId(log) != null && getParentLogId(log).length() > 0) {

            l.debug("Not a primary event. Skipping event" + log.getId());
            return;

        }

        String excludePrincipal = ATNA_EXCLUDE_PRINCIPAL;
        if (excludePrincipal != null && excludePrincipal.length() > 0) {
            if (log.getPrincipal() != null) {
                if (excludePrincipal.equalsIgnoreCase(log.getPrincipal())) {
                    l.debug("Skipping event for identity = " + excludePrincipal);
                    return;
                }
            }

        }

        byte[] bAry = null;
        if (CollectionUtils.isNotEmpty(log.getTargets()))
            for (AuditLogTarget auditLogTarget : log.getTargets()) {


                if (auditLogTarget.getTargetType().equalsIgnoreCase("USER")) {
                    if (log.getAction().equalsIgnoreCase("LOGIN")) {
                        bAry = login(log);
                    }
                    if (log.getAction().equalsIgnoreCase("LOGOUT")) {
                        bAry = logout(log);
                    }
                }
                if (auditLogTarget.getTargetType().equalsIgnoreCase("USER")) {
                    bAry = userChange(log);
                }
        /*if (auditLogTarget.getTargetType().equalsIgnoreCase("PASSWORD")) {
            bAry = userChange(log);
        }*/

                if (auditLogTarget.getTargetType().equalsIgnoreCase("ROLE")) {
                    bAry = roleChange(log);
                }
                if (auditLogTarget.getTargetType().equalsIgnoreCase("RESOURCE")) {
                    bAry = roleChange(log);
                }
        /*if (auditLogTarget.getTargetType().equalsIgnoreCase("POLICY")) {
            bAry = roleChange(log);
        }*/

                if (auditLogTarget.getTargetType().equalsIgnoreCase("GROUP")) {
                    bAry = roleChange(log);
                }

        /*if (auditLogTarget.getTargetType().equalsIgnoreCase("MANAGED_SYS")) {
            bAry = roleChange(log);
        }*/
            }


        l.debug("Calling Send ATNA Message");
        // -----
        sendMessage(bAry);
        // -----

        l.debug("IHEAuditEvent Audit Event completed...");

    }

    public boolean isAlive() {
        l.debug("isAlive test called. ");

        String keyStorePath = ATNA_KEYSTORE_PATH;                 //"/opt/openiam/client.jks";

        String clientKeyStorePassword = ATNA_STORE_PASSWORD;       //"clientKeyStorePassword";
        String clientKeyPassword = ATNA_CLIENT_PASSWORD;         //"clientKeyPassword";

        String ip = ATNA_HOST;
        String sPort = ATNA_PORT;
        int port = Integer.valueOf(sPort);


        char[] keyStorePasswordByteArray = clientKeyStorePassword.toCharArray();
        char[] keyPasswordByteArray = clientKeyPassword.toCharArray();


        System.setProperty("javax.net.ssl.trustStore", keyStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", clientKeyStorePassword);

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");

            keyStore.load(new FileInputStream(new File(keyStorePath)), keyStorePasswordByteArray);

            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance("SunX509");
            kmFactory.init(keyStore, keyPasswordByteArray);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");

            sslContext.init(kmFactory.getKeyManagers(), null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            SSLSocket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            socket.setEnabledProtocols(new String[]{"TLSv1"});

            l.debug("Start handshake test...");

            socket.startHandshake();

            l.debug("handshake test complete..");


            socket.close();

            return true;


        } catch (Exception e) {
            l.error(e.toString(), e);
            return false;


        }

    }

    private void sendMessage(byte[] bAry) {

        l.debug("IHEAuditEvent Sending Message...");

        String keyStorePath = ATNA_KEYSTORE_PATH;                 //"/opt/openiam/client.jks";

        String clientKeyStorePassword = ATNA_STORE_PASSWORD;       //"clientKeyStorePassword";
        String clientKeyPassword = ATNA_CLIENT_PASSWORD;         //"clientKeyPassword";

        String ip = ATNA_HOST;
        String sPort = ATNA_PORT;
        int port = Integer.valueOf(sPort);


        if (bAry == null || bAry.length < 10) {
            return;
        }


        char[] keyStorePasswordByteArray = clientKeyStorePassword.toCharArray();
        char[] keyPasswordByteArray = clientKeyPassword.toCharArray();


        System.setProperty("javax.net.ssl.trustStore", keyStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", clientKeyStorePassword);

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");

            keyStore.load(new FileInputStream(new File(keyStorePath)), keyStorePasswordByteArray);

            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance("SunX509");
            kmFactory.init(keyStore, keyPasswordByteArray);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");

            sslContext.init(kmFactory.getKeyManagers(), null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            SSLSocket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            socket.setEnabledProtocols(new String[]{"TLSv1"});

            l.debug("Start handshake...");

            socket.startHandshake();

            l.debug("handshake complete..");

            OutputStream out = socket.getOutputStream();
            out.write(bAry);
            out.flush();
            out.close();


            socket.close();


        } catch (Exception e) {
            l.error(e.toString(), e);
            return;


        }
        l.debug("IHEAuditEvent Message Sent...");
    }


    private byte[] login(IdmAuditLog log) {
        l.debug("Preparing login event message");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String timeStr = format.format(log.getTimestamp());

        String eventOutcome = "0";
        if (log.getResult().equals("FAILURE")) {
            eventOutcome = "4";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"E\" EventDateTime=\"" + timeStr + "\" EventOutcomeIndicator=\"" + eventOutcome + "\" EventOutcomeDescription=\"" + getReason(log) + "\" >");
        buf.append("  <EventID csd-code=\"110114\" codeSystemName=\"DCM\" displayName=\"User Authentication\"/>");
        buf.append("  <EventTypeCode csd-code=\"110122\" codeSystemName=\"DCM\" displayName=\"Login\"/>");
        buf.append(" </EventIdentification>");
        buf.append("<ActiveParticipant UserID=\"" + log.getPrincipal() + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"" + log.getClientIP() + "\" >");
        buf.append("</ActiveParticipant>");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\"   NetworkAccessPointID=\"" + log.getNodeIP() + "\" >");
        buf.append("</ActiveParticipant>");


        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST DiR\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");
        buf.append(" </AuditMessage>");

        String payLoad = buf.toString();

        l.debug("LOGIN MESSAGE:" + buf.toString());

        return payLoad.getBytes();


    }

    private byte[] logout(IdmAuditLog log) {
        l.debug("Preparing login event message");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String timeStr = format.format(log.getTimestamp());

        String eventOutcome = "0";
        if (log.getResult().equals("FAILURE")) {
            eventOutcome = "4";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>  ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"E\" EventDateTime=\"" + timeStr + "\" EventOutcomeIndicator=\"" + eventOutcome + "\" EventOutcomeDescription=\"" + getReason(log) + "\" >");
        buf.append("  <EventID csd-code=\"110114\" codeSystemName=\"DCM\" displayName=\"User Authentication\"/>");
        buf.append("  <EventTypeCode csd-code=\"110123\" codeSystemName=\"DCM\" displayName=\"Logout\"/>");
        buf.append(" </EventIdentification>\n");
        buf.append("<ActiveParticipant UserID=\"" + log.getPrincipal() + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"" + log.getClientIP() + "\" >");
        buf.append("</ActiveParticipant>");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointID=\"" + log.getNodeIP() + "\"  >");
        buf.append("</ActiveParticipant>");


        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST DiR\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");
        buf.append(" </AuditMessage>");

        String payLoad = buf.toString();

        l.debug("LOGIN MESSAGE:" + buf.toString());

        return payLoad.getBytes();


    }


    private byte[] userChange(IdmAuditLog log) {
        l.debug("Preparing User Changed event message");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String timeStr = format.format(log.getTimestamp());

        String actionCode = null;
        if (log.getAction().equalsIgnoreCase("CREATE USER")) {
            actionCode = "C";
        } else {
            actionCode = "U";
        }

        String eventOutcome = "0";
        if (log.getResult().equals("FAILURE")) {
            eventOutcome = "12";
        }

        String reason = getReason(log);
        if (reason == null) {
            reason = "";
        }

        String actionId = "";
        if (log.getAction() != null) {
            actionId = encodeBase64String(log.getAction().getBytes());

        }


        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>  \n");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        buf.append("<EventIdentification EventActionCode=\"" + actionCode + "\" EventDateTime=\"" + timeStr + "\" EventOutcomeIndicator=\"" + eventOutcome + "\" EventOutcomeDescription=\"" + reason + "\" >");
        buf.append("  <EventID csd-code=\"IAM110113\" codeSystemName=\"DCM\" displayName=\"Identity Manager Used\"/>");
        buf.append("  <EventTypeCode csd-code=\"110137\" codeSystemName=\"DCM\" displayName=\"User Security Attributes Changed\"/>");
        buf.append(" </EventIdentification>");

        // User
        buf.append("<ActiveParticipant UserID=\"" + log.getPrincipal() + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\"  NetworkAccessPointID=\"" + log.getClientIP() + "\"  />");
        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"" + "172.17.2.114" + "\"  />");

        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST Di-R\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");

        buf.append("<ParticipantObjectIdentification ParticipantObjectTypeCode=\"1\" ParticipantObjectTypeCodeRole=\"11\" ParticipantObjectID=\"" + getTargetPrincipal(log) + "\" >");
        buf.append(" <ParticipantObjectIDTypeCode code=\"11\" codeSystemName=\"DCM\" displayName=\"Security User Entity\"> </ParticipantObjectIDTypeCode> ");
        buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\"" + actionId + "\"/>");


        buf.append("</ParticipantObjectIdentification>");

        buf.append("</AuditMessage>");

        String payLoad = buf.toString();

        l.debug("USER CHANGE MESSAGE:" + buf.toString());

        return payLoad.getBytes();

    }


    private byte[] roleChange(IdmAuditLog log) {
        l.debug("Preparing User Changed event message");

        String eventDisplayName = null;
        String eventDisplayNameSuffix = null;

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String timeStr = format.format(log.getTimestamp());

        String actionCode = null;
        if (log.getAction().equalsIgnoreCase("CREATE ROLE") || log.getAction().equalsIgnoreCase("CREATE RESOURCE")
                || log.getAction().equalsIgnoreCase("CREATE GROUP")) {
            actionCode = "C";
            eventDisplayNameSuffix = "created";
        } else {
            actionCode = "U";
            eventDisplayNameSuffix = "modified";
        }

        String eventOutcome = "0";
        if (log.getResult().equals("FAILURE")) {
            eventOutcome = "12";
        }

        String typeCode = "";
        String typeDisplayName = "";

        for (AuditLogTarget auditLogTarget : log.getTargets()) {

            if (auditLogTarget.getTargetType().equalsIgnoreCase("ROLE")) {
                typeCode = "12";
                typeDisplayName = "Role";
                eventDisplayName = "Role " + eventDisplayNameSuffix;
            }
            if (auditLogTarget.getTargetType().equalsIgnoreCase("GROUP")) {
                typeCode = "13";
                typeDisplayName = "Group";
                eventDisplayName = "Group " + eventDisplayNameSuffix;
            }
            if (auditLogTarget.getTargetType().equalsIgnoreCase("RESOURCE")) {
                typeCode = "14";
                typeDisplayName = "Resource";
                eventDisplayName = "Resource " + eventDisplayNameSuffix;
            }

        }

        String encodeDisplayName = "";
        if (eventDisplayName != null) {
            encodeDisplayName = encodeBase64String(eventDisplayName.getBytes());

        }


        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"" + actionCode + "\" EventDateTime=\"" + timeStr + "\" EventOutcomeIndicator=\"" + eventOutcome + "\" EventOutcomeDescription=\"" + getReason(log) + "\" >");
        buf.append("  <EventID csd-code=\"IAM110113\" codeSystemName=\"DCM\" displayName=\"Identity Manager Used\"/>");

        buf.append("  <EventTypeCode csd-code=\"110136\" codeSystemName=\"DCM\" displayName=\"Security Roles Changed\"/>");

        // buf.append("  <EventTypeCode csd-code=\"110136\" codeSystemName=\"DCM\" displayName=\""+ eventDisplayName +"\"/>");

        buf.append(" </EventIdentification>");

        // User
        buf.append("<ActiveParticipant UserID=\"" + log.getPrincipal() + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\"" + log.getClientIP() + "\" />");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointTypeCode=\"2\"  NetworkAccessPointID=\"" + "172.17.2.114" + "\"  />");

        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST Di-R\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");


        buf.append("<ParticipantObjectIdentification ParticipantObjectTypeCode=\"2\" ParticipantObjectTypeCodeRole=\"" + typeCode + "\" ParticipantObjectID=\"" + getTargetName(log) + "\" >");
        buf.append("<ParticipantObjectIDTypeCode  code=\"" + typeCode + "\"  codeSystemName=\"DCM\" displayName=\"" + typeDisplayName + "\"> </ParticipantObjectIDTypeCode> ");
        buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\"" + encodeDisplayName + "\"/>");


        buf.append("</ParticipantObjectIdentification>");

        //  buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\"null\"> </ParticipantObjectIdentification> " );
        buf.append(" </AuditMessage>");

        String payLoad = buf.toString();

        l.debug("ROLE CHANGE MESSAGE:" + buf.toString());

        return payLoad.getBytes();

    }

    private String getReason(IdmAuditLog log) {

        Map<String, String> label = new HashMap<String, String>();
        label.put("-1", "INTERNAL_ERROR");
        label.put("1", "RESULT_SUCCESS");
        label.put("2", "RESULT_SUCCESS_PASSWORD_EXP");
        label.put("3", "RESULT_SUCCESS_FIRST_TIME");
        label.put("100", "RESULT_INVALID_LOGIN");
        label.put("101", "RESULT_INVALID_PASSWORD");
        label.put("109", "RESULT_INVALID_DOMAIN");
        label.put("102", "RESULT_PASSWORD_EXPIRED");
        label.put("103", "RESULT_LOGIN_LOCKED");
        label.put("110", "RESULT_LOGIN_DISABLED");
        label.put("104", "RESULT_INVALID_USER_STATUS");
        label.put("105", "RESULT_SERVICE_UNAVAILABLE");
        label.put("107", "RESULT_SERVICE_NOT_FOUND");
        label.put("108", "RESULT_INVALID_TOKEN");
        label.put("120", "RESULT_INVALID_CONFIGURATION");
        label.put("106", "RESULT_SENSITIVE_APP");
        label.put("111", "RESULT_PASSWORD_CHANGE_AFTER_RESET");

        for (IdmAuditLogCustom rec : log.getCustomRecords()) {

            if (label.get(rec.getValue()) != null) {
                return label.get(rec.getValue());
            } else {
                return rec.getValue();
            }

        }
        return null;
    }

    private String getTargetName(IdmAuditLog log) {

        for (AuditLogTarget target : log.getTargets()) {

            switch (target.getTargetType()) {
                case "USER":
                    UserEntity user = userDataService.getUser(target.getTargetId());
                    return user.getFirstName() + " " + user.getLastName();
                case "RESOURCE":
                    ResourceEntity resource = resourceService.findResourceById(target.getTargetId());
                    return resource.getName();
                case "ROLE":
                    RoleEntity role = roleDataService.getRole(target.getTargetId());
                    return role.getName();
                case "GROUP":
                    GroupEntity group = groupDataService.getGroup(target.getTargetId());
                    return group.getName();
                case "MANAGED_SYS":
                    return target.getObjectPrincipal();
            }
        }
        return null;
    }


    private String getParentLogId(IdmAuditLog log) {
        if (CollectionUtils.isEmpty(log.getParentLogs())) {
            return null;
        }
        String parentLogId = null;

        for (IdmAuditLog idmAuditLog : log.getParentLogs()) {
            parentLogId = idmAuditLog.getId();
        }

        return parentLogId;
    }

    private String getTargetPrincipal(IdmAuditLog log) {

        String targetPrincipal = null;
        for (AuditLogTarget target : log.getTargets()) {
            targetPrincipal = target.getObjectPrincipal();
        }

        return targetPrincipal;
    }


}
