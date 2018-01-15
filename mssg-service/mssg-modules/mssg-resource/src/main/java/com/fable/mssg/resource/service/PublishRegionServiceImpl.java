package com.fable.mssg.resource.service;

import com.fable.mssg.domain.resmanager.PublishRegion;
import com.fable.mssg.resource.repository.PublishRegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description 资源发布范围接口实现类
 * @author xiejk 2017/9/30
 */
@Service
public class PublishRegionServiceImpl implements PublishRegionService {

    //资源发布范围dao层操作对象
    @Autowired
    PublishRegionRepository publishRegionRepository;

    /**
     * 新增资源发布范围
     * @param pb 资源发布范围对象
     * @return  资源发布范围对象
     */
    @Override
    public PublishRegion insertPublishRegion(PublishRegion pb) {
        return publishRegionRepository.save(pb);
    }
}
