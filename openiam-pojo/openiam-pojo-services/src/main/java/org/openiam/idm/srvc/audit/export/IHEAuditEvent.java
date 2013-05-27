package org.openiam.idm.srvc.audit.export;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation to export IHE (Healthcare) Audit Events
 * User: suneetshah
 * Date: 9/18/11
 * Time: 10:47 PM
 */
@Component("iheAuditEvent")
public class IHEAuditEvent implements ExportAuditEvent {

	@Value("${ATNA_EXCLUDE_PRINCIPAL}")
	private String excludePrincipal;
	
	@Value("${ATNA_KEYSTORE_PATH}")
	private String keyStorePath;

	@Value("${ATNA_STORE_PASSWORD}")
	private String clientKeyStorePassword;
	
	@Value("${ATNA_CLIENT_PASSWORD}")
	private String clientKeyPassword;
	
	@Value("${ATNA_HOST}")
	private String atnaHost;
	
	@Value("${ATNA_PORT}")
	private int atnaPort;

    private static final Log l = LogFactory.getLog(IHEAuditEvent.class);

    public void event(IdmAuditLog log) {
        l.debug("IHEAuditEvent Audit Event called...");

        l.debug("Linked Log Id =" + log.getLinkedLogId());

        if (log.getLinkedLogId() != null && log.getLinkedLogId().length() > 0) {

            l.debug("Not a primary event. Skipping event" + log.getLogId());
            return;

        }

        if (excludePrincipal != null && excludePrincipal.length() > 0) {
            if (log.getPrincipal() != null) {
                if (excludePrincipal.equalsIgnoreCase(log.getPrincipal())) {
                    l.debug("Skipping event for identity = " + excludePrincipal);
                    return;
                }
            }

        }

        byte[] bAry = null;

        if (log.getObjectTypeId().equalsIgnoreCase("AUTHENTICATION")) {
            if (log.getActionId().equalsIgnoreCase("AUTHENTICATION")) {
                bAry = login(log);
            }
            if (log.getActionId().equalsIgnoreCase("LOGOUT")) {
                bAry = logout(log);
            }
        }
        if (log.getObjectTypeId().equalsIgnoreCase("USER")) {
            bAry = userChange(log);
        }
        if (log.getObjectTypeId().equalsIgnoreCase("PASSWORD")) {
            bAry = userChange(log);
        }

        if (log.getObjectTypeId().equalsIgnoreCase("ROLE")) {
            bAry = roleChange(log);
        }
        if (log.getObjectTypeId().equalsIgnoreCase("RESOURCE")) {
            bAry = roleChange(log);
        }
        if (log.getObjectTypeId().equalsIgnoreCase("POLICY")) {
            bAry = roleChange(log);
        }

        l.debug("Calling Send ATNA Message");
        // -----
        sendMessage(bAry);
        // -----

        l.debug("IHEAuditEvent Audit Event completed...");

    }

