package com.fable.mssg.catalog.web;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.sipheader.QueryHeader;
import com.fable.mssg.utils.login.LoginUtils;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 15:55 2017/10/26 0026
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/")
public class MediaSourceController {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Setter
    public ChannelManager channelManager;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 媒体源目录推送
     *
     * @param deviceId  媒体源设备id
     * @param host      媒体源ip
     * @param port      媒体源端口
     * @param mediaName 媒体源名称
     */
    @RequestMapping("pushCatalogs")
    @SneakyThrows
    public void pushCatalogs(String deviceId, String host, String port, String mediaName) {

        MasterConfigProperties.Master master = masterConfigProperties.getMaster();
        QueryHeader queryHeader = new QueryHeader(host, Integer.parseInt(port), deviceId,
                master.getHost(), master.getPort(), master.getUser());
        SipMessage sipMessage = queryHeader.generateQueryHeader(); // 生成查询信令头信息
        /**
         * 发送目录查询消息
         */
        InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
        InetSocketAddress gatewayAddress = AddressUtils.from(host, Integer.parseInt(port)); // 网关ip和端口
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
        catalogBean.setAddress(host);
        catalogBean.setMediaDeviceId(deviceId);
        catalogBean.setCatalogType(1);
        EquipmentCatalogBean equipmentBean = equipmentCatalogService.getCatalogInfoByDeviceCode(deviceId);
        if (equipmentBean == null) { // 保存
            equipmentCatalogService.saveCatalogInfo(catalogBean);
        } else { // 更新
            equipmentCatalogService.updateCatalogInfoByDeviceCode(equipmentBean);
        }
    }

}
