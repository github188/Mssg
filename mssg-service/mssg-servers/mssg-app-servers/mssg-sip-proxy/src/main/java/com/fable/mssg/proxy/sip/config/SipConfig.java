package com.fable.mssg.proxy.sip.config;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.proxy.MessageFilter;
import com.fable.framework.proxy.sip.DeviceInfo;
import com.fable.framework.proxy.sip.DeviceManager;
import com.fable.framework.proxy.sip.config.ProxyInitializer;
import com.fable.framework.proxy.sip.config.SipProxyConfiguration;
import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.handler.SipProxyHandlerAdapter;
import com.fable.framework.proxy.sip.media.MediaService;
import com.fable.framework.proxy.sip.server.SipUdpMessageServer;
import com.fable.mssg.bean.DataSourceInfo;
import com.fable.mssg.catalog.handler.*;
import com.fable.mssg.catalog.schedule.EquipmentCatalogSchedule;
import com.fable.mssg.catalog.schedule.SlaveSignalingSchedule;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.proxy.sip.codec.DecipheringDecoder;
import com.fable.mssg.proxy.sip.codec.EncryptEncoder;
import com.fable.mssg.proxy.sip.filter.MediaMultiplexFilter;
import com.fable.mssg.proxy.sip.filter.SipAuthenticationFilter;
import com.fable.mssg.service.datasource.DataSourceAuthService;
import com.fable.mssg.service.equipment.SecurityService;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.utils.HttpsURLConnectionUtilX;
import com.google.common.collect.Lists;
import io.pkts.packet.sip.SipMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
@Configuration
@EnableConfigurationProperties(SipProxyProperties.class)
@AutoConfigureAfter(SipProxyConfiguration.class)
@Slf4j
public class SipConfig {

    private final CacheManager cacheManager;
    private SipProxyProperties proxyProperties;

    private ServiceRegistry serviceRegistry;


    public SipConfig(SipProxyProperties proxyProperties, ServiceRegistry serviceRegistry, CacheManager cacheManager) {
        this.proxyProperties = proxyProperties;
        this.serviceRegistry = serviceRegistry;
        this.cacheManager = cacheManager;
    }

    @Value("${serverself.ssl.enabled}")
    private boolean isSSL;

    @Value("${com.fable.mssg.equipment.master.user}")
    private String masterUser;

    @Bean(name = "sipMessageHandlers")
    public List<SipMessageHandler<SipMessage>> requestHandlers() {
        if (isMaster()) {
            return Lists.newArrayList(
                    //master
                    catalogNotifyHandler(),
                    catalogSubscribeHandler(),
                    equipmentCatalogHandler(),
                    masterRegisterHandler()
            );
        } else {
            return Lists.newArrayList(
                    //slave
                    clientRegisterHandler(),
                    resourceSharingHandler(),
                    slaveSubscribeHandler()
            );
        }
    }

    @Bean
    public ProxyInitializer proxyInitializer(SecurityService securityService) {
        EncryptEncoder encryptEncoder = encryptEncoder(securityService);
        DecipheringDecoder decipheringDecoder = decipheringDecoder(securityService);
        boolean encryptEnabled = proxyProperties.isEncryptEnabled();
        boolean authEnabled = proxyProperties.isAuthEnabled();

        return new ProxyInitializer() {
            @Override
            public void customizeForwardServer(SipUdpMessageServer sipForwardServer) {
                if (!isMaster()) {
                    if (encryptEnabled) {
                        sipForwardServer.setCustomHandlers(decipheringDecoder);
                    }
                } else {
                    SipProxyHandlerAdapter handlerAdapter = sipForwardServer.getProxyHandlerAdapter();
                    if (encryptEnabled) {
                        handlerAdapter.setClientHandlers(encryptEncoder);
                    }
                    if (authEnabled) {
                        MessageFilter[] messageFilters = new MessageFilter[2];
                        messageFilters[0] = authenticationFilter(securityService);
                        messageFilters[1] = mediaMultiplexFilter();
                        handlerAdapter.setMessageFilters(Lists.newArrayList(messageFilters));
                    }
                }
            }

            @Override
            public void customizeBackwardServer(SipUdpMessageServer sipBackwardServer) {
                if (isMaster()) {
                    SipProxyHandlerAdapter handlerAdapter = sipBackwardServer.getProxyHandlerAdapter();
                    if (encryptEnabled) {
                        handlerAdapter.setClientHandlers(encryptEncoder);
                    }
                } else {
                    if (encryptEnabled) {
                        sipBackwardServer.setCustomHandlers(decipheringDecoder);
                    }
                }
            }
        };
    }

    @Bean
    public SipAuthenticationFilter authenticationFilter(SecurityService securityService) {
        SipAuthenticationFilter authenticationFilter = new SipAuthenticationFilter();
        authenticationFilter.setSslEnabled(isSSL);
        authenticationFilter.setDataSourceAuthService(dataSourceAuthService());
        authenticationFilter.setMaster(isMaster());
        authenticationFilter.setSecurityService(securityService);
        return authenticationFilter;
    }

    @Bean
    public MediaMultiplexFilter mediaMultiplexFilter(){
        MediaMultiplexFilter mediaMultiplexFilter = new MediaMultiplexFilter();
        mediaMultiplexFilter.setSslEnabled(isSSL);
        mediaMultiplexFilter.setMediaService(getMedisService());
        mediaMultiplexFilter.setCacheManager(cacheManager);
        return mediaMultiplexFilter;
    }

