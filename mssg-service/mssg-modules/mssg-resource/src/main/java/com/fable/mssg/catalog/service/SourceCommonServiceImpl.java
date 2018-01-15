package com.fable.mssg.catalog.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.repository.MediaSourceRepository;
import com.fable.mssg.catalog.sipheader.QueryHeader;
import com.fable.mssg.config.CommonConfigProperties;
import com.fable.mssg.service.mediainfo.SourceCommonService;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.net.*;

/**
 * @author: yuhl Created on 11:09 2017/10/27 0027
 */

@Exporter(interfaces = SourceCommonService.class)
@EnableConfigurationProperties(CommonConfigProperties.class)
@Service
@Slf4j
public class SourceCommonServiceImpl implements SourceCommonService {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Setter
    public ChannelManager channelManager;

    //@Setter
    public CommonConfigProperties commonConfigProperties;

    @Autowired
    private MediaSourceRepository mediaSourceRepository;

    /**
     * 媒体源可用性检测
     *
     * @param serverIp   媒体源ip
     * @param serverPort 媒体源端口
     * @return
     */
    @Override
    public boolean mediaSourceConn(String serverIp, int serverPort) {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverIp);
            datagramSocket.connect(address, Integer.valueOf(serverPort));
            byte[] buffer = new byte[1024];
            buffer = ("test").getBytes();
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            DatagramPacket dp1 = new DatagramPacket(new byte[1024], 1024);
            datagramSocket.setSoTimeout(2000);
            datagramSocket.send(dp);
            datagramSocket.receive(dp1);
            datagramSocket.close();
        } catch (SocketTimeoutException e) {
            log.error("Socket Timeout Exception {} ", e);
            return true;
        } catch (Exception e) {
            log.error("Socket connect failed {}", e);
            return false;
        }
        return true;
    }

    /**
     * 发送媒体源目录查询接口
     *
     * @param deviceId   媒体源设备id
     * @param serverIp   媒体源ip
     * @param serverPort 媒体源端口
     * @param mediaName  媒体源名称
     */
    @Override
    @SneakyThrows
    public void sendCatalogQuerySip(String deviceId, String serverIp, int serverPort, String mediaName) {
        CommonConfigProperties.Master master = commonConfigProperties.getMaster();
        QueryHeader queryHeader = new QueryHeader(serverIp, serverPort, deviceId,
                master.getHost(), master.getPort(), master.getUser());
        SipMessage sipMessage = queryHeader.generateQueryHeader(); // 生成查询信令头信息
        /**
         * 发送目录查询消息
         */
        InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
        InetSocketAddress gatewayAddress = AddressUtils.from(serverIp, serverPort); // 网关ip和端口
        ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
        SipUtils.send(future, gatewayAddress, sipMessage);
        /**
         * 保存联网网关信息
         * 不存在则保存
         * 存在则更新
         */
        EquipmentCatalogBean catalogBean = new EquipmentCatalogBean();
        catalogBean.setDeviceId(deviceId);
        catalogBean.setCatalogName(mediaName);
        catalogBean.setAddress(serverIp);
        catalogBean.setMediaDeviceId(deviceId);
        catalogBean.setCatalogType(1);
        EquipmentCatalogBean equipmentBean = equipmentCatalogService.getCatalogInfoByDeviceCode(deviceId);
        if (equipmentBean == null) { // 保存
            equipmentCatalogService.saveCatalogInfo(catalogBean);
        } else { // 更新
            equipmentCatalogService.updateCatalogInfoByDeviceCode(equipmentBean);
        }
    }

    /**
     * 根据设备id删除媒体源目录
     *
     * @param mediaDeviceId 媒体源设备目录
     * @return
     */
    @Override
    public int deleteOriginalDs(String mediaDeviceId) {
        return mediaSourceRepository.deleteCatalogByMediaDeviceId(mediaDeviceId);
    }

}
