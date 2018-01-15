package com.fable.mssg.service.datasource;

import com.fable.mssg.domain.dsmanager.DataSource;

import java.util.List;

/**
 * .
 *
 * @author stormning 2017/8/21
 * @since 1.3.0
 */
public interface DataSourceService {

    void save(List<DataSource> dataSource);
    
    DataSource save(DataSource dataSource);
    
    void delete(List<DataSource> id);

    List<DataSource> findAll();
    
    List<DataSource> querySubscribe(String userId);

    List<DataSource> find(List<String> ids);

    List<DataSource> findByDeviceId(String deviceId);

    List<DataSource> findAllWithMedia();

    List<DataSource> findByMediaId(String mediaId);


    DataSource findByResIdAndParent(String resourceId, String parentId);

    DataSource findById(String id);
}
