package com.fable.mssg.resource.service.datasource.impl;

import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.dsmanager.EquipAttributeBean;
import com.fable.mssg.resource.repository.datasource.EquiAttributeRepository;
import com.fable.mssg.service.datasource.DataSourceService;
import com.fable.mssg.service.datasource.EquipAttributeService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class EquipAttributeServiceImpl   implements EquipAttributeService {

    @Autowired
    EquiAttributeRepository equiAttributeRepository;

    @Autowired
    DataSourceService dataSourceService;

    /**
     * 根据设备编码查询
     *
     * @return
     */
    @Override
    public List<EquipAttributeBean> findOneEquiBysbbm(String sbbm) {
        return equiAttributeRepository.findAllBySbbm(sbbm);
    }


    /**
     * 修改设备属性信息
     * @param equipAttributeBean
     * @return
     */
    @Override
    public EquipAttributeBean updateEquiAttribute(EquipAttributeBean equipAttributeBean) {

        //修改DataSource表的属
        List<DataSource> dataSources = dataSourceService.findByDeviceId(equipAttributeBean.getSbbm());
        for(DataSource dataSource:dataSources) {
            if(null!=equipAttributeBean.getJkdwlx()&&!"".equals(equipAttributeBean.getJkdwlx())) {
                int i = Integer.parseInt(equipAttributeBean.getJkdwlx());
                dataSource.setDsLevel(i);
            }
            dataSource.setLocationType(Integer.parseInt(equipAttributeBean.getSxjwzlx()));
        }
        dataSourceService.save(dataSources);

        return equiAttributeRepository.save(equipAttributeBean);
    }


    /**
     * 导入设备属性
     * @param inputStream
     * @return
     */
    @Override
    public String leadIn(InputStream inputStream) {
        String message;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            List<EquipAttributeBean> eqi = new ArrayList<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                EquipAttributeBean e = new EquipAttributeBean();
                e.setSbbm(getString(row.getCell(0)));//设备编码
                e.setSbmc(getString(row.getCell(1)));//设备名称
                e.setSbcs(getString(row.getCell(2)));//设备厂商
                e.setXzqy(Long.valueOf(getString(row.getCell(3))));//行政区域
                e.setJkdwlx(getString(row.getCell(4)));//监控点位置类型
                e.setSbxh(getString(row.getCell(5)));//设备型号
                e.setDwsc(getString(row.getCell(6)));//点位俗称
                e.setIpv4(getString(row.getCell(7)));//IPV4地址
                e.setIpv6(getString(row.getCell(8)));//IPV6地址
                e.setMacdz(getString(row.getCell(9)));//MAC地址
                e.setSxjlx(getString(row.getCell(10)));//摄像机类型
                e.setSxjgnlx(getString(row.getCell(11)));//摄像机功能类型
                e.setBgsx(getString(row.getCell(12)));//补光属性
                e.setSxjbmgs(getString(row.getCell(13)));//摄像机编码格式
                e.setAzdz(getString(row.getCell(14)));//安装地址
                e.setJd(getDouble(row.getCell(15)));//经度
                e.setWd(getDouble(row.getCell(16)));//纬度
                e.setSxjwzlx(getString(row.getCell(17)));//摄像机位置类型
                e.setJsfw(getString(row.getCell(18)));//监视方位
                e.setLwsx(getString(row.getCell(19)));//联网属性
                e.setSsxqgajg(getString(row.getCell(20)));//所属辖区公安机关
                e.setAzsj(Timestamp.valueOf(getString(row.getCell(21))));//安装时间
                e.setGldw(getString(row.getCell(22)));//管理单位
                e.setGldwlxfs(getString(row.getCell(23)));//管理单位联系方式
                e.setLxbcts(Long.valueOf(getString(row.getCell(24))));//录像保存天数
                e.setSbzt(getString(row.getCell(25)));//设备状态
                e.setSsbmhy(getString(row.getCell(26)));//所属部门/行业
                eqi.add(e);
            }
            equiAttributeRepository.save(eqi);
            message="导入成功";
        } catch (IOException e) {
            e.printStackTrace();
            message=e.getMessage();
        }
        return  message;
    }


    //从cell中读取int
    private Double getDouble(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return (double) cell.getNumericCellValue();
        }
    }

    //从cell中读取String
    private String getString(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return cell.getStringCellValue();

        }

    }
}
