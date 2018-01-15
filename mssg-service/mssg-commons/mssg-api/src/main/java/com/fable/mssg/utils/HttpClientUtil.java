package com.fable.mssg.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <类描述>
 * <http工具类>
 *
 * @author wangshouyan
 * @version [版本号, 2016/12/07 9:34]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public class HttpClientUtil
{
    
    /**
     * httpClient的get请求方式
     *
     * @return
     * @throws Exception
     */
    public static String doGet(String url, String charset)
    {
        /*
         * 使用 GetMethod 来访问一个 URL 对应的网页,实现步骤: 1:生成一个 HttpClinet 对象并设置相应的参数。
         * 2:生成一个 GetMethod 对象并设置响应的参数。 3:用 HttpClinet 生成的对象来执行 GetMethod 生成的Get
         * 方法。 4:处理响应状态码。 5:若响应正常，处理 HTTP 响应内容。 6:释放连接。
         */
        
        /* 1 生成 HttpClinet 对象并设置参数 */
        HttpClient httpClient = new HttpClient();
        // 设置 Http 连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        /* 2 生成 GetMethod 对象并设置参数 */
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时为 5 秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
        // 设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        /* 3 执行 HTTP GET 请求 */
        try
        {
            int statusCode = httpClient.executeMethod(getMethod);
            /* 4 判断访问的状态码 */
            if (statusCode != HttpStatus.SC_OK)
            {
                // 请求出错
                return null;
            }
            /* 5 处理 HTTP 响应内容 */
            // HTTP响应头部信息，这里简单打印
            Header[] headers = getMethod.getResponseHeaders();
            String headerStr = "";
            for (Header h : headers)
            {
                if (StringUtils.isNotBlank(headerStr))
                {
                    headerStr = headerStr + "\\,h.getName()=" + h.getValue();
                }
                else
                {
                    headerStr = "h.getName()=" + h.getValue();
                }
            }
            // 读取 HTTP 响应内容，这里简单打印网页内容
            byte[] responseBody = getMethod.getResponseBody();
            if (StringUtils.isEmpty(charset))
            {
                charset = "UTF-8";
            }
            // 读取为字节数组
            response = new String(responseBody, charset);
            // 读取为 InputStream，在网页内容数据量大时候推荐使用
            // InputStream response = getMethod.getResponseBodyAsStream();
        }
        catch (HttpException e)
        {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            log.error("HttpException:{}", e);
        }
        catch (IOException e)
        {
            // 发生网络异常
            log.error("IOException:{}", e);
        }
        finally
        {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }
    
    /**
     * 发送HTTPS请求
     *
     * @param url
     * @return
     */
    public static String doGetWithSSL(String url, String charset)
    {
        
        String content = null;
        try
        {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            
            X509TrustManager tm = new X509TrustManager()
            {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException
                {
                }
                
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException
                {
                }
                
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
                
            };
            
            sslcontext.init(null, new TrustManager[] {tm}, null);
            
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
            
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(httpGet);
            HttpEntity entity = closeableHttpResponse.getEntity();
            
            if (StringUtils.isEmpty(charset))
            {
                charset = "UTF-8";
            }
            content = EntityUtils.toString(entity, charset);
        }
        catch (Exception e)
        {
            log.error("Exception:{}", e);
        }
        
        return content;
    }
    
    public static String doPost(String url, Map<String, Object> reqContent, Map<String, String> header, String contentType)
    {
        return doPost(url, JSONObject.toJSONString(reqContent), header, contentType, "");
    }
    
    public static String doPost(String url, Map<String, Object> reqContent, Map<String, String> header)
    {
        return doPost(url, JSONObject.toJSONString(reqContent), header, "", "");
    }
    
    public static String doPost(String url, Map<String, Object> reqContent)
    {
        return doPost(url, JSONObject.toJSONString(reqContent), null, "", "");
    }
    
    /**
     * 采用默认utf-8编码
     * @param url
     * @param reqContent
     * @param header
     * @param contentType
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String doPost(String url, String reqContent, Map<String, String> header, String contentType)
    {
        return doPost(url, reqContent, header, contentType, "");
    }
    
    public static String doPost(String url, String reqContent, Map<String, String> header)
    {
        return doPost(url, reqContent, header, "", "");
    }
    
    public static String doPost(String url, String reqContent)
    {
        return doPost(url, reqContent, null, "", "");
    }
    
    /**
     * post请求
     * @param url 请求的地址
     * @param reqContent 请求内容
     * @param header 消息头
     * @param contentType 请求的类型，默认 application/json
     * @param charset 字符编码，默认为utf-8
     * @return 异常，超时返回 ""
     * @see [类、类#方法、类#成员]
     */
    public static String doPost(String url, String reqContent, Map<String, String> header, String contentType, String charset)
    {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        
        //设置消息头
        if (0!=header.size())
        {
            for (Entry<String, String> entry : header.entrySet())
            {
                post.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        //返回值
        String result = "";
        
        StringEntity s = null;
        try
        {
            //设置请求参数，json格式
            s = new StringEntity(reqContent);
            
            //设置字符编码
            if (StringUtils.isNotEmpty(charset))
            {
                s.setContentEncoding(charset);
            }
            else
            {
                s.setContentEncoding("UTF-8");
            }
            
            //发送内容类型 contentType
            if (StringUtils.isNotBlank(contentType))
            {
                s.setContentType(contentType);
            }
            else
            {
                s.setContentType("application/json");
            }
            post.setEntity(s);
            
            //返回参数
            HttpResponse httpResponse = client.execute(post);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            
            //返回的状态200
            if (statusCode == HttpStatus.SC_OK)
            {
                HttpEntity entity = httpResponse.getEntity();
                // 获得返回数据
                result = EntityUtils.toString(entity, "UTF-8");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("UnsupportedEncodingException:{}", e);
        }
        catch (ClientProtocolException e)
        {
            log.error("ClientProtocolException:{}", e);
        }
        catch (IOException e)
        {
            log.error("IOException:{}", e);
        }
        
        return result;
    }
    
    /**
     * 解析soapXML
     * 
     * 
     * @param soapXML  需要解析的XML返回报文
     * @return Iterator  格式化数据
     */
    @SuppressWarnings("unchecked")
    public static Iterator<SOAPElement> parseSoapMessage(String soapXML)
    {
        try
        {
            SOAPMessage msg = formatSoapString(soapXML);
            SOAPBody body = msg.getSOAPBody();
            return body.getChildElements();
        }
        catch (SOAPException e)
        {
            log.error("SOAPException:{}", e);
        }
        catch (Exception e)
        {
            log.error("Exception:{}", e);
        }
        
        return null;
    }
    
    /**
     * 把soap字符串格式化为SOAPMessage
     * 
     * @param soapString:
     *            soap字符串
     * @return
     */
    private static SOAPMessage formatSoapString(String soapString)
    {
        MessageFactory msgFactory;
        try
        {
            msgFactory = MessageFactory.newInstance();
            ByteArrayInputStream stream = new ByteArrayInputStream(soapString.getBytes("UTF-8"));
            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), stream);
            reqMsg.saveChanges();
            
            return reqMsg;
        }
        catch (SOAPException e)
        {
            log.error("SOAPException:{}", e);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("UnsupportedEncodingException:{}", e);
        }
        catch (IOException e)
        {
            log.error("IOException:{}", e);
        }
        
        return null;
    }
    
}
