package com.fable.mssg.utils;

import com.fable.mssg.domain.dsmanager.EquipAttributeBean;
import com.fable.mssg.domain.equipment.EquipAttribute;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Description HSSFWorkbook工具类
 * @Author wangmeng 2017/11/16
 */
public class HSSFUtil {

    public static Integer getInteger(HSSFCell cell) {
        if (cell == null) {
            return 0;
        }
        try {
            return (int) cell.getNumericCellValue();
        } catch (IllegalStateException e) {
            return 0;
        }

    }

    public static String getString(HSSFCell cell) {
        if (cell == null) {
            return "";
        }
        try {
            return cell.getStringCellValue();
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static Double getDouble(HSSFCell cell) {
        if (cell == null) {
            return 0.0;
        }
        try {
            return cell.getNumericCellValue();

        } catch (IllegalStateException e) {
            return 0.0;
        }
    }

    public static Date getDate(HSSFCell cell) {
        if (cell == null) {
            return null;
        }

        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd").parse(cell.getStringCellValue());
        } catch (ParseException e) {
            return null;
        }

    }


    public static String setInteger(Integer integer) {
        if (integer == null) {
            return "";
        }
        return integer.toString();
    }

    public static HSSFWorkbook exportEquipInfo(List<EquipAttribute> equipAttributes) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("equipment");
        /**
         HSSFCellStyle integerStyle = workbook.createCellStyle();
         HSSFDataFormat dataFormat = workbook.createDataFormat();
         integerStyle.setDataFormat(dataFormat.getFormat("0"));
         HSSFCellStyle doubleStyle = workbook.createCellStyle();
         doubleStyle.setDataFormat(dataFormat.getFormat("0.00"));
         HSSFCellStyle stringStype = workbook.createCellStyle();
         stringStype.setDataFormat(dataFormat.getFormat());
         **/
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("设备编码");
        row.createCell(1).setCellValue("设备名称");
        row.createCell(2).setCellValue("设备厂商");
        row.createCell(3).setCellValue("行政区域");
        row.createCell(4).setCellValue("监控点位类型");
        row.createCell(5).setCellValue("设备型号");
        row.createCell(6).setCellValue("点位俗称");
        row.createCell(7).setCellValue("IPV4地址");
        row.createCell(8).setCellValue("IPV6地址");
        row.createCell(9).setCellValue("MAC地址");
        row.createCell(10).setCellValue("摄像机类型");
        row.createCell(11).setCellValue("摄像机功能类型");
        row.createCell(12).setCellValue("补光属性");
        row.createCell(13).setCellValue("摄像机编码格式");
        row.createCell(14).setCellValue("安装地址");
        row.createCell(15).setCellValue("经度");
        row.createCell(16).setCellValue("纬度");
        row.createCell(17).setCellValue("摄像机位置类型");
        row.createCell(18).setCellValue("监控方位");
        row.createCell(19).setCellValue("联网属性");
        row.createCell(20).setCellValue("所属辖区公安机关");
        row.createCell(21).setCellValue("安装时间");
        row.createCell(22).setCellValue("管理单位");
        row.createCell(23).setCellValue("管理单位联系方式");
        row.createCell(24).setCellValue("录像保存天数");
        row.createCell(25).setCellValue("设备状态");
        row.createCell(26).setCellValue("所属部门");
        for (int i = 0; i < equipAttributes.size(); i++) {
            EquipAttribute equipAttribute = equipAttributes.get(i);
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(equipAttribute.getSbbm());
            row.createCell(1).setCellValue(equipAttribute.getSbmc());
            row.createCell(2).setCellValue(equipAttribute.getSbcs()==null?99:Integer.parseInt(equipAttribute.getJkdwlx()));
            row.createCell(3).setCellValue(equipAttribute.getXzqy());
            row.createCell(4).setCellValue(equipAttribute.getJkdwlx()==null?0:Integer.parseInt(equipAttribute.getJkdwlx()));
            row.createCell(5).setCellValue(equipAttribute.getSbxh());
            row.createCell(6).setCellValue(equipAttribute.getDwsc());
            row.createCell(7).setCellValue(equipAttribute.getIpv4());
            row.createCell(8).setCellValue(equipAttribute.getIpv6());
            row.createCell(9).setCellValue(equipAttribute.getMacdz());
            row.createCell(10).setCellValue(equipAttribute.getSxjlx());
            row.createCell(11).setCellValue(equipAttribute.getSxjgnlx());
            row.createCell(12).setCellValue(equipAttribute.getBgsx());
            row.createCell(13).setCellValue(equipAttribute.getSxjbmgs());
            row.createCell(14).setCellValue(equipAttribute.getAzdz());
            row.createCell(15).setCellValue(equipAttribute.getJd());
            row.createCell(16).setCellValue(equipAttribute.getWd());
            row.createCell(17).setCellValue(equipAttribute.getSxjwzlx()==null?99:Integer.parseInt(equipAttribute.getSxjwzlx()));
            row.createCell(18).setCellValue(equipAttribute.getJsfw());
            row.createCell(19).setCellValue(equipAttribute.getLwsx());
            row.createCell(20).setCellValue(equipAttribute.getSsxqgajg());
            row.createCell(21).setCellValue(equipAttribute.getAzsj());
            row.createCell(22).setCellValue(equipAttribute.getGldw());
            row.createCell(23).setCellValue(equipAttribute.getGldwlxfs());
            row.createCell(24).setCellValue(equipAttribute.getLxbcts()==null?0:equipAttribute.getLxbcts());
            row.createCell(25).setCellValue(equipAttribute.getSbzt());
            row.createCell(26).setCellValue(equipAttribute.getSsbmhy());
        }


        return workbook;
    }


    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\test\\test.xls");
        try {
            file.createNewFile();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("equipAttribute");
            HSSFRow row = sheet.createRow(0);
            //设置下拉框数据
            String[] arr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
            int i=0;
            for(String str:arr){
                HSSFCell cell = row.createCell(i++,HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(str);
            }


            workbook.write(new FileOutputStream(file));


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
