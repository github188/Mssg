package com.fable.mssg.datasource.repository;

import com.fable.mssg.domain.dsmanager.DataSource;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * .
 *
 * @author stormning 2017/8/21
 * @since 1.3.0
 */
public interface DataSourceRepository extends GenericJpaRepository<DataSource, String>, JpaSpecificationExecutor<DataSource> {

    List<DataSource> findByRsId(String rid);

    @Query(value = "SELECT d FROM DataSource d JOIN d.mediaId ")
    List<DataSource> findAllWithMedia();

    @Query(value = "SELECT d FROM DataSource d JOIN d.mediaId WHERE d.id = ?1")
    DataSource findOneDataSource(String dataSourceId);

    @Query(value = "SELECT d FROM DataSource d where d.mediaId.deviceId=?1")
    List<DataSource> findByMediaId(String mediaId);

    DataSource findByRsIdAndDsCode(String resourceId, String parentId);
}
