package org.openiam.security.oauth.util;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by alexander on 24/06/15.
 */
public class OAuth2Utils {

    public static int ACCESS_TOKEN_EXPIRES_IN = 60 * 60; // hour
    public static int CODE_EXPIRES_IN = 5 * 60*1000; // 5 min

    private static final int MIN_TOKEN_SIZE = 32;
    private static final int MAX_TOKEN_SIZE = 100;
    private static final int ClIENT_SECRET_SIZE = 45;

    private static final String TOKEN_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz._-";

    public static String randomCode() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String randomClientId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static String randomClientSecret() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return sha(String.valueOf(generateRandomString(ClIENT_SECRET_SIZE) + System.nanoTime()));
    }

    public static String randomToken() {
        return generateRandomString(getTokenSize());
    }

    private static int getTokenSize() {
        Random rn = new Random();
        int range = MAX_TOKEN_SIZE - MIN_TOKEN_SIZE + 1;
        return MIN_TOKEN_SIZE + rn.nextInt(range);
    }

    /**
     * Generate a random token that conforms to <a href="http://tools.ietf.org/html/rfc6750">RFC 6750 Bearer Token</a>
     *
     * @return a new token that is URL Safe (no '+' or '/'  characters).
     */
    public static String generateRandomString(int length){
        Random random = new Random();
        return random.ints(45,122)
                .filter(i-> TOKEN_CHARS.contains(String.valueOf((char)i)))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private static String sha(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return new String(Hex.encodeHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"))));
    }

    public static Date getCodeExpirationDate(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MILLISECOND, CODE_EXPIRES_IN);

        return c.getTime();
    }

}
