package com.fable.mssg.catalog.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/**
 * @author: yuhl Created on 11:09 2017/11/16 0016
 */
@Slf4j
public class Base64Utils {

    /**
     * Base64编码
     * @param str
     * @return
     */
    public static String encodeByBase64(String str) {
        byte[] b = null;
        String result = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported encoding type {}", e);
        }
        if (b != null) { // 编码
            result = new BASE64Encoder().encode(b);
        }
        return result;
    }

    /**
     * Base64解码
     * @param str
     * @return
     */
    public static String decodeByBase64(String str) {
        byte[] b = null;
        String result = null;
        if (str != null) { // 解码
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(str);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                log.error("Error occured during BASE64 decoding {}", e);
            }
        }
        return result;
    }

}
