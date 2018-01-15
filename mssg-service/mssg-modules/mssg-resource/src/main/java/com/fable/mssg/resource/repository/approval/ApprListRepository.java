package com.fable.mssg.resource.repository.approval;


import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *  审批dao层
 * @author xiejk 2017/9/30
 */
public interface ApprListRepository extends GenericJpaRepository<ApprList,String> ,JpaSpecificationExecutor<ApprList> {



}
