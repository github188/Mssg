package com.fable.mssg.service.resource;

import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.bean.SubscribeCondition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
public interface ResSubscribeService {

    ResSubscribe save(ResSubscribe resSubscribe,String resId,Company company);
    void delete(String id);
    void approval(ResSubscribe resSubscribe, SysUser sysUser);
    ResSubscribe findById(String id);

    boolean cancel(String id);

    List<ResSubscribe> findByCondition(SubscribeCondition condition);

    List<SubscribePrv> findByUser(SysUser sysUser);

    List<ResSubscribe> findByCompany(Company company, Integer dsLevel, Integer locationType, String dsName);

    List<ResSubscribe> findByApplyUser(SysUser sysUser);

    void shared(String[] mediaIds,String subscribeId,String userId);

    /**
     * 根据媒体平台该平台具有权限的设备集合
     * @param mediaId
     * @return
     */

    List<DataSource> findDataSourceByMediaId(String mediaId);

    long count();

    long count(ResSubscribe example);

    long countByComId(String id);

    void deleteByResourceId(String rid);

    String uploadFile(String fileName,InputStream inputStream) throws IOException;

    void deletePrv(Set<SubscribePrv> subscribePrvList);
}
