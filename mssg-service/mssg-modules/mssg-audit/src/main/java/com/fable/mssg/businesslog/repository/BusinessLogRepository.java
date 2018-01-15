package com.fable.mssg.businesslog.repository;


import com.fable.mssg.domain.businesslog.BusinessLog;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessLogRepository extends GenericJpaRepository<BusinessLog, String>, JpaSpecificationExecutor<BusinessLog> {
    BusinessLog findById(String id);

    @Query(nativeQuery = true,
            value = "SELECT ds.DS_NAME, mc.NAME as COMPANY , bl.OP_TYPE,bl.START_TIME,bl.SEGMENT_TIME,msu.USER_NAME " +
            "FROM mssg_bus_log bl  " +
            "JOIN mssg_datasource ds ON bl.DS_ID = ds.ID " +
            "JOIN mssg_sys_user msu ON msu.ID = bl.VISIT_USER " +
            "JOIN mssg_company mc ON mc.ID = bl.COM_ID " +
            "WHERE bl.VISIT_TYPE = ?1 " +
            "ORDER BY bl.START_TIME DESC")
    List find(Integer type);
}
