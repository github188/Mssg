package com.fable.mssg.datasource.service.impl;

import java.util.List;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.datasource.converter.DataSourceConverter;
import com.fable.mssg.datasource.repository.DataSourceRepository;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.service.datasource.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @author stormning 2017/8/21
 * @since 1.3.0
 */
@Exporter(interfaces = DataSourceService.class)
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataSourceConverter dataSourceConverter;


    @Override
    public DataSource save(DataSource dataSource) {
        return dataSourceRepository.save(dataSource);
    }

    @Override
    public List<DataSource> findAll() {
        return dataSourceRepository.findAll();
    }

    @Override
    public List<DataSource> querySubscribe(String userId) {
        List<DataSource> dataSources = dataSourceRepository.findAll();
        return dataSources;
    }

    @Override
    public void save(List<DataSource> dataSource) {
        dataSourceRepository.save(dataSource);
    }

    @Override
    public void delete(List<DataSource> dataSource) {
        dataSourceRepository.delete(dataSource);
    }

    @Override
    public List<DataSource> find(List<String> ids) {
        return dataSourceRepository.findAll(ids);
    }

    @Override
    public List<DataSource> findByDeviceId(String deviceId) {
        DataSource dataSource = new DataSource();
        dataSource.setDsCode(deviceId);
        return dataSourceRepository.findAll(Example.of(dataSource));
    }

    @Override
    public List<DataSource> findAllWithMedia() {

        List<DataSource> dataSources = dataSourceRepository.findAllWithMedia();
        return dataSources;
    }

    @Override
    public List<DataSource> findByMediaId(String mediaId) {
        return dataSourceRepository.findByMediaId(mediaId);
    }


    @Override
    public DataSource findByResIdAndParent(String resourceId, String parentId){

        return dataSourceRepository.findByRsIdAndDsCode(resourceId,parentId);
    }

    @Override
    public DataSource findById(String id) {
        return dataSourceRepository.findOne(id);
    }
}
