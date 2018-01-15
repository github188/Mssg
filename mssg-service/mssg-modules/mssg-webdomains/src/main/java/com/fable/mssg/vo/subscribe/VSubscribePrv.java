package com.fable.mssg.vo.subscribe;

import com.fable.mssg.vo.FlatDir;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */

@Data
@Builder
public class VSubscribePrv {

    //元数据信息从实体类的datasourceId获取
    private String dsId; //datasource主键ID

    private String pid; //父id

    private String dsName; //数据源名称,

    private String dsCode;  //数据源ID   标准定义，长度不超过20

    private Integer dsType; //数据源类型  1:行政区划目录  2:系统目录 3:业务分组目录 4:虚拟目录 5:设备目录 6:自定义目录

    private Integer locationType;

    private Integer realPlay;

    private Integer realControl;

    private Integer playBack;

    private String realBeginTime;

    private String realEndTime;

    private String hisBeginTime;

    private String hisEndTime;

    private Integer record;

    private Integer realSnap;

    private Integer histSnap;

    private Integer download;

    private String realTime;

    private String histTime;

    private Integer dsLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VSubscribePrv that = (VSubscribePrv) o;

        return dsCode.equals(that.dsCode);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + dsCode.hashCode();
        return result;
    }
}