    public boolean isAlive() {
        l.debug("isAlive test called. ");

        char[] keyStorePasswordByteArray = clientKeyStorePassword.toCharArray();
        char[] keyPasswordByteArray = clientKeyPassword.toCharArray();

        System.setProperty("javax.net.ssl.trustStore", keyStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword",
                clientKeyStorePassword);

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");

            keyStore.load(new FileInputStream(new File(keyStorePath)),
                    keyStorePasswordByteArray);

            KeyManagerFactory kmFactory = KeyManagerFactory
                    .getInstance("SunX509");
            kmFactory.init(keyStore, keyPasswordByteArray);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");

            sslContext.init(kmFactory.getKeyManagers(), null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            SSLSocket socket = (SSLSocket) socketFactory.createSocket(atnaHost, atnaPort);

            socket.setEnabledProtocols(new String[] { "TLSv1" });

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

        if (bAry == null || bAry.length < 10) {
            return;
        }

        char[] keyStorePasswordByteArray = clientKeyStorePassword.toCharArray();
        char[] keyPasswordByteArray = clientKeyPassword.toCharArray();

        System.setProperty("javax.net.ssl.trustStore", keyStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword",
                clientKeyStorePassword);

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");

            keyStore.load(new FileInputStream(new File(keyStorePath)),
                    keyStorePasswordByteArray);

            KeyManagerFactory kmFactory = KeyManagerFactory
                    .getInstance("SunX509");
            kmFactory.init(keyStore, keyPasswordByteArray);

            SSLContext sslContext = SSLContext.getInstance("TLSv1");

            sslContext.init(kmFactory.getKeyManagers(), null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            SSLSocket socket = (SSLSocket) socketFactory.createSocket(atnaHost, atnaPort);

            socket.setEnabledProtocols(new String[] { "TLSv1" });

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
        String timeStr = format.format(log.getActionDatetime());

        String eventOutcome = "0";
        if (log.getActionStatus().equals("FAIL")) {
            eventOutcome = "4";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"E\" EventDateTime=\""
                + timeStr
                + "\" EventOutcomeIndicator=\""
                + eventOutcome
                + "\" EventOutcomeDescription=\"" + log.getReason() + "\" >");
        buf.append("  <EventID csd-code=\"110114\" codeSystemName=\"DCM\" displayName=\"User Authentication\"/>");
        buf.append("  <EventTypeCode csd-code=\"110122\" codeSystemName=\"DCM\" displayName=\"Login\"/>");
        buf.append(" </EventIdentification>");
        buf.append("<ActiveParticipant UserID=\""
                + log.getPrincipal()
                + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\""
                + log.getHost() + "\" >");
        buf.append("</ActiveParticipant>");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\"   NetworkAccessPointID=\""
                + log.getNodeIP() + "\" >");
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
        String timeStr = format.format(log.getActionDatetime());

        String eventOutcome = "0";
        if (log.getActionStatus().equals("FAIL")) {
            eventOutcome = "4";
        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>  ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"E\" EventDateTime=\""
                + timeStr
                + "\" EventOutcomeIndicator=\""
                + eventOutcome
                + "\" EventOutcomeDescription=\"" + log.getReason() + "\" >");
        buf.append("  <EventID csd-code=\"110114\" codeSystemName=\"DCM\" displayName=\"User Authentication\"/>");
        buf.append("  <EventTypeCode csd-code=\"110123\" codeSystemName=\"DCM\" displayName=\"Logout\"/>");
        buf.append(" </EventIdentification>\n");
        buf.append("<ActiveParticipant UserID=\""
                + log.getPrincipal()
                + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\""
                + log.getHost() + "\" >");
        buf.append("</ActiveParticipant>");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointID=\""
                + log.getNodeIP() + "\"  >");
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
        String timeStr = format.format(log.getActionDatetime());

        String actionCode = null;
        if (log.getActionId().equalsIgnoreCase("CREATE")) {
            actionCode = "C";
        } else {
            actionCode = "U";
        }

        String eventOutcome = "0";
        if (log.getActionStatus().equals("FAIL")) {
            eventOutcome = "12";
        }

        String reason = log.getReason();
        if (reason == null) {
            reason = "";
        }

        String actionId = "";
        if (log.getActionId() != null) {
            actionId = encodeBase64String(log.getActionId().getBytes());

        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>  \n");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        buf.append("<EventIdentification EventActionCode=\"" + actionCode
                + "\" EventDateTime=\"" + timeStr
                + "\" EventOutcomeIndicator=\"" + eventOutcome
                + "\" EventOutcomeDescription=\"" + reason + "\" >");
        buf.append("  <EventID csd-code=\"IAM110113\" codeSystemName=\"DCM\" displayName=\"Identity Manager Used\"/>");
        buf.append("  <EventTypeCode csd-code=\"110137\" codeSystemName=\"DCM\" displayName=\"User Security Attributes Changed\"/>");
        buf.append(" </EventIdentification>");

        // User
        buf.append("<ActiveParticipant UserID=\""
                + log.getPrincipal()
                + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\"  NetworkAccessPointID=\""
                + log.getHost() + "\"  />");
        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\""
                + "172.17.2.114" + "\"  />");

        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST Di-R\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");

