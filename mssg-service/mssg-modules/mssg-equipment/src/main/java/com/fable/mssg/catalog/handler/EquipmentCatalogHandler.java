package com.fable.mssg.catalog.handler;

import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.converter.EquipmentCatalogConverter;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.catalog.xml.query.CatalogItemXml;
import com.fable.mssg.catalog.xml.query.CatalogResponseXml;
import com.fable.mssg.catalog.xml.query.EquipmentInfoXml;
import com.fable.mssg.catalog.xml.query.ResponseDeviceListXml;
import com.fable.mssg.vo.orginalds.VEquipmentCatalogBean;
import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.XmlContent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.net.InetSocketAddress;


/**
 * @author : yuhl 2017-09-01
 *         设备目录查询逻辑处理类
 */
@Slf4j
public class EquipmentCatalogHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public EquipmentCatalogService equipmentCatalogService;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    @Setter
    public CacheManager cacheManager;

    /**
     * 判断信令是否需要处理
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        // 返回的200 OK或者是结果信令
        boolean flag = false;
        String method = sipMessage.getCSeqHeader().getMethod().toString();
        Buffer buffer = sipMessage.getRawContent();
        if (buffer == null) { // 信令内容为空
            flag = false;
        } else if (buffer.toString().contains("Notify") && !"NOTIFY".equals(method)) { // 不处理保活和通知信令
            flag = false;
        } else if (buffer.toString().contains("Catalog")) { // 目录查询返回
            flag = true;
        } else if (buffer.toString().contains("RecordInfo") && buffer.toString().contains("Response")) { // 历史视频文件检索
            flag = true;
        } else if (buffer.toString().contains("RecordInfo") && buffer.toString().contains("Query")) {
            flag = false;
        }
        return (sipMessage.isMessage() && flag)
                || (sipMessage.getInitialLine().toString().contains("200 OK")
                && sipMessage.getCSeqHeader().toString().contains("MESSAGE"));
    }

    /**
     * 处理原始消息
     *
     * @param sipMessage
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage sipMessage) {

        MasterConfigProperties.Master master = masterConfigProperties.getMaster();
        String gatewayHost = sipMessage.getViaHeader().getHost().toString(); // 网关ip
        int gatewayPort = sipMessage.getViaHeader().getPort(); // 网关端口
        InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort());
        InetSocketAddress gatewayAddress = AddressUtils.from(gatewayHost, gatewayPort);
        // 返回200 OK
        final SipResponse response = sipMessage.createResponse(SipConstant.OK_CMD);
        /**
         * 异步保存数据，保证可以立即发送响应信令
         */
        SaveCatalogThread saveCatalogThread = new SaveCatalogThread(sipMessage);
        Thread thread = new Thread(saveCatalogThread);
        thread.start();
        return new SipRouterInfo(masterAddress, gatewayAddress, response);
    }

    /**
     * 判断是否存在待新增的记录
     *
     * @param deviceCode
     * @return
     */
    public boolean isExist(String deviceCode) {
        return equipmentCatalogService.getCatalogInfoByDeviceCode(deviceCode) != null;
    }

    /**
     * 保存设备目录
     */
    private class SaveCatalogThread implements Runnable {

        private SipMessage sipMessage;

        private SaveCatalogThread(SipMessage sipMessage) {
            this.sipMessage = sipMessage;
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
            /**
             * 将返回的信令体解析为对应的实体
             */
            // 进行业务处理
            XmlContent content = ((XmlContent) sipMessage.getContent());
            CatalogResponseXml deviceListXml = content.get(CatalogResponseXml.class);

            /**
             * 设置实体类信息并保存
             */
            ResponseDeviceListXml responseDeviceListXml = deviceListXml.getDeviceListXml();
            if (null != responseDeviceListXml && null != responseDeviceListXml.getItemXmlList()
                    && responseDeviceListXml.getItemXmlList().size() > 0) { // 存在目录和设备
                CatalogItemXml catalogItemXml = responseDeviceListXml.getItemXmlList().get(0);
                EquipmentInfoXml infoXml = catalogItemXml.getInfoXml(); // 设备附加信息
                EquipmentCatalogBean catalogBean = new EquipmentCatalogBean();
                String deviceId = catalogItemXml.getDeviceId();
                catalogBean.setAddress(catalogItemXml.getAddress()); // 地址
                catalogBean.setBlock(catalogItemXml.getBlock());
                catalogBean.setBusGroupId(catalogItemXml.getBusGroupId()); // 业务分组id
                catalogBean.setCatalogName(catalogItemXml.getName()); // 目录名称
                catalogBean.setCivilCode(catalogItemXml.getCivilCode()); // 行政区划编码
                catalogBean.setDeviceId(deviceId); // 设备id
                catalogBean.setManuName(catalogItemXml.getManuFacturer()); // 厂商
                catalogBean.setModel(catalogItemXml.getModel()); // 设备型号
                catalogBean.setOwner(catalogItemXml.getOwner()); // 设备所属
                catalogBean.setRegisterWay(catalogItemXml.getRegisterWay()); // 注册路径
                catalogBean.setParentId(catalogItemXml.getParentId()); // 父级目录id
                catalogBean.setParental(catalogItemXml.getParental()); // 是否有子设备 1-有 0-无
                catalogBean.setSecrecy(catalogItemXml.getSecrecy()); // 保密属性
                catalogBean.setStatus(catalogItemXml.getStatus()); // 设备状态
                catalogBean.setMediaDeviceId(deviceListXml.getDeviceId()); // 媒体源设备id
                catalogBean.setParent_id(catalogItemXml.getParentId());
                catalogBean.setLng(catalogItemXml.getLongitude()); // 经度
                catalogBean.setLat(catalogItemXml.getLatitude()); // 纬度
                catalogBean.setIpAddress(catalogItemXml.getIpAddress()); // 设备ip
                if (null != infoXml) { // 设备信息不为空
                    catalogBean.setLocationType(infoXml.getPositionType()); // 摄像机位置
                    catalogBean.setEquipType(infoXml.getPtzType()); // 摄像机类型
                }
                /**
                 * 目录类型
                 */
                if (deviceId.length() == 2 || deviceId.length() == 4 || deviceId.length() == 6 || deviceId.length() == 8) {
                    catalogBean.setCatalogType(1);
                } else if (deviceId.length() == 20) { // 系统目录
                    String subId = deviceId.substring(10, 13);
                    if ("200".equals(subId)) {
                        catalogBean.setCatalogType(2);
                    } else if ("215".equals(subId)) {
                        catalogBean.setCatalogType(3);
                    } else if ("216".equals(subId)) {
                        catalogBean.setCatalogType(4);
                    } else {
                        catalogBean.setCatalogType(5);
                    }
                }
                /**
                 * 如果存在则更新，不存在则新增
                 */
                if (!isExist(catalogItemXml.getDeviceId())) { // 不存在记录
                    equipmentCatalogService.saveCatalogInfo(catalogBean); // 保存设备目录信息
                } else { // 存在则更新
                    equipmentCatalogService.updateCatalogInfoByDeviceCode(catalogBean);
                }
            }
        }
    }
}
