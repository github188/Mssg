package com.fable.mssg.catalog.service.impl;

import com.fable.mssg.domain.equipment.EquipAttribute;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/12/27
 */

@Service
public class EquipmentExcelService {
    // 样式
    private HSSFCellStyle integerCellStyle;

    private HSSFCellStyle doubleCellStyle;

    private HSSFCellStyle stringCellStyle;

    private HSSFCellStyle dateCellType;


    //监控点位类型 可以读数据字典
    private List<Integer> dsLevel = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 9));
    private List<Integer> locationType = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 96, 97, 98, 99));
    //厂商
    private List<Integer> mfrs = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 99));

    private List<Integer> direction = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    private List<Integer> lwsx = new ArrayList<>(Arrays.asList(0, 1));

    private List<Integer> equipStatus = new ArrayList<>(Arrays.asList(1, 2, 3));

    private List<Integer> ssbmhy = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18));

    private List<Integer> cameraType = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 99));

    private List<Integer> cameraFunctionType = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 99));

    private List<Integer> bgsx = new ArrayList<>(Arrays.asList(1, 2, 3, 9));

    private List<Integer> sxjbmgs = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

    public void setDataCellStyles(HSSFWorkbook workbook) {
        integerCellStyle = workbook.createCellStyle();
        // 设置边框
        integerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        integerCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        integerCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        integerCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置背景色
        integerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        integerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置居中
        integerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置单元格格式为文本格式（这里还可以设置成其他格式,可以自行百度）
        HSSFDataFormat format = workbook.createDataFormat();
        integerCellStyle.setDataFormat(format.getFormat("#,##0"));


        doubleCellStyle = workbook.createCellStyle();
        // 设置边框
        doubleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        doubleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        doubleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        doubleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置背景色
        doubleCellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        doubleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置居中
        doubleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置单元格格式为文本格式（这里还可以设置成其他格式,可以自行百度）
        format = workbook.createDataFormat();
        doubleCellStyle.setDataFormat(format.getFormat("#,##0.000000"));


        stringCellStyle = workbook.createCellStyle();
        // 设置边框
        stringCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        stringCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        stringCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        stringCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置背景色
        stringCellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        stringCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置居中
        stringCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        // 设置单元格格式为文本格式（这里还可以设置成其他格式,可以自行百度）
        format = workbook.createDataFormat();
        stringCellStyle.setDataFormat(format.getFormat("@"));

        dateCellType = workbook.createCellStyle();
        // 设置边框
        dateCellType.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        dateCellType.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        dateCellType.setBorderRight(HSSFCellStyle.BORDER_THIN);
        dateCellType.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设置背景色
        dateCellType.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        dateCellType.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置居中
        dateCellType.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        // 设置单元格格式为文本格式（这里还可以设置成其他格式,可以自行百度）
        format = workbook.createDataFormat();
        dateCellType.setDataFormat(format.getFormat("yyyy/m/d"));

    }

    /**
     * 创建数据域（下拉联动的数据）
     *
     * @param workbook
     * @param hideSheetName 数据域名称
     */
    private void createHideSheet(HSSFWorkbook workbook, String hideSheetName) {
        // 创建数据域
        HSSFSheet sheet = workbook.createSheet(hideSheetName);
        // 用于记录行
        int rowRecord = 0;
        // 创建范围数据
        this.createHideRow(sheet.createRow(rowRecord++), dsLevel);
        this.createHideRow(sheet.createRow(rowRecord++), locationType);
        this.createHideRow(sheet.createRow(rowRecord++), mfrs);
        this.createHideRow(sheet.createRow(rowRecord++), direction);
        this.createHideRow(sheet.createRow(rowRecord++), lwsx);
        this.createHideRow(sheet.createRow(rowRecord++), equipStatus);
        this.createHideRow(sheet.createRow(rowRecord++), ssbmhy);
        this.createHideRow(sheet.createRow(rowRecord++), cameraType);
        this.createHideRow(sheet.createRow(rowRecord++), cameraFunctionType);
        this.createHideRow(sheet.createRow(rowRecord++), bgsx);
        this.createHideRow(sheet.createRow(rowRecord++), sxjbmgs);

    }


    /**
     * 创建一列数据
     *
     * @param currentRow
     * @param values
     */
    public void createHideRow(HSSFRow currentRow, List values) {
        if (values != null) {
            int i = 0;
            for (Object cellValue : values) {
                // 注意列是从（1）下标开始
                HSSFCell cell = currentRow.createCell(i++);
                if (cellValue instanceof String) {
                    cell.setCellValue((String) cellValue);
                }
                if (cellValue instanceof Double) {
                    cell.setCellValue((Double) cellValue);
                }
                if (cellValue instanceof Integer) {
                    cell.setCellValue((Integer) cellValue);
                }
            }
        }
    }

    /**
     * 名称管理
     *
     * @param workbook
     * @param hideSheetName 数据域的sheet名
     */
    private void createExcelNameList(HSSFWorkbook workbook, String hideSheetName) {
        Name name;
        name = workbook.createName();
        int index = 1;
        // 设置监控点位类型
        name.setNameName("dsLevel");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(dsLevel.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("locationType");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(locationType.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("mfrs");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(mfrs.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("direction");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(direction.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("lwsx");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(lwsx.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("equipStatus");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(equipStatus.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("ssbmhy");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(ssbmhy.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("cameraType");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(cameraType.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("cameraFunctionType");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(cameraFunctionType.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("bgsx");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(bgsx.size()) + "$" + index++);

        name = workbook.createName();
        name.setNameName("sxjbmgs");
        name.setRefersToFormula(hideSheetName + "!$A$" + index + ":$" + this.getCellColumnFlag(sxjbmgs.size()) + "$" + index++);

    }

    // 根据数据值确定单元格位置（比如：28-AB）
    private String getCellColumnFlag(int num) {
        String columnFiled = "";
        int chuNum = 0;
        int yuNum = 0;
        if (num >= 1 && num <= 26) {
            columnFiled = this.doHandle(num);
        } else {
            chuNum = num / 26;
            yuNum = num % 26;

            columnFiled += this.doHandle(chuNum);
            columnFiled += this.doHandle(yuNum);
        }
        return columnFiled;
    }

    private String doHandle(final int num) {
        String[] charArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        return charArr[num - 1].toString();
    }


    /**
     * 使用已定义的数据源方式设置一个数据验证
     *
     * @param formulaString
     * @param fromRowIndex
     * @param toRowIndex
     * @param naturalColumnIndex
     * @return
     */
    public DataValidation getDataValidationByFormula(String formulaString, int fromRowIndex, int toRowIndex, int naturalColumnIndex) {

        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        int firstCol = naturalColumnIndex;
        int lastCol = naturalColumnIndex;
        CellRangeAddressList regions = new CellRangeAddressList(fromRowIndex, toRowIndex, firstCol, lastCol);
        // 数据有效性对象
        DataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
        return dataValidationList;
    }

    /**
     * 创建一列数据
     *
     * @param hssfSheet
     */
    public void createRow(HSSFSheet hssfSheet, int naturalRowIndex, EquipAttribute equipAttribute) {
        // 获取行
        HSSFRow row = hssfSheet.createRow(naturalRowIndex);
        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getSbbm());

        cell = row.createCell(1);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getSbmc());

        cell = row.createCell(2);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getSbcs() == null ? 99 : Integer.parseInt(equipAttribute.getJkdwlx()));

        cell = row.createCell(3);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getXzqy());

        cell = row.createCell(4);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getJkdwlx() == null ? 9 : Integer.parseInt(equipAttribute.getJkdwlx()));

        cell = row.createCell(5);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getSbxh());

        cell = row.createCell(6);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getDwsc());

        cell = row.createCell(7);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getIpv4());

        cell = row.createCell(8);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getIpv6());

        cell = row.createCell(9);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getMacdz());

        cell = row.createCell(10);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getSxjlx() == null ? 99 : equipAttribute.getSxjlx());

        cell = row.createCell(11);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getSxjgnlx() == null ? 99 : equipAttribute.getSxjgnlx());

        cell = row.createCell(12);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getBgsx() == null ? 9 : equipAttribute.getBgsx());

        cell = row.createCell(13);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getSxjbmgs() == null ? 0 : equipAttribute.getSxjbmgs());

        cell = row.createCell(14);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getAzdz());

        cell = row.createCell(15);
        cell.setCellStyle(doubleCellStyle);
        cell.setCellValue(equipAttribute.getJd() == null ? 0 : equipAttribute.getJd());

        cell = row.createCell(16);
        cell.setCellStyle(doubleCellStyle);
        cell.setCellValue(equipAttribute.getWd() == null ? 0 : equipAttribute.getWd());

        cell = row.createCell(17);
        cell.setCellValue(equipAttribute.getSxjwzlx() == null ? 99 : Integer.parseInt(equipAttribute.getSxjwzlx()));
        cell.setCellStyle(integerCellStyle);

        cell = row.createCell(18);
        cell.setCellValue(equipAttribute.getJsfw() == null ? 0 : equipAttribute.getJsfw());
        cell.setCellStyle(integerCellStyle);

        cell = row.createCell(19);
        cell.setCellValue(equipAttribute.getLwsx() == null ? 0 : equipAttribute.getLwsx());
        cell.setCellStyle(integerCellStyle);

        cell = row.createCell(20);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getSsxqgajg());

        cell = row.createCell(21);
        cell.setCellStyle(dateCellType);
        if (equipAttribute.getAzsj() != null) {
            cell.setCellValue(equipAttribute.getAzsj());
        }

        cell = row.createCell(22);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getGldw());

        cell = row.createCell(23);
        cell.setCellStyle(stringCellStyle);
        cell.setCellValue(equipAttribute.getGldwlxfs());

        cell = row.createCell(24);
        cell.setCellStyle(integerCellStyle);
        cell.setCellValue(equipAttribute.getLxbcts() == null ? 0 : equipAttribute.getLxbcts());

        cell = row.createCell(25);
        cell.setCellValue(equipAttribute.getSbzt() == null ? 0 : equipAttribute.getSbzt());
        cell.setCellStyle(integerCellStyle);

        cell = row.createCell(26);
        cell.setCellValue(equipAttribute.getSsbmhy() == null ? 0 : equipAttribute.getSsbmhy());
        cell.setCellStyle(integerCellStyle);

    }

    public HSSFWorkbook export(List<EquipAttribute> equipAttributes) {
        // 创建excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 设置sheet 名称
        HSSFSheet excelSheet = workbook.createSheet("equipment");
        // 设置样式
        this.setDataCellStyles(workbook);
        // 创建一个隐藏页和隐藏数据集
        this.createHideSheet(workbook, "hideDataSource");
        // 设置名称数据集
        this.createExcelNameList(workbook, "hideDataSource");
        // 创建一行数据
        this.createTableHead(excelSheet);
        for (int i = 0; i < equipAttributes.size(); i++) {
            this.createRow(excelSheet, i + 1, equipAttributes.get(i));
        }
        //将其他设为String格式

        //设置验证
        excelSheet.addValidationData(this.getDataValidationByFormula("dsLevel", 1, equipAttributes.size(), 4));
        excelSheet.addValidationData(this.getDataValidationByFormula("locationType", 1, equipAttributes.size(), 17));
        excelSheet.addValidationData(this.getDataValidationByFormula("mfrs", 1, equipAttributes.size(), 2));
        excelSheet.addValidationData(this.getDataValidationByFormula("direction", 1, equipAttributes.size(), 18));
        excelSheet.addValidationData(this.getDataValidationByFormula("lwsx", 1, equipAttributes.size(), 19));
        excelSheet.addValidationData(this.getDataValidationByFormula("equipStatus", 1, equipAttributes.size(), 25));
        excelSheet.addValidationData(this.getDataValidationByFormula("ssbmhy", 1, equipAttributes.size(), 26));

        excelSheet.addValidationData(this.getDataValidationByFormula("cameraType", 1, equipAttributes.size(), 10));
        excelSheet.addValidationData(this.getDataValidationByFormula("cameraFunctionType", 1, equipAttributes.size(), 11));
        excelSheet.addValidationData(this.getDataValidationByFormula("bgsx", 1, equipAttributes.size(), 12));
        excelSheet.addValidationData(this.getDataValidationByFormula("sxjbmgs", 1, equipAttributes.size(), 13));


        return workbook;
    }

    public void createTableHead(HSSFSheet sheet) {
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

    }

    public static void main(String[] args) {
        File file = new File("f:\\excel.xls");
        EquipmentExcelService equipmentExcelService = new EquipmentExcelService();
        List<EquipAttribute> equipAttributes = new ArrayList<>();
        EquipAttribute equipAttribute = new EquipAttribute();
        equipAttributes.add(equipAttribute);
        try {
            equipmentExcelService.export(equipAttributes).write(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
