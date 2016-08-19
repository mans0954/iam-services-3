package org.openiam.idm.srvc.file;

import java.io.File;

/**
 * Created by alexander on 16/08/16.
 */
public interface FileService {
    String getFile(String fName);
    File saveFile(String fName, String value);
}
