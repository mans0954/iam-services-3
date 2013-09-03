package org.openiam.idm.util;

import com.jcraft.jsch.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import java.util.Vector;

@Service
public class RemoteFileStorageManager {
    @Value("${org.openiam.upload.remote.sftp.host}")
    private String remoteFilestorageHost;
    @Value("${org.openiam.upload.remote.sftp.port}")
    private Integer remoteFilestoragePort;
    @Value("${org.openiam.upload.remote.sftp.user}")
    private String remoteFilestorageUser;
    @Value("${org.openiam.upload.remote.sftp.passwd}")
    private String remoteFilestoragePasswd;
    @Value("${org.openiam.upload.remote.sftp.keypath}")
    private String remoteFilestorageKeypath;
    @Value("${org.openiam.upload.remote.sftp.directory}")
    private String remoteFilestorageDir;

    //Upload Attachment to remote host
    public void uploadFile(final InputStream fileInputStream, final String destSubDirectory, final String fileName) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        boolean createNewDirectory = true;
        if (fileInputStream != null) {
            sftpChannel.cd(remoteFilestorageDir);
            Vector files = sftpChannel.ls(remoteFilestorageDir);
            for (int i = 0; i < files.size(); i++) {
                com.jcraft.jsch.ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) files.get(i);
                if (!lsEntry.getFilename().equals(".") && !lsEntry.getFilename().equals("..")) {
                    if (lsEntry.getFilename().equalsIgnoreCase(destSubDirectory))
                        createNewDirectory = false;
                }
            }
            if (createNewDirectory) {
                sftpChannel.mkdir(destSubDirectory);
            }
            sftpChannel.cd(destSubDirectory);

            sftpChannel.put(fileInputStream, fileName);

            sftpChannel.quit();

        }
        session.disconnect();
    }

    //Download Attachment from remote host
    public InputStream downloadFile(final String destFilePath) throws JSchException, SftpException {
        Session session = getSession();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        String fileDownloadName = FilenameUtils.getBaseName(destFilePath);
        InputStream is = sftpChannel.get(fileDownloadName);

        sftpChannel.quit();

        session.disconnect();
        return is;
    }

    private Session getSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = null;
        if (StringUtils.isNotEmpty(remoteFilestorageKeypath)) {
            jsch.addIdentity(remoteFilestorageKeypath);
        }
        jsch.setConfig("StrictHostKeyChecking", "no");

        session = jsch.getSession(remoteFilestorageUser, remoteFilestorageHost, remoteFilestoragePort);
        if (StringUtils.isNotEmpty(remoteFilestoragePasswd)) {
            session.setPassword(remoteFilestoragePasswd);
        }
        session.connect();
        return session;
    }
}
