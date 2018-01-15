package com.fable.mssg.company.service;

import com.alibaba.fastjson.JSONArray;
import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.company.repository.ComEquipLevelRepository;
import com.fable.mssg.company.repository.CompanyRepository;
import com.fable.mssg.company.service.exception.CompanyException;
import com.fable.mssg.domain.company.ComEquipLevel;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.user.repository.UserRepository;
import com.fable.mssg.utils.HSSFUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/9
 */
@Exporter(interfaces = CompanyService.class)
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ComEquipLevelRepository comEquipLevelRepository;

    @Autowired
    ResSubscribeService resSubscribeService;

    @Autowired
    DictItemService dictItemService;

    /**
     * 条件查询
     *
     * @param page
     * @param size
     * @param nameOrCode
     * @param comLevel
     * @return
     */
    @Override
    public Page<Company> findAll(int page, int size, String nameOrCode, Integer comLevel) {
        return companyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.equal(root.get("status"), 0);
            if (nameOrCode != null && !"".equals(nameOrCode)) {
                predicate = criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + nameOrCode + "%"),
                        criteriaBuilder.equal(root.get("code"), nameOrCode));
            }
            if (comLevel != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("comLevel"), comLevel));
            }

            return predicate;
        }, new PageRequest(page - 1, size));
    }

    @Override
    public Company save(Company company) {
        Company companyPersist = companyRepository.findByName(company.getName());

        if (companyPersist != null && !companyPersist.getId().equals(company.getId())) {
            throw new CompanyException(CompanyException.COMPANY_NAME_ALREADY_EXIST);
        }

        return companyRepository.save(company);
    }

    @Override
    public void deleteById(String id) {
        SysUser sysUser = new SysUser();
        sysUser.setDeleteFlag(0);
        sysUser.setComId(new Company(id));
        if (userRepository.count(Example.of(sysUser)) > 0) {
            throw new CompanyException(CompanyException.COMPANY_REMAIN_USER);
        }
        if (resSubscribeService.countByComId(id) > 0) {
            throw new CompanyException(CompanyException.COMPANY_REMAIN_SUBSCRIBE);
        }
        sysUser.setDeleteFlag(1);
        userRepository.delete(userRepository.findAll(Example.of(sysUser)));
        companyRepository.delete(id);
    }


    @Override
    public Company findById(String id) {
        return companyRepository.findOne(id);
    }

    @Override
    public Company findByCode(String code) {

        return companyRepository.findByCode(code);
    }

    /**
     * 数据库->文件
     *
     * @return
     */
    @Override
    public HSSFWorkbook leadOut() {

        List<Company> companies = companyRepository.findAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("company");
        sheet.setDefaultColumnWidth(20);
        //设置标题
        HSSFRow head = sheet.createRow(0);
        head.createCell(0).setCellValue("单位ID");
        head.createCell(1).setCellValue("单位名称");
        head.createCell(2).setCellValue("单位代码");
        head.createCell(3).setCellValue("父单位ID");
        head.createCell(4).setCellValue("单位等级");
        head.createCell(5).setCellValue("单位描述");
        head.createCell(6).setCellValue("索引");
        head.createCell(7).setCellValue("邮箱");
        head.createCell(8).setCellValue("单位地址");
        head.createCell(9).setCellValue("ip网段");
        head.createCell(10).setCellValue("单位类型");
        head.createCell(11).setCellValue("单位职务");
        head.createCell(12).setCellValue("电话");
        head.createCell(13).setCellValue("联系人");
        int rowNum = 1;
        for (Company company : companies) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(company.getId());
            row.createCell(1).setCellValue(company.getName());
            row.createCell(2).setCellValue(company.getCode());
            row.createCell(3).setCellValue(company.getPid());
            row.createCell(4).setCellValue(company.getComLevel() == null ? 0 : company.getComLevel());
            row.createCell(5).setCellValue(company.getDescription());
            //对数字要做判空操作,因为数字的自动去包装类的操作会引发空指针异常
            row.createCell(6).setCellValue(company.getIdex() == null ? 0 : company.getIdex());
            row.createCell(7).setCellValue(company.getEmail());
            row.createCell(8).setCellValue(company.getAddress());
            row.createCell(9).setCellValue(company.getComIpSegment());
            row.createCell(10).setCellValue(company.getComType() == null ? 0 : company.getComType());
            row.createCell(11).setCellValue(company.getPosition());
            row.createCell(12).setCellValue(company.getTelphone());
            row.createCell(13).setCellValue(company.getContacts());
            rowNum++;
        }
        return workbook;
    }

    @Override
    public boolean leadIn(InputStream inputStream) {

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            List<Company> companies = new ArrayList<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                Company company = new Company();
                company.setId(HSSFUtil.getString(row.getCell(0)));
                company.setName(HSSFUtil.getString(row.getCell(1)));
                company.setCode(HSSFUtil.getString(row.getCell(2)));
                company.setPid(HSSFUtil.getString(row.getCell(3)));
                company.setComLevel(HSSFUtil.getInteger(row.getCell(4)));
                company.setIdex(HSSFUtil.getInteger(row.getCell(6)));
                company.setDescription(HSSFUtil.getString(row.getCell(5)));
                company.setEmail(HSSFUtil.getString(row.getCell(7)));
                company.setAddress(HSSFUtil.getString(row.getCell(8)));
                company.setComIpSegment(HSSFUtil.getString(row.getCell(9)));
                company.setComType(HSSFUtil.getInteger(row.getCell(10)));
                company.setPosition(HSSFUtil.getString(row.getCell(11)));
                company.setTelphone(HSSFUtil.getString(row.getCell(12)));
                company.setContacts(HSSFUtil.getString(row.getCell(13)));
                companies.add(company);
                //TODO 判断单位是否存在 判断标准
            }
            companyRepository.save(companies);
            return true;

        } catch (IOException e) {
            log.error("导入异常", e);
            throw new CompanyException(CompanyException.IO_EXCEPTION);
        }


    }

    /**
     * 保存单位等级以及其可访问的设备
     *
     * @param comEquipLevels
     */
    @Override
    public void saveComLevel(List<ComEquipLevel> comEquipLevels) {
        if (comEquipLevels != null && comEquipLevels.size() > 0) {
            Integer comLevel = comEquipLevels.get(0).getComLevel();
            if (comEquipLevelRepository.findByComLevel(comLevel).size() != 0) {
                throw new CompanyException(CompanyException.COM_LEVEL_ALREADY_EXIST);
            }
            comEquipLevelRepository.save(comEquipLevels);
        }

    }

    @Override
    public List<ComEquipLevel> findAllComLevel() {
        return comEquipLevelRepository.findAll();
    }


    @Override
    public List<ComEquipLevel> findComLevel(Integer comLevel) {
        return comEquipLevelRepository.findByComLevel(comLevel);
    }

    @Override
    public void deleteComLevel(Integer comLevel) {

        if (companyRepository.findByComLevel(comLevel).size() > 0) {
            throw new CompanyException(CompanyException.COM_LEVEL_IN_USED);
        }
        List<ComEquipLevel> comEquipLevels = findComLevel(comLevel);

        comEquipLevelRepository.delete(comEquipLevels);
    }

    @Override
    public List findTopSubscribeCompany(Timestamp applyTime) {

        return companyRepository.findTopSubscribeCompany(applyTime);
    }

    @Override
    public List<Company> findAllByComTypeAndStatus(int companytype, int status) {
        return companyRepository.findAllByComTypeAndStatus(companytype, status);
    }

    @Override
    public long countShared() {
        Company company = new Company();
        company.setComType(2);
        return companyRepository.count(Example.of(company));
    }

    @Override
    public long countOnline() {
        return companyRepository.countOnline();
    }

    @Override
    public List<Company> findAll(int status) {
        return companyRepository.findAllByStatus(status);
    }

    @Override
    public Company findAllByName(String name) {
        return companyRepository.findByName(name);
    }

    @Override
    public List<Integer> findAllComLevelCode() {
        return comEquipLevelRepository.findAllComLevelCode();
    }

    @Override
    public void modifyComLevel(Integer comLevel, String levelJson) {
        List<ComEquipLevel> comEquipLevels = this.findComLevel(comLevel);
        comEquipLevelRepository.delete(comEquipLevels);
        comEquipLevels.clear();
        JSONArray jsonArray = JSONArray.parseArray(levelJson);
        Integer[] equipLevels = new Integer[jsonArray.size()];
        equipLevels = jsonArray.toArray(equipLevels);
        for (int equipLevel : equipLevels) {
            ComEquipLevel comEquipLevel = new ComEquipLevel();
            comEquipLevel.setComLevel(comLevel);
            comEquipLevel.setEquipLevel(equipLevel);
            comEquipLevels.add(comEquipLevel);
        }
        comEquipLevelRepository.save(comEquipLevels);

    }


}
