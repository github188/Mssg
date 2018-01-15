package com.fable.mssg.company.web;

import com.alibaba.fastjson.JSONArray;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.company.service.exception.CompanyException;
import com.fable.mssg.company.vo.VComEquipLevel;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.company.ComEquipLevel;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import com.fable.mssg.vo.dict.VDictItem;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/18
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/company")
@Slf4j
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    CompanyConverter companyConverter;

    @Autowired
    DictItemService dictItemService;


    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public DataTable<VCompany> findAll(Integer page, Integer size, String nameOrCode, Integer comLevel) {
        return DataTable.buildDataTable(companyService.findAll(page, size, nameOrCode, comLevel));
    }

    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public VCompany findById(@RequestParam String id) {
        Company company = companyService.findById(id);
        VCompany vCompany = companyConverter.convert(company);
        vCompany.setCnLevel(dictItemService.getName(company.getComLevel()));
        return vCompany;
    }

    @RequestMapping(value = "/findByCode", method = RequestMethod.GET)
    public VCompany findByCode(@RequestParam String code) {
        return companyConverter.convert(companyService.findByCode(code));
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public void addCompany(String companyName, String companyCode, String pid, Integer level, String description,
                           String email, String phone, String contacts, String address, String ipSegement,
                           String position, Integer comType, String officePhone, HttpSession session) {
        Company company = new Company();
        company.setName(companyName);
        company.setAddress(address);
        company.setCode(companyCode);
        company.setContacts(contacts);
        company.setCreateTime(new Timestamp(System.currentTimeMillis()));
        company.setComLevel(level);
        company.setCreateUser((String) session.getAttribute("login_sys_user"));
        company.setPid(pid);
        company.setDescription(description);
        company.setEmail(email);
        company.setComIpSegment(ipSegement);
        company.setPosition(position);
        company.setTelphone(phone);
        company.setComType(comType);
        company.setOfficePhone(officePhone);
        company.setStatus(0);//默认0
        companyService.save(company);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void deleteCompany(String companyId) {
        companyService.deleteById(companyId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modifyCompany(String id, String companyName, String companyCode, String pid, Integer level, String description,
                              String email, String phone, String contacts, String address, String ipSegement,
                              String position, Integer comType, String officePhone, HttpSession session) {
        Company company = companyService.findById(id);
        if (company == null) {
            throw new CompanyException(CompanyException.COMPANY_NOT_FOUND);
        }
        company.setId(id);
        company.setName(companyName);
        company.setAddress(address);
        company.setCode(companyCode);
        company.setContacts(contacts);
        company.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        company.setComLevel(level);
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        company.setComIpSegment(ipSegement);
        company.setPosition(position);
        company.setComType(comType);
        company.setUpdateUser(loginUserInfo.getSysUser().getUserName());
        company.setPid(pid);
        company.setDescription(description);
        company.setEmail(email);
        company.setTelphone(phone);
        company.setOfficePhone(officePhone);
        companyService.save(company);
    }

    @RequestMapping(value = "/leadin", method = RequestMethod.POST)
    public void leadIn(MultipartFile companies) {

        if (!companies.isEmpty()) {
            InputStream inputStream = null;
            try {
                inputStream = companies.getInputStream();
                companyService.leadIn(inputStream);
            } catch (IOException e) {
                log.error("导入异常", e);
                throw new CompanyException(CompanyException.FILE_NOT_CORRECT);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.warn("关闭流出错", e);
                    }
                }
            }
        } else {
            throw new CompanyException(CompanyException.FILE_IS_EMPTY);
        }
    }

    @RequestMapping(value = "/leadout", method = RequestMethod.GET)
    public void leadOut(HttpServletResponse res) {
        HSSFWorkbook workbook = companyService.leadOut();
        OutputStream out = null;
        try {
            res.setContentType("application/force-download");// 设置强制下载打开
            res.addHeader("Content-Disposition", "attachment;fileName=" + "UnitTable.xls");// 设置文件名
            out = res.getOutputStream();
            workbook.write(out);
        } catch (IOException e) {
            log.error("导出异常", e);
            throw new CompanyException(CompanyException.IO_EXCEPTION);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                log.warn("关闭流出错", e);
            }
        }

    }

    @RequestMapping(value = "/getComLevel", method = RequestMethod.GET)
    @ApiOperation(value = "查询单位等级以及可访问设备等级")
    public DataTable<VComEquipLevel> getComLevel(Integer page, Integer size) {
        List<ComEquipLevel> comEquipLevels = companyService.findAllComLevel();
        List<VComEquipLevel> vComEquipLevels = new ArrayList<>();
        for (ComEquipLevel comEquipLevel : comEquipLevels) {
            Integer comLevel = comEquipLevel.getComLevel();
            String cnLevel = dictItemService.getName(comLevel);
            boolean flag = false;//结果集中没有
            for (VComEquipLevel vComEquipLevel : vComEquipLevels) {
                if (comLevel.equals(vComEquipLevel.getComLevel())) {
                    vComEquipLevel.getEquipLevel().add(comEquipLevel.getEquipLevel() + "");
                    flag = true;//结果集中有
                }
            }
            if (!flag) {
                List<String> equipLevels = new ArrayList<>();
                equipLevels.add(comEquipLevel.getEquipLevel() + "");
                vComEquipLevels.add(VComEquipLevel.builder()
                        .comLevel(comLevel)
                        .equipLevel(equipLevels)
                        .cnLevel(cnLevel)
                        .build());
            }

        }
        //由于数据库条数和页面展示条数不一样,所以不能用简单的分页查询
        int from = (page - 1) * size;
        int to = page * size;
        //size 10  from 9
        if (from >= vComEquipLevels.size()) {
            from = 0;
            to = 0;
        } else if (from < vComEquipLevels.size() && to > vComEquipLevels.size() + 1) {
            to = vComEquipLevels.size();
        }

        return DataTable.buildDataTable(vComEquipLevels.subList(from, to));

    }

    @RequestMapping(value = "/addComLevel", method = RequestMethod.GET)
    @ApiOperation(value = "保存单位等级以及可访问设备等级")
    public void addComLevel(@RequestParam @ApiParam(value = "单位等级") Integer comLevel, @RequestParam @ApiParam("设备等级") String levelJson) {

        JSONArray jsonArray = JSONArray.parseArray(levelJson);
        Integer[] equipLevels = new Integer[jsonArray.size()];
        equipLevels = jsonArray.toArray(equipLevels);
        List<ComEquipLevel> comEquipLevels = new ArrayList<>();
        for (int equipLevel : equipLevels) {
            ComEquipLevel comEquipLevel = new ComEquipLevel();
            comEquipLevel.setComLevel(comLevel);
            comEquipLevel.setEquipLevel(equipLevel);
            comEquipLevels.add(comEquipLevel);
        }

        companyService.saveComLevel(comEquipLevels);

    }

    @RequestMapping(value = "/modifyComLevel", method = RequestMethod.GET)
    @ApiOperation(value = "修改单位等级以及可访问设备等级")
    public void modifyComLevel(@RequestParam Integer comLevel, @RequestParam String levelJson) {

        if(levelJson==null||levelJson.equals("")){
            throw new CompanyException(CompanyException.EQUIP_LEVEL_IS_NULL);
        }


        companyService.modifyComLevel(comLevel,levelJson);

    }

    @RequestMapping(value = "/deleteComLevelConfig", method = RequestMethod.GET)
    @ApiOperation(value = "删除单位等级配置")
    public void deleteComLevelConfig(Integer comLevel) {

        companyService.deleteComLevel(comLevel);
    }

    @RequestMapping(value = "/findComLevelNotUsed", method = RequestMethod.GET)
    public List<VDictItem> findComLevelNotUsed() {
        List<DictItem> dictItems = dictItemService.findAll(10000007L);//单位字典
        List<ComEquipLevel> comEquipLevels = companyService.findAllComLevel();
        for (ComEquipLevel comEquipLevel : comEquipLevels) {
            for (int i = dictItems.size() - 1; i >= 0; i--) {

                if (comEquipLevel.getComLevel().longValue() == dictItems.get(i).getDictItemCode().longValue()) {
                    dictItems.remove(i);
                }
            }
        }
        List<VDictItem> vDictItems = new ArrayList<>();
        for (DictItem dictItem : dictItems) {
            vDictItems.add(VDictItem.builder()
                    .dictItemCode(dictItem.getDictItemCode())
                    .dictItemName(dictItem.getDictItemName())
                    .build());
        }
        return vDictItems;
    }

    @RequestMapping(value = "/findAllComLevel", method = RequestMethod.GET)
    public List<VDictItem> findAllComLevel() {
        List<Integer> comLevels = companyService.findAllComLevelCode();
        List<VDictItem> dictItems = new ArrayList<>(comLevels.size());
        for(Integer comLevel:comLevels){
            String levelName = dictItemService.getName(comLevel);
            dictItems.add(VDictItem.builder().dictItemCode(comLevel.longValue()).dictItemName(levelName).build());
        }

        return dictItems;
    }

//    @ExceptionHandler(NumberFormatException.class)
//    public void handleNumberFormat(NumberFormatException e) {
//        log.error("参数有误", e);
//        throw new CompanyException(CompanyException.INPUT_ERROR);
//    }
}
