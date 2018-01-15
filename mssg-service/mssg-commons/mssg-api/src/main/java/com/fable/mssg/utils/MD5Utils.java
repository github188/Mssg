package com.fable.mssg.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: yuhl Created on 10:48 2017/11/14 0014
 */
@Slf4j
public class MD5Utils {

    /**
     * MD5加密处理
     * @param str
     * @return
     */
    public static String getMD5Value(String str) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // 指定加密方式
            byte[] messageByte = str.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);
            md5str = bytesToHex(md5Byte); // 十六进制转为二进制
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm {}", e);
        } catch (UnsupportedEncodingException ex) {
            log.error("Unsupported encoding type {}", ex);
        }
        return md5str;
    }

    /**
     * 二进制转为十六进制
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if(num < 0) {
                num += 256;
            }
            if(num < 16){
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

//    public static void main(String[] args){
//
//        System.out.println(getMD5Value("multimedia&security*server@gateway"));
//    }
}
