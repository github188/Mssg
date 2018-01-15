package com.fable.mssg.resource.repository;

import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.usermanager.SysUser;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
public interface ResSubscribeRepository extends GenericJpaRepository<ResSubscribe, String>, JpaSpecificationExecutor<ResSubscribe> {


    List<ResSubscribe> findByStateAndApplyUser(int state, SysUser applyUser);

    List<ResSubscribe> findByStateAndComId(int state, Company company);

    List<ResSubscribe> findByApplyUser(SysUser sysUser);

    List<ResSubscribe> findByResId(Resource resource);

    ResSubscribe findByComIdAndResId(Company company, Resource resource);

    @Query(value = "SELECT r FROM ResSubscribe r WHERE r.comId.id=?1 AND (r.state=2 OR r.state=5) AND r.resId.resStatus=5")
    List<ResSubscribe> findSubscribe(String comId);
}
