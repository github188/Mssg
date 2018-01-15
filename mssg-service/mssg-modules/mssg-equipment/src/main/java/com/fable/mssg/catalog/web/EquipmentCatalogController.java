package com.fable.mssg.catalog.web;

import com.alibaba.fastjson.JSONArray;
import com.fable.mssg.catalog.converter.EquipmentCatalogConverter;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.service.exception.EquipmentCatalogException;
import com.fable.mssg.domain.dsmanager.EquipAttributeBean;
import com.fable.mssg.domain.dsmanager.Originalds;
import com.fable.mssg.service.datasource.OriginaldsService;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.equipment.VEquipment;
import com.fable.mssg.vo.orginalds.VEquipmentCatalogBean;
import com.fable.mssg.converter.subscribe.EquipAttributeConverter;
import com.fable.mssg.service.datasource.EquipAttributeService;
import com.fable.mssg.vo.DataTable;
import com.fable.mssg.vo.equipment.VEquipAttribute;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wangmeng 2017/11/8
 */
@Secured(LoginUtils.ROLE_USER)
@Api(value = "原始设备管理接口", description = "原始设备接口")
@RestController
@RequestMapping("/equipment")
@Slf4j
public class EquipmentCatalogController {

    @Autowired
    EquipmentCatalogService equipmentCatalogService;
    @Autowired
    EquipAttributeService equipAttributeService;

    @Autowired
    EquipmentCatalogConverter equipmentCatalogConverter;

    @Autowired
    EquipAttributeConverter equipAttributeConverter;
    @Autowired
    OriginaldsService originaldsService;
    @Autowired
    DictItemService dictItemService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询所有原始设备")
    public List<VEquipmentCatalogBean> findAll() {
        return equipmentCatalogConverter.convert(equipmentCatalogService.findAll());
    }

    @RequestMapping(value = "/findByFilter", method = RequestMethod.GET)
    @ApiOperation(value = "根据媒体平台查询所有原始设备")
    public List<VEquipmentCatalogBean> findAll(String mediaJson, String eqLevel, String position, String eqName) {
        String[] mediaIds = mediaJson.split(",");
        Integer[] eqLevels = getIntegerArray(eqLevel);
        Integer[] positions = getIntegerArray(position);


        return equipmentCatalogConverter.convert(equipmentCatalogService.findByFilter(mediaIds, eqLevels, positions, eqName));
    }

    private Integer[] getIntegerArray(String str) {
        Integer[] results = new Integer[0];
        if (null != str && !"".equals(str)) {
            String[] temps = str.split(",");
            results = new Integer[temps.length];
            for (int i = 0; i < results.length; i++) {
                results[i] = Integer.parseInt(temps[i]);
            }
        }
        return results;
    }


    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ApiOperation(value = "导入摄像设备信息")
    public String importEquipInfo(@RequestParam MultipartFile multipartFile) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();

