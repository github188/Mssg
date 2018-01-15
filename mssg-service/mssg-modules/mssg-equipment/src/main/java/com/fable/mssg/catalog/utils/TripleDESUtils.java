package com.fable.mssg.catalog.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author: yuhl Created on 10:28 2017/11/15 0015
 */
public class TripleDESUtils {

    private final static String TRIPLE_DES = "DESede";

    /**
     * 3DES加密
     * @param data
     * @param desKey
     * @return
     */
    @SneakyThrows
    public static byte[] encodeByTripleDES(byte[] data, String desKey) {
        SecureRandom random = new SecureRandom();
        SecretKey secretKey = new SecretKeySpec(desKey.getBytes(), TRIPLE_DES);
        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
        return cipher.doFinal(data);
    }

    /**
     * 3DES解密
     * @param data
     * @param desKey
     * @return
     */
    @SneakyThrows
    public static byte[] decodeByTriple(byte[] data, String desKey) {
        SecureRandom random = new SecureRandom();
        SecretKey secretKey = new SecretKeySpec(desKey.getBytes(), TRIPLE_DES);
        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
        return cipher.doFinal(data);
    }
}