        buf.append("<ParticipantObjectIdentification ParticipantObjectTypeCode=\"1\" ParticipantObjectTypeCodeRole=\"11\" ParticipantObjectID=\""
                + log.getCustomRecord(3, CustomIdmAuditLogType.ATTRIB) + "\" >");
        buf.append(" <ParticipantObjectIDTypeCode code=\"11\" codeSystemName=\"DCM\" displayName=\"Security User Entity\"> </ParticipantObjectIDTypeCode> ");
        buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\""
                + actionId + "\"/>");

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
        String timeStr = format.format(log.getActionDatetime());

        String actionCode = null;
        if (log.getActionId().equalsIgnoreCase("CREATE")) {
            actionCode = "C";
            eventDisplayNameSuffix = "created";
        } else {
            actionCode = "U";
            eventDisplayNameSuffix = "modified";
        }

        String eventOutcome = "0";
        if (log.getActionStatus().equals("FAIL")) {
            eventOutcome = "12";
        }

        String typeCode = "12";
        String typeDisplayName = "Security User Group";
        if (log.getObjectTypeId().equalsIgnoreCase("ROLE")) {
            typeCode = "12";

            eventDisplayName = "Role " + eventDisplayNameSuffix;
        }
        if (log.getObjectTypeId().equalsIgnoreCase("RESOURCE")) {
            typeCode = "13";
            typeDisplayName = "Security Resource";
            eventDisplayName = "Resource " + eventDisplayNameSuffix;
        }
        if (log.getObjectTypeId().equalsIgnoreCase("POLICY")) {
            typeCode = "14";
            typeDisplayName = "Security Granularity";
            eventDisplayName = "Policy " + eventDisplayNameSuffix;
        }

        String encodeDisplayName = "";
        if (eventDisplayName != null) {
            encodeDisplayName = encodeBase64String(eventDisplayName.getBytes());

        }

        StringBuffer buf = new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        buf.append(" <AuditMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        buf.append("<EventIdentification EventActionCode=\"" + actionCode
                + "\" EventDateTime=\"" + timeStr
                + "\" EventOutcomeIndicator=\"" + eventOutcome
                + "\" EventOutcomeDescription=\"" + log.getReason() + "\" >");
        buf.append("  <EventID csd-code=\"IAM110113\" codeSystemName=\"DCM\" displayName=\"Identity Manager Used\"/>");

        buf.append("  <EventTypeCode csd-code=\"110136\" codeSystemName=\"DCM\" displayName=\"Security Roles Changed\"/>");

        // buf.append("  <EventTypeCode csd-code=\"110136\" codeSystemName=\"DCM\" displayName=\""+
        // eventDisplayName +"\"/>");

        buf.append(" </EventIdentification>");

        // User
        buf.append("<ActiveParticipant UserID=\""
                + log.getPrincipal()
                + "\" UserIsRequestor=\"TRUE\" NetworkAccessPointTypeCode=\"2\" NetworkAccessPointID=\""
                + log.getHost() + "\" />");

        // Node
        buf.append("<ActiveParticipant UserID=\"OpenIAM\" UserIsRequestor=\"FALSE\" NetworkAccessPointTypeCode=\"2\"  NetworkAccessPointID=\""
                + "172.17.2.114" + "\"  />");

        buf.append("  <AuditSourceIdentification AuditSourceEnterpriseSiteId=\"GTA WEST Di-R\" AuditSourceID=\"OpenIAM\"   >");
        buf.append("    <AuditSourceTypeCode code=\"6\" />");
        buf.append("  </AuditSourceIdentification>");

        buf.append("<ParticipantObjectIdentification ParticipantObjectTypeCode=\"2\" ParticipantObjectTypeCodeRole=\""
                + typeCode
                + "\" ParticipantObjectID=\""
                + log.getResourceName() + "\" >");
        buf.append("<ParticipantObjectIDTypeCode  code=\"" + typeCode
                + "\"  codeSystemName=\"DCM\" displayName=\"" + typeDisplayName
                + "\"> </ParticipantObjectIDTypeCode> ");
        buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\""
                + encodeDisplayName + "\"/>");

        buf.append("</ParticipantObjectIdentification>");

        // buf.append("<ParticipantObjectDetail type=\"IAM Action\" value=\"null\"> </ParticipantObjectIdentification> "
        // );
        buf.append(" </AuditMessage>");

        String payLoad = buf.toString();

        l.debug("ROLE CHANGE MESSAGE:" + buf.toString());

        return payLoad.getBytes();

    }

}
