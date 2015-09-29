package org.openiam.util.an;

import java.util.Arrays;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

/**
 * Created by Vitaly on 8/2/2015.
 */
public class ANPasswordDeriveBytes {

    private byte[] password;
    private byte[] salt;
    private byte[] baseValue;
    private Digest digest;
    private int iterationCount;

    public ANPasswordDeriveBytes() {
    }

    public ANPasswordDeriveBytes(byte[] password, byte[] salt) {
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes constructor");
        digest = new SHA1Digest();
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes SHA1Digest");
        iterationCount = 100;
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes iterationCount");
        this.password = Arrays.copyOf(password, password.length);
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes Arrays copyOf1");
        this.salt = salt != null ? Arrays.copyOf(salt, salt.length) : null;
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes Arrays copyOf2");
        baseValue = null;
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes end");
    }

    public byte[] getBytes(int cb) {
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes getBytes");
        byte[] rgbOut = new byte[cb];

        if (baseValue == null) {
            computeBaseValue();
        }

        int i = 0;
        int ib = 0;
        while(ib < cb) {
            byte[] rgb = computeDigest(i++);
            int len = Math.min(rgb.length, cb-ib);
            System.arraycopy(rgb, 0, rgbOut, ib, len);
            ib += len;
        }
        baseValue = null;
        System.out.println("================ an_extAttribute13.groovy PasswordDeriveBytes getBytes end");
        return rgbOut;
    }

    public byte[] computeDigest(int iteration) {

        byte[] digestBytes = new byte[digest.getDigestSize()];

        if (iteration > 0) {
            byte[] prefixBytes = getHashPrefix(iteration);
            digest.update(prefixBytes, 0, prefixBytes.length);
        }
        digest.update(baseValue, 0, baseValue.length);
        digest.doFinal(digestBytes, 0);

        return digestBytes;

    }

    public byte[] getHashPrefix(int _prefix) {
        int cb = 0;
        byte[] rgb = new byte[3];

        if (_prefix >= 100) {
            rgb[0] += (byte) (_prefix /100);
            cb += 1;
        }
        if (_prefix >= 10) {
            rgb[cb] += (byte) ((_prefix % 100) / 10);
            cb += 1;
        }
        if (_prefix > 0) {
            rgb[cb] += (byte) (_prefix % 10);
            cb += 1;
            byte[] rgbOut = new byte[cb];
            System.arraycopy(rgb, 0, rgbOut, 0, cb);
            return rgbOut;
        }
        return null;
    }

    public void computeBaseValue() {

        byte[] digestBytes = new byte[digest.getDigestSize()];

        digest.update(password, 0, password.length);
        if (salt != null) {
            digest.update(salt, 0, salt.length);
        }
        digest.doFinal(digestBytes, 0);

        for (int i = 1; i < (iterationCount -1); i++)
        {
            digest.update(digestBytes, 0, digestBytes.length);
            digest.doFinal(digestBytes, 0);
        }

        baseValue = digestBytes.clone();

    }
}
