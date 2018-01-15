package com.fable.mssg.resource.web.dict;

import com.fable.mssg.domain.dictmanager.Dict;
import com.fable.mssg.resource.converter.dictConverter.DictConverter;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.service.dict.DictService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.dict.VDict;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * description  字典控制器
 * @author xiejk 2017/11/06
 */
@RestController
@RequestMapping("/dict")
@Api(value = "字典控制器",description = "字典控制器")
@Slf4j
@Secured(LoginUtils.ROLE_USER)
public class DictController {

    @Autowired
    DictService dictService;

    @Autowired
    DictConverter dictConverter;

    @Autowired
    DictItemService dictItemService;

    /**
     * 查询全部字典信息
     * @return
     */
    @ApiOperation(value = "查询全部字典信息",notes = "查询全部字典信息")
    @RequestMapping(value = "/findAllDict",method = RequestMethod.GET)
    public List<VDict> findAllDict(){
        return   dictConverter.convert(dictService.findAllDict());
    }

    /**
     * 分页查询字典接口
     * @param dictName 字典名称
     * @param dictCode 字典代码
     * @param size 每页显示个数
     * @param page 页数
     * @return DataTable
     */
    @ApiOperation(value = "分页查询字典接口",notes = "分页查询字典接口")
    @RequestMapping(value = "/findAllPageDiceByCondition",method = RequestMethod.GET)
    public DataTable findAllPageDiceByCondition(String dictName, String dictCode, @RequestParam int size, @RequestParam int page){
        Page<VDict> vdict= dictService.findAllPageDict(dictName,dictCode,size,page).map(dictConverter);
        return  DataTable.buildDataTable(vdict);
    }

    /**
     * 新增字典
     * @param dictName 字典名称
     * @return String
     */
    @ApiOperation(value = "新增字典",notes = "新增字典")
    @RequestMapping(value = "/insertDict",method = RequestMethod.POST)
    public String insertDict(@RequestParam String dictName,@RequestParam String dictRemark){
        String message;
        if(dictService.findDictByName(dictName)){
            Dict d=new Dict();
            d.setDictName(dictName);
            d.setDictRemark(dictRemark);
            //d.setCreateUser("");
            d.setCreateTime(new Timestamp(System.currentTimeMillis()));
            if(dictService.insertDict(d)){
                message="新增字典成功";
            }else{
                message="新增字典失败";
            }
        }else{
            message="名称已经存在";
        }
        return  message;
    }


    /**
     * 删除字典信息
     * @param dirtid 字典id
     * @return message
     */
    @ApiOperation(value = "删除字典信息",notes = "删除字典信息")
    @RequestMapping(value = "delDirt",method = RequestMethod.GET)
    public String delDirt(@RequestParam String dirtid){
        Dict d=dictService.findOneDict(dirtid);
        String message;
        if(d!=null){
            if(dictItemService.findAllByDictCode(d.getDictCode()))
            {
                if(dictService.delDict(dirtid))
                {
                    message="删除字典成功";
                }else{
                    message="删除字典失败";
                }
            }else{
                message="存在数据，不允许删除";
            }
        }else{
            message="数据不存在";
        }
        return  message;
    }


    /**
     * 导入字典的功能  只能导入2003版的表格
     * @param dictfile
     * @return
     */
    @ApiOperation(value = "导入字典",notes = "导入字典")
    @RequestMapping(value = "leadIn",method = RequestMethod.POST)
    public String leadIn(@RequestParam MultipartFile dictfile){
        String message;
        if(!dictfile.isEmpty()){
            try {
                message=dictService.leadIn(dictfile.getInputStream());
            } catch (Exception e) {
               throw  new RuntimeException("导入异常",e);
            }
        }else{
            message="文件是空的！";
        }
        return  message;
    }

    /**
     * 导出字典
     * @param res
     */
    @ApiOperation(value = "导出字典",notes = "导出字典")
    @RequestMapping(value = "/leadout", method = RequestMethod.GET)
    public void leadOut(HttpServletResponse res) {
        HSSFWorkbook workbook = dictService.leadOut();
        OutputStream out = null;
        try {
            res.setContentType("application/force-download");// 设置强制下载打开
            res.addHeader("Content-Disposition", "attachment;fileName=" + "dict.xls");// 设置文件名
            out = res.getOutputStream();
            workbook.write(out);
        } catch (IOException e) {
            log.error("导出异常{}", e);
            e.getMessage();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("导出异常{}", e);
            }
        }
    }
//
//    @ExceptionHandler(Exception.class)
//    public String handle(Exception e) {
//        log.error("单位异常{}", e);
//        return e.getMessage();
//    }





}









