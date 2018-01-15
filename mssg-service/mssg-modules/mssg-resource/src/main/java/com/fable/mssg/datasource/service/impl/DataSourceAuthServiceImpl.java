package com.fable.mssg.datasource.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.bean.DataSourceInfo;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.datasource.repository.DataSourceRepository;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.repository.MediaSubscribeRepository;
import com.fable.mssg.resource.repository.SubscribePrvRepository;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.datasource.DataSourceAuthService;
import com.fable.mssg.service.resource.MediaInfoService;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.user.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/11/7
 */

@Service
@Exporter(interfaces = DataSourceAuthService.class)
public class DataSourceAuthServiceImpl implements DataSourceAuthService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SubscribePrvRepository subscribePrvRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private ResSubscribeService resSubscribeService;

    @Autowired
    private MediaInfoService mediaInfoService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MediaSubscribeRepository mediaSubscribeRepository;

    @Autowired
    private EquipmentCatalogService equipmentCatalogService;

    @Override
    public AuthInfo isAuth(String sipId, String dsId, Integer operateType, Long current, Long begin, Long end, boolean isThird) {
        if(operateType==10){
            return AuthInfo.builder().isAuth(true).build();
        }


        SysUser sysUser = new SysUser();
        Company company;
        String userId = null;
        MediaInfo mediaInfo = null;
        if (!isThird) {//非第三方平台
            sysUser.setSipId(sipId);
            sysUser.setDeleteFlag(0);
            sysUser.setState(0);
            try {
                sysUser = sysUserRepository.findOne(Example.of(sysUser));
            } catch (IncorrectResultSizeDataAccessException e) {
                throw new RuntimeException("sipId找到多个用户", e);
            }
            if (sysUser == null) {
                throw new RuntimeException("根据SipId未找到已经启用的用户,用户名为:" + sipId);
            }
            userId = sysUser.getId();
            company = sysUser.getComId();
        } else {
            mediaInfo = mediaInfoService.findByDeviceId(sipId);
            company = companyService.findById(mediaInfo.getComId());
        }

        SubscribePrv privilege = subscribePrvRepository.findDsPrivilege(dsId, company.getId());
        if (isThird) {
            userId = mediaSubscribeRepository.findBySscIdAndMediaId(privilege.getResSubscribeId(), mediaInfo.getId()).getShareId();
        }
        AuthInfo authInfo = AuthInfo.builder().comId(company.getId()).userId(userId).isAuth(true).build();
        if (privilege == null) {
            authInfo.setAuth(false);
            authInfo.setReason("没有发现订阅记录或订阅已经撤销");
            return authInfo;
        }
        switch (operateType) {

            case 1://实时
                if (0 == privilege.getRealPlay()) {
                    authInfo.setAuth(false);
                } else if (2 == privilege.getRealPlay()) {
                    authInfo.setAuth(true);
                } else {
                    authInfo.setAuth(isSatisfied(current, privilege.getRealBeginTime(), privilege.getRealEndTime()));
                    authInfo.setReason("无此时间段内权限");
                }
                break;
            case 2://历史
                if (0 == privilege.getPlayBack()) {
                    authInfo.setAuth(false);
                } else if (2 == privilege.getPlayBack()) {
                    authInfo.setAuth(true);
                } else {
                    authInfo.setAuth(isSatisfied(begin, privilege.getHisBeginTime(), privilege.getHisEndTime())
                            && isSatisfied(end, privilege.getHisBeginTime(), privilege.getHisEndTime()));
                    authInfo.setReason("无此时间段内权限");
                }
                break;
            case 3:
                authInfo.setAuth(1 == privilege.getRealControl());
                break;
            case 4:
                authInfo.setAuth(1 == privilege.getDownload());
                break;
            case 5:
                authInfo.setAuth(1 == privilege.getRealSnap());//实时截图
                break;
            case 6:
                authInfo.setAuth(1 == privilege.getRecord());//录像
                break;
            case 7:
                authInfo.setAuth(1 == privilege.getHistSnap());//历史截图
                break;
            default:
                authInfo.setAuth(true);
        }
        /*
        if (!company.getLevel().equals(ComEquipLevel.ALL)) {
            //考虑到设备等级会
            List<ComEquipLevel> comEquipLevelList = companyService.findComLevel(company.getLevel());

            equipmentCatalogService.getCatalogInfoByDeviceCode(dataSourceRepository.findOne(dsId).getDsCode());
            for (ComEquipLevel comEquipLevel : comEquipLevelList) {
                // if ()
            }
        }
        */
        if(!authInfo.isAuth()&&null==authInfo.getReason()){
            authInfo.setReason("无此项操作的权限");
        }

        return authInfo;
    }

    @Override
    public DataSourceInfo getDataSourceInfo(String dataSourceId) {
        DataSource dataSource = dataSourceRepository.findOneDataSource(dataSourceId);
        return DataSourceInfo.builder()
                .dsCode(dataSource.getDsCode())
                .mediaCode(dataSource.getMediaId().getDeviceId())
                .mediaIp(dataSource.getMediaId().getIpAddress())
                .mediaPort(dataSource.getMediaId().getSessionPort())
                .build();
    }

    private boolean isSatisfied(Long toVali, Timestamp begin, Timestamp end) {
        Date toValidate = new Date(toVali);
        return toValidate.after(begin) && toValidate.before(end);

    }
}
