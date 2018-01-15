package com.fable.mssg.catalog.handler;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.sipheader.MessageHeader;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.catalog.xml.query.CatalogItemXml;
import com.fable.mssg.catalog.xml.query.CatalogResponseXml;
import com.fable.mssg.catalog.xml.query.EquipmentInfoXml;
import com.fable.mssg.catalog.xml.query.ResponseDeviceListXml;
import com.fable.mssg.catalog.xml.subscribe.CatalogSubscribeXml;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.service.datasource.DataSourceService;
import com.fable.mssg.service.resource.MediaInfoService;
import com.fable.mssg.service.resource.ResSubscribeService;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.XmlContent;
import io.pkts.packet.sip.header.FromHeader;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: yuhl Created on 14:46 2017/11/14 0014
 */
@Slf4j
public class ResourceSharingHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    @Autowired
    public ResSubscribeService resSubscribeService;

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    ServiceRegistry registry;

    /**
     * 判断是否为上级平台的目录查询请求
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        return sipMessage.isMessage() && sipMessage.getRawContent().toString().contains("<Query>")
                && sipMessage.getRawContent().toString().contains("Catalog")
                && !sipMessage.toBuffer().toString().contains("fablesoft");
    }

    /**
     * 处理目录和设备查询请求
     *
     * @param sipMessage
     * @return
     */
    @Override
    @SneakyThrows
    public SipRouterInfo handle(SipMessage sipMessage) {
        MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
        String remoteHost = masterConfigProperties.getRemoteServer().getHost(); // 远程调用ip
        int remotePort = masterConfigProperties.getRemoteServer().getPort(); // 远程调用端口
        FromHeader fromHeader = sipMessage.getFromHeader();
        String sipId = SipUtils.getSipURI(fromHeader).getUser().toString(); // 获取sipId
        // 根据sipId查询媒体源信息，远程调用
        log.info("The third side platform device id : {}", sipId);
        List<DataSource> dataSources = registry.lookup(remoteHost, remotePort, true,
                ResSubscribeService.class).findDataSourceByMediaId(sipId);
        MediaInfo mediaInfo = registry.lookup(remoteHost, remotePort, true,
                MediaInfoService.class).findByDeviceId(sipId);
        log.info("Shared resource size : {}", dataSources.size());
        MessageHeader messageHeader = new MessageHeader(slave.getHost(), slave.getPort(), slave.getSlaveId());
        for (DataSource dataSource : dataSources) { // 循环发送每一条记录
            DataSource source = registry.lookup(remoteHost, remotePort, true, DataSourceService.class)
                    .findByResIdAndParent(dataSource.getRsId(), dataSource.getStandardParentId());
            String content = sipContent(dataSource, slave.getSlaveId(), dataSources.size(), sipMessage, source); // xml内容
            SipMessage response = (SipMessage) messageHeader.generateMessageHeader(mediaInfo, content);
            /**
             * 发送目录查询消息
             */
            InetSocketAddress masterAddress = AddressUtils.from(slave.getHost(), slave.getPort()); // master地址和端口
            InetSocketAddress gatewayAddress = AddressUtils.from(mediaInfo.getIpAddress(),
                    mediaInfo.getSessionPort()); // 网关ip和端口

            ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
            SipUtils.send(future, gatewayAddress, response);
        }
        return null;
    }

    /**
     * 实体类转为xml
     *
     * @param dataSource
     * @return
     */
    @SneakyThrows
    public static String sipContent(DataSource dataSource, String slaveId, int sumNum,
                                    SipMessage sipMessage, DataSource source) {
        String sipContent = null;
        XmlContent content = ((XmlContent) sipMessage.getContent());
        CatalogSubscribeXml subscribeXml = content.get(CatalogSubscribeXml.class);
        CatalogResponseXml responseXml = new CatalogResponseXml(); // Response封装
        ResponseDeviceListXml deviceListXml = new ResponseDeviceListXml(); // DeviceList封装
        List<CatalogItemXml> itemXmlList = new ArrayList<>(); // Item封装
        CatalogItemXml itemXml = new CatalogItemXml();
        EquipmentInfoXml infoXml = new EquipmentInfoXml(); // Info封装
        /**
         * 响应主体信息
         */
        responseXml.setDeviceId(slaveId);
        responseXml.setCmdType(subscribeXml.getCmdType());
        responseXml.setSn(subscribeXml.getSn());
        responseXml.setSumNum(sumNum);
        if (dataSource.getDsType().equals(5)) { // 设备
            infoXml.setPositionType(dataSource.getLocationType());
            infoXml.setPtzType(dataSource.getEquipType());
            itemXml.setManuFacturer(dataSource.getManuName());
            itemXml.setModel(dataSource.getModel());
            itemXml.setOwner(dataSource.getOwner());
            itemXml.setAddress(dataSource.getAddress());
            itemXml.setStatus(dataSource.getStatus());
        }
        itemXml.setDeviceId(dataSource.getId()); // 编码
        itemXml.setName(dataSource.getDsName()); // 名称
        itemXml.setCivilCode(dataSource.getCivilCode()); // 行政区域
        itemXml.setParentId(source == null ? null : source.getId()); // 父设备/区域/系统ID
        itemXml.setRegisterWay(dataSource.getRegisterWay() == null ? 0 : dataSource.getRegisterWay()); // 注册方式
        itemXml.setSafetyWay(dataSource.getSecrecy() == null ? 0 : dataSource.getSecrecy()); // 安全策略
        itemXml.setParental(dataSource.getParental() == null ? 0 : dataSource.getParental()); // 是否有子设备
        itemXml.setBlock(dataSource.getBlock()); // 警区
        itemXml.setBusGroupId(dataSource.getBusGroupId());
        itemXml.setIpAddress(dataSource.getIpAddress());
        itemXml.setLatitude(dataSource.getLat() == null ? 0 : dataSource.getLat());
        itemXml.setLongitude(dataSource.getLng() == null ? 0 : dataSource.getLng());
        itemXmlList.add(itemXml);
        deviceListXml.setItemXmlList(itemXmlList);
        responseXml.setDeviceListXml(deviceListXml);
        sipContent = SipConstant.convertToXml(responseXml);
        return sipContent;
    }

}
