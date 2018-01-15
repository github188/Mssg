package com.fable.mssg.resource.repository;


import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.domain.resmanager.Resource;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

/**
 * 资源dao层
 *
 * @author xiejk 2017/9/30
 */
public interface ResourceRepository extends GenericJpaRepository<Resource, String>, JpaSpecificationExecutor<Resource> {


    List<Resource> findByCatalogId(Catalog catalog);

    @Query(nativeQuery = true,
            value = "SELECT mr.ID,mr.RES_NAME, COUNT(DISTINCT mrs.ID) as SUBMIT , count(DISTINCT md.ID) as DSQTY , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 1,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as REALTIME , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 2,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as HISTORY , " +
                    "mr.CREATE_TIME as createTime "+
                    "FROM mssg_resource mr " +
                    "JOIN mssg_res_subscribe mrs ON mr.ID = mrs.RES_ID " +
                    "JOIN mssg_datasource md ON mr.ID = md.RES_ID " +
                    "LEFT JOIN mssg_bus_log mbl ON mbl.DS_ID = md.ID " +
                    "WHERE md.DS_TYPE = 5 AND mrs.UPDATE_TIME > ?1 " +
                    "GROUP BY mr.ID " +
                    "ORDER BY COUNT(mrs.ID) DESC " +
                    "LIMIT ?2")
    List findTopSubRes(Timestamp timestamp,Integer size);

    @Query(nativeQuery = true,
            value = "SELECT mr.ID,mr.RES_NAME, COUNT(DISTINCT mrs.ID) as SUBSCRIBE , count(DISTINCT md.ID) as DSQTY , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 1,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as REALTIME , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 2,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as HISTORY , " +
                    "mr.CREATE_TIME as createTime "+
                    "FROM mssg_resource mr " +
                    "JOIN mssg_res_subscribe mrs ON mr.ID = mrs.RES_ID " +
                    "JOIN mssg_datasource md ON mr.ID = md.RES_ID " +
                    "LEFT JOIN mssg_bus_log mbl ON mbl.DS_ID = md.ID " +
                    "WHERE md.DS_TYPE = 5 AND mbl.START_TIME > ?1 " +
                    "GROUP BY mr.ID " +
                    "ORDER BY COUNT(mbl.ID) DESC " +
                    "LIMIT ?2")
    List findPopRes(Timestamp timestamp,Integer size);

    @Query(nativeQuery = true,
            value = "SELECT mr.ID,mr.RES_NAME, COUNT(DISTINCT mrs.ID) as SUBSCRIBE , count(DISTINCT md.ID) as DSQTY , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 1,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as REALTIME , " +
                    "FORMAT(COUNT(IF(mbl.OP_TYPE = 2,TRUE,NULL)) / COUNT(DISTINCT mrs.ID),0) as HISTORY , " +
                    "mr.CREATE_TIME as createTime "+
                    "FROM mssg_resource mr " +
                    "JOIN mssg_res_subscribe mrs ON mr.ID = mrs.RES_ID " +
                    "JOIN mssg_datasource md ON mr.ID = md.RES_ID " +
                    "LEFT JOIN mssg_bus_log mbl ON mbl.DS_ID = md.ID " +
                    "WHERE md.DS_TYPE = 5 AND mbl.START_TIME > ?1 AND mbl.COM_ID=?3 " +
                    "GROUP BY mr.ID " +
                    "ORDER BY COUNT(mbl.ID) DESC " +
                    "LIMIT ?2")
    List findPopResByComId(Timestamp timestamp,Integer size,String comId);

    Resource findByResCode(String resCode);

    Resource findByCatalogIdAndResName(Catalog catalogId, String resName);
}