            int[] results = equipmentCatalogService.importEquipInfo(inputStream);
            return "导入成功:" + results[0] + "条,导入失败:" + results[1] + "条";
        } catch (IOException e) {
            log.error("获取摄像设备信息文件出错", e);
            throw new EquipmentCatalogException(EquipmentCatalogException.EQUIPMENT_IMPORT_EXP);
        }
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出")
    public void exportEquipInfo(HttpServletResponse response) {
        HSSFWorkbook workbook = equipmentCatalogService.exportEquipInfo();
        OutputStream outputStream = null;
        try {
            response.setContentType("application/force-download");// 设置强制下载打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + "EquipmentInfo.xls");// 设置文件名
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("导出异常", e);
            throw new EquipmentCatalogException(EquipmentCatalogException.EQUIPMENT_EXPORT_EXP);
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.warn("关闭流出错", e);
            }
        }

    }

    @ApiOperation(value = "分页查询摄像设备信息", notes = "分页查询摄像设备信息")
    @RequestMapping(value = "/findAllPageByCondition", method = RequestMethod.GET)
    public DataTable findAllPageByCondition(String size, String page, String dsName, String jkdwlx, String locationType
            , String mediaDeviceId) {
        Map map = equipmentCatalogService.findAllPageByCondition(Integer.valueOf(size), Integer.valueOf(page), dsName, jkdwlx, locationType, mediaDeviceId);
        DataTable dataTable = new DataTable();
        dataTable.setRecordsFiltered(Long.valueOf((int) map.get("count")));
        dataTable.setRecordsTotal(Long.valueOf((int) map.get("count")));
        List<VEquipment> vlist = (List) map.get("vslist");
        for (VEquipment vEquipment : vlist) {
            vEquipment.setManuName(EquipAttributeConverter.getsbcs(vEquipment.getManuName() == null ? "" : vEquipment.getManuName()));
        }
        dataTable.setData(vlist);
        return dataTable;
    }


    /**
     * 修改设备属性
     *
     * @param equipAttribute
     */
    @ApiOperation(value = "修改设备属性", notes = "修改设备属性")
    @RequestMapping(value = "/updateEquipAttribute", method = RequestMethod.POST)
    public void updateEquipAttribute(@RequestBody VEquipAttribute equipAttribute) {
        EquipAttributeBean eb = new EquipAttributeBean();
        eb.setId(equipAttribute.getId());//id
        eb.setSbbm(equipAttribute.getSbbm());
        eb.setSsbmhy(equipAttribute.getSsbmhy());
        eb.setSbzt(equipAttribute.getSbzt());
        eb.setLxbcts(equipAttribute.getLxbcts());
        eb.setGldwlxfs(equipAttribute.getGldwlxfs());
        //安装时间
        eb.setAzsj(Timestamp.valueOf(equipAttribute.getAzsj()));
        eb.setSsxqgajg(equipAttribute.getSsxqgajg());
        eb.setLwsx(equipAttribute.getLwsx());
        eb.setJsfw(equipAttribute.getJsfw());
        eb.setSxjwzlx(equipAttribute.getSxjwzlx());
        eb.setWd(equipAttribute.getWd());
        eb.setJd(equipAttribute.getJd());
        eb.setAzdz(equipAttribute.getAzdz());
        eb.setSxjbmgs(equipAttribute.getSxjbmgs());
        eb.setBgsx(equipAttribute.getBgsx());
        eb.setSxjgnlx(equipAttribute.getSxjgnlx());
        eb.setSxjlx(equipAttribute.getSxjlx());
        eb.setMacdz(equipAttribute.getMacdz());
        eb.setIpv4(equipAttribute.getIpv4());
        eb.setIpv6(equipAttribute.getIpv6());
        eb.setDwsc(equipAttribute.getDwsc());
        eb.setSbxh(equipAttribute.getSbxh());
        eb.setJkdwlx(equipAttribute.getJkdwlx());
        eb.setSbcs(equipAttribute.getSbcs());
        eb.setXzqy(0L);
        eb.setSbmc(equipAttribute.getSbmc());
        eb.setGldw(equipAttribute.getGldw());
        equipAttributeService.updateEquiAttribute(eb);
    }


    /**
     * 查看单个设备属性信息
     *
     * @param oid
     * @return
     */
    @ApiOperation(value = "查看单个设备属性信息", notes = "查看单个设备属性信息")
    @RequestMapping(value = "/findOneEquipBydsCode", method = RequestMethod.GET)
    public List<VEquipAttribute> findOneEquipBydsCode(String oid) {
        Originalds original = originaldsService.findOne(oid);
        List<EquipAttributeBean> equ = equipAttributeService.findOneEquiBysbbm(original.getDeviceId());//根据设备编码查询
        if (equ.size() == 0) {
            List<EquipAttributeBean> equip = new ArrayList<>();
            EquipAttributeBean bean = new EquipAttributeBean();
            bean.setSbbm(original.getDeviceId());
            equip.add(bean);
            return equipAttributeConverter.convert(equip);
        } else {
            return equipAttributeConverter.convert(equ);
        }


    }


    /**
     * 查看单个设备属性信息查看按钮
     *
     * @param oid
     * @return
     */
    @ApiOperation(value = "查看单个设备属性信息查看按钮", notes = "查看单个设备属性信息查看按钮")
    @RequestMapping(value = "/findOneEquipBydsCodeForView", method = RequestMethod.GET)
    public List<VEquipAttribute> findOneEquipBydsCodeForView(String oid) {
        Originalds original = originaldsService.findOne(oid);
        List<EquipAttributeBean> equs = equipAttributeService.findOneEquiBysbbm(original.getDeviceId());//根据设备编码查询
        if (equs.size() == 0) {
            List<EquipAttributeBean> equss = new ArrayList<>();
            EquipAttributeBean equ = new EquipAttributeBean();
            equ.setSbmc(original.getDsName());
            equ.setSbbm(original.getDeviceId());//设备编码
            equss.add(equ);
            return equipAttributeConverter.convert(equss);
        } else {
            EquipAttributeBean equipAttributeBean = equs.get(0);
            List<VEquipAttribute> velist = equipAttributeConverter.convert(equs);
            for (VEquipAttribute ve : velist) {
                ve.setXzqy(dictItemService.findByDictItemCode(equipAttributeBean.getXzqy()) == null ?
                        "" : dictItemService.findByDictItemCode(equipAttributeBean.getXzqy()).getDictItemName());
            }
            return velist;

        }


    }


}
