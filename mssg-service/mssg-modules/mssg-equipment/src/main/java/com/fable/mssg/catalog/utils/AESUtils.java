package com.fable.mssg.catalog.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: yuhl Created on 10:28 2017/11/15 0015
 */
public class AESUtils {

    private final static String AES_KEY = "AES"; // 加密算法

    /**
     * AES加密
     * @param data
     * @param aesKey
     * @return
     */
    @SneakyThrows
    public static byte[] encodeByAES(byte[] data, String aesKey) {
        SecretKey key = new SecretKeySpec(aesKey.getBytes(), AES_KEY);
        Cipher cipher = Cipher.getInstance(AES_KEY);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * AES解密
     * @param data
     * @param aesKey
     * @return
     */
    @SneakyThrows
    public static byte[] decodeByAES(byte[] data, String aesKey) {
        SecretKey key = new SecretKeySpec(aesKey.getBytes(), AES_KEY);
        Cipher cipher = Cipher.getInstance(AES_KEY);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
