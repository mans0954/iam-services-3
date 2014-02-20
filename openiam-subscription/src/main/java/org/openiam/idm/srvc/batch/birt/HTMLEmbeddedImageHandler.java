package org.openiam.idm.srvc.batch.birt;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IImage;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;

public class HTMLEmbeddedImageHandler extends HTMLServerImageHandler {

    private static Logger log = Logger.getLogger(HTMLEmbeddedImageHandler.class);

    private final static String IMAGE_PREFIX_TEMPLATE = "data:{mime-type};base64,";

    @Override
    protected String handleImage(IImage image, Object context, String prefix, boolean needMap) {
        return getImagePrefix(image.getMimeType()) + printBase64(image.getImageStream());
    }

    private static String getImagePrefix(String mimeType) {
        return IMAGE_PREFIX_TEMPLATE.replace("{mime-type}", mimeType);
    }

    private static String printBase64(InputStream inputStream) {
        String encodedString = null;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            encodedString = DatatypeConverter.printBase64Binary(bytes);
        } catch (IOException e) {
            log.error(e);
        }
        return encodedString;
    }
}
