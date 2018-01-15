package com.fable.mssg.resource.repository;

import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
public interface SubscribePrvRepository extends GenericJpaRepository<SubscribePrv, String> {

    @Query(nativeQuery = true,
            value = "SELECT msp.*  " +
                    "FROM mssg_subscribe_prv msp " +
                    "inner JOIN mssg_res_subscribe mrs ON msp.RES_SUBSCRIBE_ID = mrs.ID " +
                    "WHERE msp.DS_ID = ?1  " +
                    "AND mrs.COM_ID = ?2  " +
                    "AND (mrs.STATE = 2 OR mrs.STATE = 5)")
    SubscribePrv findDsPrivilege(String dsId, String comId);

    List<SubscribePrv> findByResSubscribeId(String rid);
}
