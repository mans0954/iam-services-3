package org.openiam.base.request;

/**
 * Created by alexander on 01/12/16.
 */
public class FileRequest extends BaseServiceRequest {
    private String fileName;
    private String fileContent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileRequest{");
        sb.append(super.toString());
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", fileContent='").append(fileContent).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
