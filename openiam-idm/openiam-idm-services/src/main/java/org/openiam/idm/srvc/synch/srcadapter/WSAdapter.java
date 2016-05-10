/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.synch.srcadapter;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.synch.domain.SynchReviewEntity;
import org.openiam.idm.srvc.synch.dto.*;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.service.WSOperationCommand;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.soap.Node;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.*;

/**
 * Gets data from a Webservice to use for synchronization
 *
 * @author suneet
 */
@Component
public class WSAdapter extends AbstractSrcAdapter { // implements SourceAdapter

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    private static final Log log = LogFactory.getLog(WSAdapter.class);

    private Connection con = null;

    @Override
    public SyncResponse startSynch(final SynchConfig config) {
        return startSynch(config, null, null);
    }

    public static String getMsgAsString(SOAPMessage message) {
        String msg = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.writeTo(baos);
            msg = baos.toString();
        } catch (Exception e) {
            log.error(e);
        }
        return msg;
    }

    private static void findNodeValue(List<String> result, String nodeName, NodeList list) {
        if (list != null && list.getLength() > 0) {
            for (int i = 0; i < list.getLength(); i++) {
                if (nodeName.equals(list.item(i).getNodeName())) {
                    result.add(list.item(i).getNodeName() == null ?
                            list.item(i).getTextContent() : list.item(i).getNodeValue());
                }
                if (list.item(i).getChildNodes() != null && list.item(i).getChildNodes().getLength() != 0) {
                    findNodeValue(result, nodeName, list.item(i).getChildNodes());
                }
            }
        }
    }

    private static void printNodeList(NodeList list) {
        if (list != null && list.getLength() > 0) {
            for (int i = 0; i < list.getLength(); i++) {
                System.out.println(list.item(i).getNodeName() + ":" + list.item(i).getNodeValue() + ":" + list.item(i).getTextContent());
                if (list.item(i).getChildNodes() != null && list.item(i).getChildNodes().getLength() != 0) {
                    printNodeList(list.item(i).getChildNodes());
                }
            }
        }
    }

    //format the XML in your String
    public String formatXML(String unformattedXml) throws Exception {
        try {
            Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            format.setIndent(3);
            format.setOmitXMLDeclaration(true);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseXmlFile(String in) throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<LineObject> getObjects(SynchConfig config) {
        List<LineObject> results = new ArrayList<LineObject>();
        SOAPConnectionFactory soapConnectionFactory = null;
        SOAPConnection soapConnection = null;
        try {
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        // Send SOAP Message to SOAP Server
        Attribute[] attrs = null;
        try {
            attrs = this.parseAttributesString(config.getWsAttributes());
        } catch (Exception e) {
            log.error("Can't parse attributes. Bad format. Server Error=" + e);
        }
        SOAPMessage soapResponse = null;
        try {
            soapResponse = soapConnection.call(createSOAPRequest(config.getWsNameSpace(), config.getWsUri(), config.getWsOperation(), attrs), config.getWsUrl());
        } catch (Exception e) {
            log.error(e);
        }
        // print SOAP Response
        log.info("Response SOAP Message:");
        try {
            List<String> queries = this.parseQueryString(config.getWsTargetEntityPath(), "->");
            if (CollectionUtils.isEmpty(queries)) {
                throw new Exception("No entity path");
            }
            SOAPBody soapBody = soapResponse.getSOAPBody();
            int counter = 0;
            List<org.w3c.dom.Node> result = new ArrayList<org.w3c.dom.Node>();
            NodeList root = soapBody.getElementsByTagName(queries.get(counter));
            NodeList sources = root;
            while (counter != queries.size()) {
                if (sources.getLength() == 0) {
                    log.error("Wrong entity path!");
                    break;
                }
                for (int i = 0; i < sources.getLength(); i++) {
                    if (queries.get(counter).equals(sources.item(i).getNodeName())) {
                        if (counter == queries.size() - 1) {
                            result.add(sources.item(i));
                        } else {
                            sources = root.item(i).getChildNodes();
                            counter++;
                            break;
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(result)) {
                    break;
                }
            }

            if (CollectionUtils.isNotEmpty(result)) {
                for (org.w3c.dom.Node node : result) {
                    if (node.getChildNodes().getLength() > 0) {
                        LineObject o = new LineObject();
                        Map<String, Attribute> columnMap = new HashMap<String, Attribute>();
                        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                            org.w3c.dom.Node n = node.getChildNodes().item(i);
                            columnMap.put(n.getNodeName(), new Attribute(n.getNodeName(), n.getNodeValue() == null ? n.getTextContent() : n.getNodeValue()));
                        }
                        o.setColumnMap(columnMap);
                        results.add(o);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        try {
            soapConnection.close();
        } catch (Exception e) {
            log.error(e);
        }
        return results;
    }

    private Attribute[] parseAttributesString(String attrs) throws Exception {
        if (StringUtils.isBlank(attrs))
            return null;
        if (attrs.split(";") == null)
            return null;

        String[] attributes = attrs.split(";");
        Attribute[] result = new Attribute[attributes.length];
        int counter = 0;
        for (String attribute : attributes) {
            String[] nameValue = attribute.split(":");
            result[counter] = new Attribute(nameValue[0], nameValue[1]);
            counter++;
        }
        return result;
    }

    private List<String> parseQueryString(String attrs, String separator) throws Exception {
        if (StringUtils.isBlank(attrs))
            return null;
        if (attrs.split(separator) == null)
            return null;
        return Arrays.asList(attrs.split(separator));
    }

    @Override
    public SyncResponse startSynch(SynchConfig config, SynchReviewEntity sourceReview, SynchReviewEntity resultReview) {
        LineObject lineHeader = null;
        Date mostRecentRecord = null;

        if(log.isDebugEnabled()) {
        	log.debug("WS SYNCH STARTED ^^^^^^^^");
        }

        SyncResponse res = new SyncResponse(ResponseStatus.SUCCESS);
        SynchReview review = null;
        if (sourceReview != null) {
            review = synchReviewDozerConverter.convertToDTO(sourceReview, false);
        }
        LineObject rowHeaderForReport = null;
        InputStream input = null;

        try {
            final ValidationScript validationScript = org.mule.util.StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config, review) : null;
            final List<TransformScript> transformScripts = SynchScriptFactory.createTransformationScript(config, review);
            final MatchObjectRule matchRule = matchRuleFactory.create(config.getCustomMatchRule()); // check if matchRule exists

            if (validationScript == null || transformScripts == null || matchRule == null) {
                res = new SyncResponse(ResponseStatus.FAILURE);
                res.setErrorText("The problem in initialization of RDBMSAdapter, please check validationScript= " + validationScript + ", transformScripts=" + transformScripts + ", matchRule=" + matchRule + " all must be set!");
                res.setErrorCode(ResponseCode.INVALID_ARGUMENTS);
                return res;
            }


            if (sourceReview != null && !sourceReview.isSourceRejected()) {
                return startSynchReview(config, sourceReview, resultReview, validationScript, transformScripts, matchRule);
            }
            List<LineObject> lineObjectList = this.getObjects(config);
            if (CollectionUtils.isNotEmpty(lineObjectList)) {
                lineHeader = lineObjectList.get(0);
            }

            for (LineObject rowObj : lineObjectList) {
            	if(log.isDebugEnabled()) {
            		log.debug("-SYNCHRONIZING NEW RECORD ---");
            	}
                if (mostRecentRecord == null) {
                    mostRecentRecord = rowObj.getLastUpdate();

                } else {
                    // if current record is newer than what we saved, then update the most recent record value
                    if (mostRecentRecord.before(rowObj.getLastUpdate())) {
                    	if(log.isDebugEnabled()) {
                    		log.debug("- MostRecentRecord value updated to=" + rowObj.getLastUpdate());
                    	}
                        mostRecentRecord.setTime(rowObj.getLastUpdate().getTime());
                    }
                }

                processLineObject(rowObj, config, resultReview, validationScript, transformScripts, matchRule);

            }

        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            res = new SyncResponse(ResponseStatus.FAILURE);
            res.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            return res;
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
            log.error(fe);
//            auditBuilder.addAttribute(AuditAttributeName.DESCRIPTION, "FileNotFoundException: "+fe.getMessage());
//            auditLogProvider.persist(auditBuilder);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            if(log.isDebugEnabled()) {
            	log.debug("WS SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            }
            return resp;
        } catch (IOException io) {
            io.printStackTrace();
            /*
            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.IO_EXCEPTION.toString(), io.toString());
            auditHelper.logEvent(synchStartLog);
			*/
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.IO_EXCEPTION);
            if(log.isDebugEnabled()) {
            	log.debug("WS SYNCHRONIZATION COMPLETE WITH ERRORS ^^^^^^^^");
            }
            return resp;

        } catch (Exception se) {

            log.error(se);
            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            resp.setErrorText(se.toString());
            return resp;

        } finally {
            if (resultReview != null) {
                if (CollectionUtils.isNotEmpty(resultReview.getReviewRecords())) { // add header row
                    resultReview.addRecord(generateSynchReviewRecord(lineHeader, true));
                }
            }
        }

        if(log.isDebugEnabled()) {
        	log.debug("WS SYNCH COMPLETE.^^^^^^^^");
        }

        SyncResponse resp = new SyncResponse(ResponseStatus.SUCCESS);
        resp.setLastRecordTime(mostRecentRecord);
        return resp;

    }

    public Response testConnection(SynchConfig config) {
        //FIXME
//        WSOperationCommand serviceCmd = getServiceCommand(config.getWsScript());
//        if (serviceCmd == null) {
//            Response resp = new Response(ResponseStatus.FAILURE);
//            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
//            return resp;
//        }
        Response resp = new Response(ResponseStatus.SUCCESS);
        return resp;
    }

    private void fillAuthCredentials(SOAPMessage message, String userName, String userPassword, String nameSpace) throws SOAPException {
        SOAPHeader header = message.getSOAPHeader();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        if (header == null) {
            header = envelope.addHeader();
        }
        QName qNameUserCredentials = new QName(nameSpace, "UserCredentials");
        SOAPHeaderElement userCredentials = header.addHeaderElement(qNameUserCredentials);
        QName qNameUsername = new QName(nameSpace, "Username");
        SOAPHeaderElement username = header.addHeaderElement(qNameUsername);
        username.addTextNode(userName);
        QName qNamePassword = new QName(nameSpace, "Password");
        SOAPHeaderElement password = header.addHeaderElement(qNamePassword);
        password.addTextNode(userPassword);

        userCredentials.addChildElement(username);
        userCredentials.addChildElement(password);
    }

    private SOAPMessage createSOAPRequest(String nameSpace, String serverURI, String operation, Attribute... elements) throws Exception {
        String NS = StringUtils.isBlank(nameSpace) ? "example" : nameSpace;
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(NS, serverURI);
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(operation, NS);
        if (elements != null) {
            for (Attribute a : elements) {
                SOAPElement soapBodyElem2 = soapBodyElem.addChildElement(a.getName());
                soapBodyElem2.addTextNode(a.getValue());
            }
        }
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI + operation);
        soapMessage.saveChanges();
        /* Print the request message */
        System.out.print("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println();
        return soapMessage;
    }

    private WSOperationCommand getServiceCommand(String scriptName) {

        if (scriptName == null || scriptName.length() == 0) {
            return null;
        }
        try {
            return (WSOperationCommand) scriptRunner.instantiateClass(null, scriptName);

        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            return null;
        }
    }
}
