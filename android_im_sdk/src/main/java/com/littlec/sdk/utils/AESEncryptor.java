package com.littlec.sdk.utils;
import android.text.TextUtils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
* @ClassName: AESEncryptor 
* @Description: TODO(用于AES加密解密) 
* @author user
* @date 2016-5-4 下午5:08:11 
*
 */
public class AESEncryptor {

    public static final String  AES_DEFAULT_ENCRIPT_SEED = "com.cmcc.common";

    /**
     * AES加密
     */
    public static String encrypt(String seed, String cleartext) throws Exception {
        String tmpSeed = seed;

        if(TextUtils.isEmpty(cleartext)) {
            return null;
        }

        if(TextUtils.isEmpty(seed)) {
            tmpSeed = AES_DEFAULT_ENCRIPT_SEED;
        }


        byte[] rawKey = getRawKey(tmpSeed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    /**
     * AES解密
     */
    public static String decrypt(String seed, String encrypted) throws Exception {
        if(TextUtils.isEmpty(encrypted)) {
            return null;
        }

        String tmpSeed = seed;
        if(TextUtils.isEmpty(seed)) {
            tmpSeed = AES_DEFAULT_ENCRIPT_SEED;
        }

        byte[] rawKey = getRawKey(tmpSeed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
