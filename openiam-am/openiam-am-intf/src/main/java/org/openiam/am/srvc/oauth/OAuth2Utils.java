package org.openiam.am.srvc.oauth;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.util.DigestUtils;

/**
 * Created by alexander on 24/06/15.
 */
public class OAuth2Utils {

    public static long ACCESS_TOKEN_EXPIRES_IN = 60 * 60; // hour
    public static long CODE_EXPIRES_IN = 5 * 60; // 5 min

    private static final int TOKEN_SIZE = 64;
    private static SecureRandom secureRandom = new SecureRandom();

    private static final String TOKEN_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz._-";

    /**
     * Generate a random token that conforms to <a href="http://tools.ietf.org/html/rfc6750">RFC 6750 Bearer Token</a>
     *
     * @return a new 64 character token that is URL Safe (no '+' or '/'  characters).
     */
    public static String randomToken() throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        String token  = RandomStringUtils.random(TOKEN_SIZE, 0, TOKEN_CHARS.length(), true, true, TOKEN_CHARS.toCharArray()) + System.nanoTime();
       // return RandomStringUtils.random(TOKEN_SIZE, 0, TOKEN_CHARS.length(), true, true, TOKEN_CHARS.toCharArray());

       return generateRandomString(TOKEN_SIZE);
    }

    private static String generateRandomString(int length){
        Random random = new Random();
        return random.ints(45,122)
                .filter(i-> TOKEN_CHARS.contains(String.valueOf((char)i)))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }


    public static String randomClientId() {
        return RandomStringUtils.random(12, true, true).toUpperCase();
    }

    public static String randomClientSecret() {
        return DigestUtils.md5DigestAsHex(String.valueOf(
                RandomStringUtils.random(4, true, true) + System.nanoTime())
                .getBytes());
    }

    private static String sha(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return new String(Hex.encodeHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"))));
    }


    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        for (int i=0; i<100;i++){
            String token = randomToken();
            System.out.println(token);
        }
    }
}
