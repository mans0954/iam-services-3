package org.openiam.sso.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by: Alexander Duckardt
 * Date: 26.09.12
 */
public class SSOUtils {
    private static final Logger log = LoggerFactory.getLogger(SSOUtils.class);

    public static String getUID(String... parameterList) {
        StringBuilder seed = new StringBuilder();
        seed.append(System.currentTimeMillis());
        seed.append(System.nanoTime());
        if (parameterList != null) {
            for (String parameter : parameterList)
                seed.append(parameter);
        }
        return DigestUtils.shaHex(seed.toString());
    }

    public static byte[] deflate(String data) throws Exception{
        byte[] input = data.getBytes("UTF-8");

        // Compress the bytes
        byte[] output = new byte[input.length];
        Deflater compresser = new Deflater(Deflater.DEFLATED, true);
        compresser.setInput(input);
        compresser.finish();
        int compressedDataLength = compresser.deflate(output);
        compresser.end();

        return Arrays.copyOf(output, compressedDataLength);
    }

    public static String inflate(byte[] data) throws Exception{
        Inflater decompresser = new Inflater(true);
        decompresser.setInput(data, 0, data.length);
        byte[] result = new byte[data.length*2];
        int resultLength = decompresser.inflate(result);
        decompresser.end();

        // Decode the bytes into a String
        return new String(result, 0, resultLength, "UTF-8");
    }

    public static String encodeBase64(byte[] data){
        return new String(Base64.encodeBase64(data));
    }
    public static byte[] decodeBase64(String data) throws UnsupportedEncodingException {
        return Base64.decodeBase64(data.getBytes("UTF-8"));
    }

    public static XMLObject parseSamlResponse(String samlResponse) {

        try {
            DefaultBootstrap.bootstrap();
            // Get parser pool manager
            BasicParserPool ppMgr = new BasicParserPool();
            ppMgr.setNamespaceAware(true);

            // Parse metadata file
            InputStream in =  new ByteArrayInputStream(samlResponse.getBytes());
            Document inCommonMDDoc = ppMgr.parse(in);
            Element metadataRoot = inCommonMDDoc.getDocumentElement();

            // Get apropriate unmarshaller
            UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);

            // Unmarshall using the document root element, an EntitiesDescriptor in this case
            return  unmarshaller.unmarshall(metadataRoot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
