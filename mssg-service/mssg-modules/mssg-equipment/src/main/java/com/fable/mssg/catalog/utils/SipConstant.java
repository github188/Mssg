package com.fable.mssg.catalog.utils;

import com.fable.framework.proxy.sip.util.SipUtils;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.address.SipURI;
import io.pkts.packet.sip.header.FromHeader;
import io.pkts.packet.sip.header.ToHeader;
import io.pkts.packet.sip.header.ViaHeader;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: yuhl Created on 10:02 2017/11/14 0014
 */
public class SipConstant {

    public static final int OK_CMD = 200; // 200 OK

    public static final int UNAUTH_CMD = 401; // 401 unauthorized

    public static ConcurrentHashMap<String, String> SESSION_MAP = new ConcurrentHashMap();

    public static long CSEQ = 0;

    /**
     * 获取当前日期-年月日
     * @return
     */
    public static String currentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        return currentDate;
    }

    /**
     * 获取当前时间-时分秒
     * @return
     */
    public static String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = simpleDateFormat.format(new Date());
        return currentTime;
    }

    /**
     * 获取当前时间-年月日 时分秒
     * @return
     */
    public static String current() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = simpleDateFormat.format(new Date());
        return current;
    }

    /**
     * 获取当前时间-年月日 时分秒.毫秒
     * @return
     */
    public static String millionTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        String current = simpleDateFormat.format(new Date());
        return current;
    }

    /**
     * 日期格式年月日时分:秒
     * @return
     */
    public static String date() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm:ss");
        String current = simpleDateFormat.format(new Date());
        return current;
    }

    /**
     * 日期格式年月日时:分
     * @return
     */
    public static String especialDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH:mm");
        String current = simpleDateFormat.format(new Date());
        return current;
    }

    /**
     * 计算两个时间差值-单位:秒
     * 日志格式yyyy-MM-dd HH:mm:ss
     * @param current
     * @param history
     * @return
     */
    @SneakyThrows
    public static long minusCommonTime(String current, String history) {
        long minus = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = simpleDateFormat.parse(current); // 当前时间
        Date historyDate = simpleDateFormat.parse(history); // 历史时间
        minus = (currentDate.getTime() - historyDate.getTime()) / 1000;
        return minus;
    }

    /**
     * 计算时间差值-单位:秒
     * 日志格式yyyyMMddHHmm:ss
     * @param current
     * @param history
     * @return
     */
    @SneakyThrows
    public static long minusTime(String current, String history) {
        long minus = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm:ss");
        Date currentDate = simpleDateFormat.parse(current); // 当前时间
        Date historyDate = simpleDateFormat.parse(history); // 历史时间
        minus = (currentDate.getTime() - historyDate.getTime()) / 1000;
        return minus;
    }

    /**
     * 计算时间差值-单位:秒
     * 日期格式yyyyMMddHH:mm
     * @param current
     * @param history
     * @return
     */
    @SneakyThrows
    public static long minusEspecialTime(String current, String history) {
        long minus = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH:mm");
        Date currentDate = simpleDateFormat.parse(current); // 当前时间
        Date historyDate = simpleDateFormat.parse(history); // 历史时间
        minus = (currentDate.getTime() - historyDate.getTime()) / 1000;
        return minus;
    }

    /**
     * 截取双引号之间的内容
     * @param str
     * @return
     */
    public static String getMiddleStr(String str) {
        String regex = "\"(.*)\""; // 指定格式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }

    /**
     * 生成数字摘要认证响应信令的header
     * @param sipMessage
     * @param host
     * @param port
     * @param sipId
     * @return
     */
    public static SipMessage generateHeader(SipMessage sipMessage, SipRequest sipRequest,
                                            String host, int port, String sipId) {
        SipURI sipURI = SipUtils.getSipURI(sipMessage.getFromHeader());
        final FromHeader fromHeader = FromHeader.with().user(sipId).host(host).port(port)
                .parameter("tag", sipMessage.getFromHeader().getTag().toString()).build();
        final ToHeader toHeader = ToHeader.with().user(sipURI.getUser().toString()).build();
        final ViaHeader viaHeader = ViaHeader.with().host(host).port(port).branch(sipMessage.getViaHeader()
                .getBranch().toString()).transport(sipMessage.getViaHeader().getTransport()).build();
        final SipRequest request = SipRequest.request(Buffers.wrap(sipMessage.getMethod().toString()),
                "sip:" + sipURI.getUser().toString() + "@" + sipURI.getHost().toString()
                        + ":" + sipURI.getPort()).from(fromHeader).via(viaHeader).to(toHeader)
                .callId(sipMessage.getCallIDHeader()).cseq(sipMessage.getCSeqHeader())
                .contact(sipMessage.getContactHeader()).build();
        return request;
    }

    /**
     * pojo转换成xml
     *
     * @param obj 待转化的对象
     * @return xml格式字符串
     * @throws Exception JAXBException
     */
    public static String convertToXml(Object obj) throws Exception {
        String result = null;
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        // 指定是否使用换行和缩排对已编组 XML 数据进行格式化的属性名称。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        result = writer.toString();
        return result;
    }

    /**
     * 十六进制转为二进制
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString){
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++){
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 二进制转为十六进制
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString){
        if (bString == null || bString.equals("") || bString.length() % 8 != 0){
            return null;
        }
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4){
            iTmp = 0;
            for (int j = 0; j < 4; j++){
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 二进制转为十进制
     * @param str
     * @return
     */
    public static int binaryToDecimal(String str){
        int sum = 0;
        int a;
        for(int i = str.length()-1; i >= 0; i--){
            a = str.charAt(i) - 48;
            if(a != 0 && a != 1) {
                throw new ArithmeticException("NOT A BINARY");
            }
            sum = (int) (sum + a * Math.pow(2,str.length() - 1 - i));
        }
        return sum;
    }

    /**
     * 字节数组转16进制字符串
     * @param data
     * @return
     */
    public static String bytesToHexString(byte[] data){
        StringBuilder buf = new StringBuilder(data.length * 2);
        for(byte b : data) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

}
