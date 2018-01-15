package com.fable.mssg.catalog.schedule;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.sipheader.KeepaliveHeader;
import com.fable.mssg.catalog.sipheader.RegisterHeader;
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
 * @author: yuhl Created on 9:56 2017/12/1 0001 slave侧定时任务
 */
@Slf4j
public class SlaveSignalingSchedule implements SchedulingConfigurer {
    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Autowired
    public ChannelManager channelManager;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 向升级平台发送注册信令
     */
    private class LowerLevelRegister implements Runnable {
        @Override
        public void run() {
            MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                String targetUser = bean.getDeviceId(); // 设备id
                String gateWayHost = bean.getIpAddress(); // 网关地址
                int gateWayPort = bean.getSessionPort(); // 网关端口
                int mediaType = bean.getMediaType(); // 1-上级 2-下级
                if (mediaType == 1) { // 如果是上级则发送注册信令
                    log.info("发送注册信令开始");
                    RegisterHeader registerHeader = new RegisterHeader(gateWayHost, gateWayPort, targetUser,
                            slave.getHost(), slave.getPort(), slave.getSlaveId());
                    SipMessage sipMessage = registerHeader.generateRegisterHeader(); // 生成查询信令头信息

                    /**
                     * 发送注册消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(slave.getHost(), slave.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口
                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    log.info("client绑定");
                    SipUtils.send(future, gatewayAddress, sipMessage);
                    log.info("发送注册信息结束");
                }
            }
        }

    }

    /**
     * 定时保活信息
     */
    private class SlaveKeepalive implements Runnable {
        @SneakyThrows(JAXBException.class)
        @Override
        public void run() {
            MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
            List<MediaInfoBean> mediaList = equipmentCatalogService.findAllMediaInfo();
            for (MediaInfoBean bean : mediaList) {
                String targetUser = bean.getDeviceId(); // 设备id
                String gateWayHost = bean.getIpAddress(); // 网关地址
                int gateWayPort = bean.getSessionPort(); // 网关ip
                int mediaType = bean.getMediaType(); // 1-上级 2-下级
                if (mediaType == 1) { // 如果是上级则发送保活信令
                    log.info("定时保活任务开始");
                    KeepaliveHeader keepaliveHeader = new KeepaliveHeader(gateWayHost, gateWayPort, targetUser,
                            slave.getHost(), slave.getPort(), slave.getSlaveId());
                    SipMessage sipMessage = keepaliveHeader.generateKeepaliveHeader(); // 生成保活信令
                    /**
                     * 发送订阅消息
                     */
                    InetSocketAddress masterAddress = AddressUtils.from(slave.getHost(), slave.getPort()); // master地址和端口
                    InetSocketAddress gatewayAddress = AddressUtils.from(gateWayHost, gateWayPort); // 网关ip和端口
                    ChannelFuture future = channelManager.selectUdpChannel(null, masterAddress, null);
                    log.info("client绑定");
                    SipUtils.send(future, gatewayAddress, sipMessage);
                    log.info("定时保活任务结束");
                }
            }
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
        IntervalTask registerTask = new IntervalTask(new LowerLevelRegister(),
                schedule.getRegInterval(), schedule.getRegDelay());
        // 保活信令任务
        IntervalTask keepaliveTask = new IntervalTask(new SlaveKeepalive(),
                schedule.getKeepaliveInterval(), schedule.getKeepaliveDelay());
        scheduledTaskRegistrar.addFixedRateTask(registerTask);
        scheduledTaskRegistrar.addFixedRateTask(keepaliveTask);
    }
}
