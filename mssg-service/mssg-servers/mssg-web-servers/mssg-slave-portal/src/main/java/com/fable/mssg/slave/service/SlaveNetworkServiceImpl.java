package com.fable.mssg.slave.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.service.slave.SlaveNetworkService;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description
 * @Author wangmeng 2017/11/28
 */
@Exporter(interfaces = SlaveNetworkService.class)
@Slf4j
public class SlaveNetworkServiceImpl implements SlaveNetworkService{

    @Value("${slave.in.netInterface.name}")
    String netInterfaceName;

    @Override
    public Long getRxBytes() {

        Sigar sigar = new Sigar();
        try {
            NetInterfaceStat ifStat = sigar.getNetInterfaceStat(netInterfaceName);
            return ifStat.getTxBytes();
        } catch (SigarException e) {
            log.error("获取网卡信息失败",e);
        }
        return 0L;
    }
}
