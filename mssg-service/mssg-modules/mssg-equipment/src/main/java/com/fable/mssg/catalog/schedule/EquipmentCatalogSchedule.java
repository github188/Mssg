package com.fable.mssg.catalog.schedule;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.sipheader.KeepaliveHeader;
import com.fable.mssg.catalog.sipheader.QueryHeader;
import com.fable.mssg.catalog.sipheader.RegisterHeader;
import com.fable.mssg.catalog.sipheader.SubscribeHeader;
import com.fable.mssg.domain.equipment.MediaInfoBean;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.xml.bind.JAXBException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author : yuhl 2017-09-01
 */
@Slf4j
public class EquipmentCatalogSchedule implements SchedulingConfigurer {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Autowired
    public ChannelManager channelManager;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 设备注册信令
     */
    private class EquipmentRegister implements Runnable {
        @Override
        public void run() {
            log.info("发送注册信息开始");
            MasterConfigProperties.Master master = masterConfigProperties.getMaster();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                if (bean.getMediaType() == 2) { // 下级网关且非2016国标
                    String targetUser = bean.getDeviceId(); // 设备id
                    String gateWayHost = bean.getIpAddress(); // 网关地址
                    int gateWayPort = bean.getSessionPort(); // 网关端口
                    RegisterHeader registerHeader = new RegisterHeader(gateWayHost, gateWayPort, targetUser,
                            master.getHost(), master.getPort(), master.getUser());
                    SipMessage sipMessage = registerHeader.generateRegisterHeader(); // 生成查询信令头信息

                    /**
                     * 发送注册消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口
                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    SipUtils.send(future, gatewayAddress, sipMessage);
                }

            }
            log.info("发送注册信息结束");
        }

    }

    /**
     * 定时查询设备目录
     *
     * @throws JAXBException
     */
    private class QueryCatalogInfo implements Runnable {
        @SneakyThrows(JAXBException.class)
        @Override
        public void run() {
            log.info("定时查询任务开始");
            MasterConfigProperties.Master master = masterConfigProperties.getMaster();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                String targetUser = bean.getDeviceId(); // 设备id
                String gateWayHost = bean.getIpAddress(); // 网关地址
                int gateWayPort = bean.getSessionPort(); // 网关端口
                int mediaType = bean.getMediaType(); // 1-上级 2-下级
                if (mediaType == 2) { // 如果是下级则发送目录查询信令
                    QueryHeader queryHeader = new QueryHeader(gateWayHost, gateWayPort, targetUser,
                            master.getHost(), master.getPort(), master.getUser());
                    SipMessage sipMessage = queryHeader.generateQueryHeader(); // 生成查询信令头信息
                    /**
                     * 发送目录查询消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口

                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    SipUtils.send(future, gatewayAddress, sipMessage);
                    /**
                     * 保存联网网关信息
                     * 不存在则保存
                     * 存在则更新
                     */
                    EquipmentCatalogBean catalogBean = new EquipmentCatalogBean();
                    catalogBean.setDeviceId(targetUser);
                    catalogBean.setCatalogName(bean.getMediaName());
                    catalogBean.setAddress(gateWayHost);
                    catalogBean.setMediaDeviceId(targetUser);
                    catalogBean.setCatalogType(1);
                    EquipmentCatalogBean equipmentBean = equipmentCatalogService.getCatalogInfoByDeviceCode(targetUser);
                    if (equipmentBean == null) { // 保存
                        equipmentCatalogService.saveCatalogInfo(catalogBean);
                    } else { // 更新
                        equipmentCatalogService.updateCatalogInfoByDeviceCode(equipmentBean);
                    }
                }
            }
            log.info("定时查询任务结束");
        }

    }

    /**
     * 定时订阅目录信息
     */
    public class SubscribeCatalog implements Runnable {
        @SneakyThrows(JAXBException.class)
        @Override
        public void run() {
            MasterConfigProperties.Master master = masterConfigProperties.getMaster();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                String targetUser = bean.getDeviceId(); // 设备id
                String gateWayHost = bean.getIpAddress(); // 网关地址
                int gateWayPort = bean.getSessionPort(); // 网关ip
                int mediaType = bean.getMediaType(); // 1-上级 2-下级
                if (mediaType == 2) { // 如果是下级发送目录订阅信令
                    log.info("定时订阅任务开始");
                    SubscribeHeader subscribeHeader = new SubscribeHeader(gateWayHost, gateWayPort, targetUser,
                            master.getHost(), master.getPort(), master.getUser(), master.getExpires());
                    SipMessage sipMessage = subscribeHeader.generateSubscribeHeader(); // 生成订阅信令
                    /**
                     * 发送订阅消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口
                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    SipUtils.send(future, gatewayAddress, sipMessage);
                    log.info("定时订阅任务结束");
                }
            }
        }

    }

    /**
     * 定时保活信息
     */
    private class MasterKeepalive implements Runnable {
        @SneakyThrows(JAXBException.class)
        @Override
        public void run() {
            log.info("定时保活任务开始");
            MasterConfigProperties.Master master = masterConfigProperties.getMaster();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                if (bean.getMediaType() == 2) { // 下级网关且非2016国标
                    String targetUser = bean.getDeviceId(); // 设备id
                    String gateWayHost = bean.getIpAddress(); // 网关地址
                    int gateWayPort = bean.getSessionPort(); // 网关ip
                    KeepaliveHeader keepaliveHeader = new KeepaliveHeader(gateWayHost, gateWayPort, targetUser,
                            master.getHost(), master.getPort(), master.getUser());
                    SipMessage sipMessage = keepaliveHeader.generateKeepaliveHeader(); // 生成保活信令
                    /**
                     * 发送订阅消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口
                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    SipUtils.send(future, gatewayAddress, sipMessage);
                }

            }
            log.info("定时保活任务结束");
        }

    }

    /**
     * 设置定时任务的执行策略
     *
     * @param scheduledTaskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        MasterConfigProperties.Schedule schedule = masterConfigProperties.getSchedule();
        // 注册信令任务
//        IntervalTask registerTask = new IntervalTask(new EquipmentRegister(),
//                schedule.getRegInterval(), schedule.getRegDelay());
        // 保活信令任务
//        IntervalTask keepaliveTask = new IntervalTask(new MasterKeepalive(),
//                schedule.getKeepaliveInterval(), schedule.getKeepaliveDelay());
        // 目录查询信令任务
        IntervalTask queryTask = new IntervalTask(new QueryCatalogInfo(),
                schedule.getQInterval(), schedule.getQDelay());
        // 订阅信令任务
        IntervalTask subscribeTask = new IntervalTask(new SubscribeCatalog(),
                schedule.getSubInterval(), schedule.getSubDelay());
//        scheduledTaskRegistrar.addFixedRateTask(registerTask);
//        scheduledTaskRegistrar.addFixedRateTask(keepaliveTask);
        scheduledTaskRegistrar.addFixedRateTask(queryTask);
        scheduledTaskRegistrar.addFixedRateTask(subscribeTask);
    }

}
