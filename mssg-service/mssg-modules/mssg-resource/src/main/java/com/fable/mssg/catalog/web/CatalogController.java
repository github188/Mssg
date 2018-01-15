package com.fable.mssg.catalog.web;

import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.catalog.converter.CatalogConverter;
import com.fable.mssg.catalog.service.CatalogService;
import com.fable.mssg.catalog.vo.VCatalog;
import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.sql.Timestamp;


/**
 * @Description
 * @Author wangmeng 2017/10/10
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/catalog")
@Slf4j
@Api(value = "目录管理", description = "目录管理")
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    @Autowired
    CatalogConverter catalogConverter;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ApiOperation(value = "新增目录")
    public void addCatalog(String cataName, String cataCode, String description,
                           String approvalId, HttpSession session) {
        Catalog catalog = new Catalog();
        SysUser sysUser = new SysUser();
        sysUser.setId(approvalId);
        catalog.setApproval(sysUser);
        catalog.setCatalogCode(cataCode);
        catalog.setCatalogName(cataName);
        catalog.setDescription(description);
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser createUser = loginUserInfo.getSysUser();
        catalog.setCreateUser(createUser);
        catalog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        catalog.setComId(createUser.getComId());
        catalogService.save(catalog);
    }

    @ApiOperation(value = "删除目录")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void deleteCatalog(String id) {
        catalogService.delete(id);
    }

    @ApiOperation(value = "查询所有目录")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public List<VCatalog> findAll() {
        return catalogConverter.convert(catalogService.findAll());
    }

    @ApiOperation(value = "修改目录")
    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public void modifyCatalog(String id, String cataName, String cataCode,
                              String description, String approvalId, HttpSession session) {

        Catalog catalog = new Catalog();
        catalog.setId(id);
//        SysUser sysUser = new SysUser();
//        sysUser.setId(approvalId);
        //  catalog.setApproval(sysUser);
        catalog.setCatalogCode(cataCode);
        catalog.setCatalogName(cataName);
        catalog.setDescription(description);
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser updateUser = loginUserInfo.getSysUser();
        catalog.setUpdateUser(updateUser);
        catalog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        SysUser sysUser = new SysUser();
        sysUser.setId(approvalId);
        catalog.setApproval(sysUser);
        catalogService.modify(catalog);

    }

    @ApiOperation(value = "根据id查询目录")
    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    public VCatalog findById(String id) {
        return catalogConverter.convert(catalogService.findById(id));
    }

}
