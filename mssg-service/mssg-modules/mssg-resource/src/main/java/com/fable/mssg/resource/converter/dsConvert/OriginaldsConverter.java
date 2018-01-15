package com.fable.mssg.resource.converter.dsConvert;

import com.fable.mssg.domain.dsmanager.Originalds;
import com.fable.mssg.resource.vo.vDataSource.VOriginalds;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 *  原始数据转换类
 * @author xiejk 2017/9/30
 */
@Service
public class OriginaldsConverter extends RepoBasedConverter<Originalds,VOriginalds,String> {
    @Override
    protected VOriginalds internalConvert(Originalds source) {
        return VOriginalds.builder()
                //资源id
                .id(source.getId())
                //资源名称
                .dsName(source.getDsName())
                //资源父级id
                .pid(source.getPid())//父级id
                .dslevel(source.getDslevel())//摄像头等级
                .equipType(getEquipType(source.getEquipType()==null?0:source.getEquipType().intValue()))//摄像头类型
                .ipAddress(source.getIpAddress())//摄像头ip地址
                .locationType(getLocationType(source.getLocationType()==null?0:source.getLocationType().intValue()))//摄像头位置
                .manuName(source.getManuName())//厂商名称
                .visitCount(source.getDeviceVistList().size())//访问次数
                .deviceId(source.getDeviceId())
                .build();
    }

    String getEquipType(int equipType){
        switch(equipType){
            case 1:  return "球机";
            case 2: return  "半球";
            case 3: return  "固定枪机";
            case 4: return  "遥控枪";
            default: return "1其他";
        }
    }

    String getLocationType(int locationType){
        switch(locationType){
            case 1:  return "省际检查站";
            case 2: return  "党政机关";
            case 3: return  "车站码头";
            case 4: return  "中心广场";
            case 5: return  "体育场馆";
            case 6: return  "商业中心";
            case 7: return  "宗教场所";
            case 8: return  "校园周边";
            case 9: return  "治安复杂区域";
            case 10: return  "交通干线";
            default: return "其他";
        }
    }

}
