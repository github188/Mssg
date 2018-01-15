package com.fable.mssg.catalog.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.sipheader.QueryHeader;
import com.fable.mssg.catalog.sipheader.RegisterHeader;
import com.fable.mssg.service.equipment.MasterResourceService;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 11:21 2017/12/8 0008
 */
@Exporter(interfaces = MasterResourceService.class)
public class MasterResourceServiceImpl implements MasterResourceService {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Autowired
    public ChannelManager channelManager;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

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
        MasterConfigProperties.Master master = masterConfigProperties.getMaster();
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
     * 向slave上级发送注册信令
     *
     * @param deviceId
     * @param serverIp
     * @param serverPort
     */
    @Override
    public void sendRegiterSip(String deviceId, String serverIp, int serverPort) {
        MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
        RegisterHeader registerHeader = new RegisterHeader(serverIp, serverPort, deviceId,
                slave.getHost(), slave.getPort(), slave.getSlaveId());
        SipMessage sipMessage = registerHeader.generateRegisterHeader(); // 生成查询信令头信息

        /**
         * 发送注册消息
         */
        InetSocketAddress masterAddress = AddressUtils.from(slave.getHost(), slave.getPort()); // slave地址和端口
        InetSocketAddress gatewayAddress = AddressUtils.from(serverIp, serverPort); // 网关ip和端口
        ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
        SipUtils.send(future, gatewayAddress, sipMessage);
    }
}
