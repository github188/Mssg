package com.fable.mssg.catalog.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * @author: yuhl Created on 10:28 2017/11/15 0015
 */
public class DESUtils {

    private final static String DES_KEY = "DES";

    /**
     * DES加密
     * @param data
     * @param desKey
     * @return
     */
    @SneakyThrows
    public static byte[] encodeByDES(byte[] data, String desKey) {
        DESKeySpec desKeySpec = new DESKeySpec(desKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_KEY);
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        Cipher cipher = Cipher.getInstance(DES_KEY);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * DES解密
     * @param data
     * @param desKey
     * @return
     */
    @SneakyThrows
    public static byte[] decodeByDES(byte[] data, String desKey) {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKeySpec = new DESKeySpec(desKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_KEY);
        SecretKey key = keyFactory.generateSecret(desKeySpec);
        Cipher cipher = Cipher.getInstance(DES_KEY);
        cipher.init(Cipher.DECRYPT_MODE, key, random);
        return cipher.doFinal(data);
    }
}
