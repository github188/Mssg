package com.fable.mssg.catalog.handler;

import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.catalog.xml.notify.CatalogNotifyItemXml;
import com.fable.mssg.catalog.xml.notify.CatalogNotifyXml;
import com.fable.mssg.catalog.xml.notify.NotifyDeviceListXml;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.XmlContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 17:16 2017/9/4 0004
 * 处理目录通知处理类
 */
public class CatalogNotifyHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 判断SIP消息是否需要处理
     *
     * @param message
     * @return
     */
    @Override
    public boolean supports(SipMessage message) {
        return message.getInitialLine().toString().contains("NOTIFY");
    }

    /**
     * 处理通知消息
     *
     * @param rawMessage
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage rawMessage) {
        MasterConfigProperties.Master master = masterConfigProperties.getMaster();
        String gatewayHost = rawMessage.getViaHeader().getHost().toString(); // 网关ip
        int gatewayPort = rawMessage.getViaHeader().getPort(); // 网关端口
        InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort());
        InetSocketAddress gatewayAddress = AddressUtils.from(gatewayHost, gatewayPort);
        // 进行业务处理
        XmlContent content = ((XmlContent) rawMessage.getContent());
        CatalogNotifyXml notifyXml = content.get(CatalogNotifyXml.class);
        // 根据event类型操作
//        updateCatalogInfo(notifyXml);
        UpdateCatalog updateCatalog = new UpdateCatalog(notifyXml);
        Thread updateThread = new Thread(updateCatalog);
        updateThread.start();
        // 发送200 OK的响应信令
        return new SipRouterInfo(masterAddress, gatewayAddress, rawMessage.createResponse(SipConstant.OK_CMD));
    }

    /**
     * 异步业务逻辑处理
     */
    private class UpdateCatalog implements Runnable {

        private CatalogNotifyXml notifyXml;

        private UpdateCatalog(CatalogNotifyXml notifyXml) {
            this.notifyXml = notifyXml;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            NotifyDeviceListXml notifyDeviceListXml = notifyXml.getNotifyDeviceListXml();
            CatalogNotifyItemXml notifyItemXml = notifyDeviceListXml.getNotifyItemXml();
            String deviceCode = notifyItemXml.getDeviceId(); // 设备id
            String event = notifyItemXml.getEvent(); // 操作类型
            if ("DEL".equals(event)) { // 删除
                equipmentCatalogService.deleteCatalogByDeviceCode(deviceCode);
            } else if ("UPDATE".equals(event)) { // 更新
                // 根据deviceCode查询相应记录
                EquipmentCatalogBean catalogBean = equipmentCatalogService.getCatalogInfoByDeviceCode(deviceCode);
                equipmentCatalogService.updateCatalogInfoByDeviceCode(catalogBean); // 更新操作
            } else if ("ADD".equals(event)) { // 新增
                EquipmentCatalogBean catalogBean = new EquipmentCatalogBean();
                catalogBean.setAddress(notifyItemXml.getAddress()); // 地址
                catalogBean.setBlock(notifyItemXml.getBlock());
                catalogBean.setBusGroupId(notifyItemXml.getBusGroupId()); // 业务分组id
                catalogBean.setCatalogName(notifyItemXml.getName()); // 目录名称
                catalogBean.setCivilCode(notifyItemXml.getCivilCode()); // 行政区划编码
                catalogBean.setDeviceId(notifyItemXml.getDeviceId()); // 设备id
                catalogBean.setManuName(notifyItemXml.getManuFacturer()); // 厂商
                catalogBean.setModel(notifyItemXml.getModel()); // 设备型号
                catalogBean.setOwner(notifyItemXml.getOwner()); // 设备所属
                catalogBean.setRegisterWay(notifyItemXml.getRegisterWay()); // 注册路径
                catalogBean.setParentId(notifyItemXml.getParentId()); // 父级目录id
                catalogBean.setParental(notifyItemXml.getParental()); // 是否有子设备 1-有 0-无
                catalogBean.setSecrecy(notifyItemXml.getSecrecy()); // 保密属性
                catalogBean.setStatus(notifyItemXml.getStatus()); // 设备状态
                catalogBean.setParent_id(notifyItemXml.getParentId());
                equipmentCatalogService.saveCatalogInfo(catalogBean); // 保存设备目录信息
            } else { // ON:上线、OFF:离线、VLOST:视频丢失、DEFECT:故障
                equipmentCatalogService.updateCatalogStatus(event, deviceCode);
            }
        }
    }

//    public void updateCatalogInfo(CatalogNotifyXml notifyXml) {
//        NotifyDeviceListXml notifyDeviceListXml = notifyXml.getNotifyDeviceListXml();
//        CatalogNotifyItemXml notifyItemXml = notifyDeviceListXml.getNotifyItemXml();
//        String deviceCode = notifyItemXml.getDeviceId(); // 设备id
//        String event = notifyItemXml.getEvent(); // 操作类型
//        if ("DEL".equals(event)) { // 删除
//            equipmentCatalogService.deleteCatalogByDeviceCode(deviceCode);
//        } else if ("UPDATE".equals(event)) { // 更新
//            // 根据deviceCode查询相应记录
//            EquipmentCatalogBean catalogBean = equipmentCatalogService.getCatalogInfoByDeviceCode(deviceCode);
//            equipmentCatalogService.updateCatalogInfoByDeviceCode(catalogBean); // 更新操作
//        } else if ("ADD".equals(event)) { // 新增
//            EquipmentCatalogBean catalogBean = new EquipmentCatalogBean();
//            catalogBean.setAddress(notifyItemXml.getAddress()); // 地址
//            catalogBean.setBlock(notifyItemXml.getBlock());
//            catalogBean.setBusGroupId(notifyItemXml.getBusGroupId()); // 业务分组id
//            catalogBean.setCatalogName(notifyItemXml.getName()); // 目录名称
//            catalogBean.setCivilCode(notifyItemXml.getCivilCode()); // 行政区划编码
//            catalogBean.setDeviceId(notifyItemXml.getDeviceId()); // 设备id
//            catalogBean.setManuName(notifyItemXml.getManuFacturer()); // 厂商
//            catalogBean.setModel(notifyItemXml.getModel()); // 设备型号
//            catalogBean.setOwner(notifyItemXml.getOwner()); // 设备所属
//            catalogBean.setRegisterWay(notifyItemXml.getRegisterWay()); // 注册路径
//            catalogBean.setParentId(notifyItemXml.getParentId()); // 父级目录id
//            catalogBean.setParental(notifyItemXml.getParental()); // 是否有子设备 1-有 0-无
//            catalogBean.setSecrecy(notifyItemXml.getSecrecy()); // 保密属性
//            catalogBean.setStatus(notifyItemXml.getStatus()); // 设备状态
//            catalogBean.setParent_id(notifyItemXml.getParentId());
//            equipmentCatalogService.saveCatalogInfo(catalogBean); // 保存设备目录信息
//        } else { // ON:上线、OFF:离线、VLOST:视频丢失、DEFECT:故障
//            equipmentCatalogService.updateCatalogStatus(event, deviceCode);
//        }
//    }

}
