package com.fable.mssg.resource.web;

import com.fable.mssg.utils.login.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.*;

/**
 * @author: yuhl Created on 15:21 2017/10/26 0026
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@Slf4j
@RequestMapping("/")
public class MediaResourceController {

    /**
     * 媒体源可用性测试
     * @param host 媒体源ip
     * @param port 媒体源端口
     * @return
     */
    @RequestMapping("mediaSourceConn")
    public String MediaSourceConn(String host, String port) {
        Socket socket = new Socket();

        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(host);
            datagramSocket.connect(address, Integer.valueOf(port));
            byte[] buffer = new byte[1024];
            buffer = ("test").getBytes();
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            DatagramPacket dp1 = new DatagramPacket(new byte[1024], 1024);
            socket.setSoTimeout(3000);
            datagramSocket.send(dp);
            datagramSocket.receive(dp1);
            datagramSocket.close();
        } catch (SocketTimeoutException e) {
            log.error("Socket Timeout Exception {} ", e);
            return "SUCCESS";
        } catch (Exception e) {
            log.error("Socket connect failed {}", e);
            return "FAIL";
        }
        return "SUCCESS";
    }
}
