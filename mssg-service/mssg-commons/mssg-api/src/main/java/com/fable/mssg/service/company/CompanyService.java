package com.fable.mssg.service.company;


import com.fable.mssg.domain.company.ComEquipLevel;
import com.fable.mssg.domain.company.Company;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/18
 */
public interface CompanyService {

    Page<Company> findAll(int page, int size, String nameOrCode, Integer comLevel);

    Company save(Company company);

    void deleteById(String id);

    Company findById(String id);

    Company findByCode(String code);

    HSSFWorkbook leadOut();

    boolean leadIn(InputStream inputStream) throws IOException;

    void saveComLevel(List<ComEquipLevel> comEquipLevels);

    List<ComEquipLevel> findAllComLevel();

    List<ComEquipLevel> findComLevel(Integer comLevel);

    void deleteComLevel(Integer comLevel);

    List findTopSubscribeCompany(Timestamp applyTime);

    List<Company> findAllByComTypeAndStatus(int companytype, int status);

    long countShared();

    long countOnline();

    List<Company> findAll(int status);

    Company findAllByName(String name);

    List<Integer> findAllComLevelCode();

    void modifyComLevel(Integer comLevel, String levelJson);
}
