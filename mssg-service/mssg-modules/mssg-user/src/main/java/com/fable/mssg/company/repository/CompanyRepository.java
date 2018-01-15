package com.fable.mssg.company.repository;

import com.fable.mssg.domain.company.Company;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/18
 */
public interface CompanyRepository extends GenericJpaRepository<Company, String>, JpaSpecificationExecutor<Company> {
    Company findByCode(String code);


    @Query(nativeQuery = true,
            value = "SELECT  mc.ID,mc.Name,mc.CODE , COUNT(mrs.ID) as COUNT FROM mssg_res_subscribe mrs " +
                    "JOIN mssg_company mc " +
                    "ON mrs.COM_ID = mc.ID " +
                    "WHERE mrs.UPDATE_TIME > ?1  " +
                    "AND mrs.STATE = 2 " +
                    "GROUP BY mrs.COM_ID " +
                    "ORDER BY COUNT(mrs.COM_ID) DESC  " +
                    "LIMIT 5")
    List findTopSubscribeCompany(Timestamp approval);

    List<Company> findAllByComTypeAndStatus(int comType,int status);

    @Query(nativeQuery = true,
            value = "SELECT count(DISTINCT mc.ID) as ONLINE_COM " +
                    "FROM mssg_company mc " +
                    "JOIN mssg_sys_user msu ON msu.COM_ID = mc.ID " +
                    "WHERE msu.LOGIN_STATE = 1 AND mc.COM_TYPE = 2")
    long countOnline();

    @Query("select company from Company company where company.name=?1 and ( company.status=0 or company.status=2 ) ")
    Company findByName(String name);


    List<Company> findAllByStatus(int status);

    List<Company> findByComLevel(Integer comLevel);
}
