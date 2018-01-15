package com.fable.mssg.resource.service;

import com.fable.mssg.domain.resmanager.PublishRegion;

/**
 * description  资源发布范围接口
 * @author xiejk 2017/9/30
 */
public interface PublishRegionService {

    /**
     * 新增资源发布范围
     * @param pb 资源发布范围对象
     * @return 资源发布范围对象
     */
     PublishRegion insertPublishRegion(PublishRegion pb);



}
