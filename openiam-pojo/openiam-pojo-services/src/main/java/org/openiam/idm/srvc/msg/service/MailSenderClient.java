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
package org.openiam.idm.srvc.msg.service;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

/**
 * Object containing mail server configuration
 *
 * @author suneet
 */
public class MailSenderClient {
    String host;
    String port;
    String username;
    String password;
    boolean auth;
    boolean starttls;

    private static final Log log = LogFactory.getLog(Message.class);


    public void send(Message msg) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.transport.protocol", "smtp");


        if (username != null && !username.isEmpty()) {
            properties.setProperty("mail.user", username);
            properties.setProperty("mail.password", password);
        }

        if (port != null && !port.isEmpty()) {
            properties.setProperty("mail.smtp.port", port);
        }


        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(msg.getFrom());
            if (msg.getTo().size() > 1) {
                List<InternetAddress> addresses = msg.getTo();
                message.addRecipients(TO, addresses.toArray(new Address[addresses.size()]));
            } else {
                message.addRecipient(TO, msg.getTo().get(0));
            }
            if (msg.getBcc() != null && msg.getBcc().size() != 0) {
                if (msg.getTo().size() > 1) {
                    List<InternetAddress> addresses = msg.getBcc();
                    message.addRecipients(BCC, addresses.toArray(new Address[addresses.size()]));
                } else {
                    message.addRecipient(TO, msg.getBcc().get(0));
                }
            }
            if (msg.getCc() != null && msg.getCc().size() > 0) {
                if (msg.getCc().size() > 1) {
                    List<InternetAddress> addresses = msg.getCc();
                    message.addRecipients(CC, addresses.toArray(new Address[addresses.size()]));
                } else {
                    message.addRecipient(CC, msg.getCc().get(0));
                }
            }
            message.setSubject(msg.getSubject(), "UTF-8");
            MimeBodyPart mbp1 = new MimeBodyPart();
            // create the Multipart and add its parts to it
            Multipart mp = new MimeMultipart();

            if (msg.getBodyType() == Message.BodyType.HTML_TEXT) {
                mbp1.setContent(msg.getBody(), "text/html");
            } else {
                mbp1.setText(msg.getBody(), "UTF-8");
            }
            if (port != null && !port.isEmpty()) {
                properties.setProperty("mail.smtp.port", port);
            }
            mp.addBodyPart(mbp1);
            if (msg.getAttachments().size() > 0) {
                for (String fileName : msg.getAttachments()) {
                    // create the second message part
                    MimeBodyPart mbpFile = new MimeBodyPart();
                    // attach the file to the message
                    FileDataSource fds = new FileDataSource(fileName);
                    mbpFile.setDataHandler(new DataHandler(fds));
                    mbpFile.setFileName(fds.getName());

                    mp.addBodyPart(mbpFile);
                }
            }
            // add the Multipart to the message
            message.setContent(mp);


            if (username != null && !username.isEmpty()) {
                properties.setProperty("mail.user", username);
                properties.setProperty("mail.password", password);
                properties.put("mail.smtp.auth", "true");
                Transport mailTransport = session.getTransport();
                mailTransport.connect(host, username, password);
                mailTransport.sendMessage(message, message.getAllRecipients());

            } else {
                Transport.send(message);
                log.debug("Message successfully sent.");
            }
        } catch (Throwable e) {
            log.error("Exception while sending mail", e);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
