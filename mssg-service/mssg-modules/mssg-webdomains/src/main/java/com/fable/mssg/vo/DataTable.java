package com.fable.mssg.vo;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Description 前端分页对象
 * @Author wangmeng 2017/9/14
 */
@Data
public class DataTable<T> {

    long recordsTotal;     //总条数
    long recordsFiltered;     //总过滤后条数
    List<T> data;

    public static DataTable buildDataTable(Page source){
        DataTable dataTable=new DataTable();
        dataTable.setRecordsTotal(source.getTotalElements());
        dataTable.setRecordsFiltered(source.getTotalElements());
        dataTable.setData(source.getContent());
        return dataTable;
    }

    public static DataTable buildDataTable(List source){
        DataTable dataTable=new DataTable();
        dataTable.setData(source);
        dataTable.setRecordsFiltered(source.size());
        dataTable.setRecordsTotal(source.size());
        return dataTable;
    }
}