    private boolean isMaster() {
        boolean master = proxyProperties.isMaster();
        log.info("Is master {} ", master);
        return master;
    }

    @Bean
    public DeviceManager deviceManager(DataSourceAuthService dataSourceAuthService) {
        return deviceId -> {
            DeviceInfo deviceInfo = null;
            DataSourceInfo dataSourceInfo = dataSourceAuthService.getDataSourceInfo(deviceId);
            if (dataSourceInfo != null) {
                deviceInfo = new DeviceInfo();
                deviceInfo.setAddress(
                        new InetSocketAddress(dataSourceInfo.getMediaIp(), dataSourceInfo.getMediaPort())
                );
                deviceInfo.setDeviceId(dataSourceInfo.getDsCode());
                deviceInfo.setSipId(masterUser);
            }
            return deviceInfo;
        };
    }

    @Bean
    @SneakyThrows
    public DataSourceAuthService dataSourceAuthService() {
        Address remoteAddress = proxyProperties.getRemoteServer();
        return serviceRegistry.lookup(remoteAddress.getHost(), remoteAddress.getPort(), isSSL, DataSourceAuthService.class);
    }

    @Bean
    @SneakyThrows
    public MediaService getMedisService(){
        Address mediaServer = proxyProperties.getMediaServer();
        return serviceRegistry.lookup(mediaServer.getHost(), mediaServer.getPort(), false, MediaService.class);
    }

    /**
     * 如果是master则初始化定时任务
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "com.fable.mssg.proxy.sip.schedule", havingValue = "1")
    public EquipmentCatalogSchedule catalogSchedule() {
        return new EquipmentCatalogSchedule();
    }

    /**
     * 如果是slave则初始化定时任务
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "com.fable.mssg.proxy.sip.schedule", havingValue = "0")
    public SlaveSignalingSchedule signalingSchedule() {
        return new SlaveSignalingSchedule();
    }

    /**
     * 目录订阅通知处理
     *
     * @return
     */
    @Bean
    public CatalogNotifyHandler catalogNotifyHandler() {
        return new CatalogNotifyHandler();
    }

    /**
     * 目录订阅处理
     *
     * @return
     */
    @Bean
    public CatalogSubscribeHandler catalogSubscribeHandler() {
        return new CatalogSubscribeHandler();
    }

    /**
     * 目录查询处理
     *
     * @return
     */
    @Bean
    public EquipmentCatalogHandler equipmentCatalogHandler() {
        EquipmentCatalogHandler equipmentCatalogHandler = new EquipmentCatalogHandler();
        equipmentCatalogHandler.setCacheManager(cacheManager);
        return equipmentCatalogHandler;
    }

    /**
     * master注册处理
     *
     * @return
     */
    @Bean
    public MasterRegisterHandler masterRegisterHandler() {
        return new MasterRegisterHandler();
    }

    /**
     * 客户端注册
     *
     * @return
     */
    @Bean
    public ClientRegisterHandler clientRegisterHandler() {
        return new ClientRegisterHandler();
    }

    /**
     * 资源共享
     *
     * @return
     */
    @Bean
    public ResourceSharingHandler resourceSharingHandler() {
        return new ResourceSharingHandler();
    }

    /**
     * 第三方平台订阅
     *
     * @return
     */
    @Bean
    public SlaveSubscribeHandler slaveSubscribeHandler() {
        return new SlaveSubscribeHandler();
    }

    @Bean
    @ConditionalOnProperty(value = "serverself.ssl.enabled", havingValue = "true")
    public HttpsUrlConnectionBean HttpsUrlConnectionBean() {
        HttpsUrlConnectionBean bean = new HttpsUrlConnectionBean();
        bean.enableHttpsClient();
        return bean;
    }

    public static class HttpsUrlConnectionBean {
        public void enableHttpsClient() {
            try {
                HttpsURLConnectionUtilX.initHttpsURLConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private EncryptEncoder encryptEncoder(SecurityService securityService) {
        EncryptEncoder encryptEncoder = new EncryptEncoder();
        encryptEncoder.setSecurityService(securityService);
        return encryptEncoder;
    }

    private DecipheringDecoder decipheringDecoder(SecurityService securityService) {
        DecipheringDecoder decipheringDecoder = new DecipheringDecoder();
        decipheringDecoder.setSecurityService(securityService);
        return decipheringDecoder;
    }


//    remote services
    @Bean
    @SneakyThrows
    public SecurityService securityService(){
        Address remoteServer = proxyProperties.getRemoteServer();
        return serviceRegistry.lookup(remoteServer.getHost(), remoteServer.getPort(), isSSL, SecurityService.class);
    }

    @Bean
    @SneakyThrows
    public EquipmentCatalogService catalogService(){
        Address remoteServer = proxyProperties.getRemoteServer();
        return serviceRegistry.lookup(remoteServer.getHost(), remoteServer.getPort(), isSSL, EquipmentCatalogService.class);
    }

    @Bean
    @SneakyThrows
    public ResSubscribeService resSubscribeService(){
        Address remoteServer = proxyProperties.getRemoteServer();
        return serviceRegistry.lookup(remoteServer.getHost(), remoteServer.getPort(), isSSL, ResSubscribeService.class);
    }


}
