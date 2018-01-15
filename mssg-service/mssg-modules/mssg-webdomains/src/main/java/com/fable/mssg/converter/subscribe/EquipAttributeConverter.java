package com.fable.mssg.converter.subscribe;

import com.fable.mssg.domain.dsmanager.EquipAttributeBean;
import com.fable.mssg.vo.equipment.VEquipAttribute;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
@Service
public class EquipAttributeConverter extends RepoBasedConverter<EquipAttributeBean, VEquipAttribute, String> {
    @Override
    protected VEquipAttribute internalConvert(EquipAttributeBean source) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return VEquipAttribute.builder()
                .id(source.getId())
                .azdz(source.getAzdz())
                .azsj(source.getAzsj()==null?"":sdf.format(source.getAzsj()))
                .bgsx(getbgsx(source.getBgsx()==null?"":source.getBgsx()))
                .dwsc(source.getDwsc())
                .gldw(source.getGldw())
                .gldwlxfs(source.getGldwlxfs())
                .ipv4(source.getIpv4())
                .ipv6(source.getIpv6())
                .jd(source.getJd())
                .jkdwlx(getjkdwlx(source.getJkdwlx()==null?"":source.getJkdwlx()))
                .jsfw(getjsfw(source.getJsfw()==null?"":source.getJsfw()))
                .lwsx(getlwsx(source.getLwsx()==null?"":source.getLwsx()))
                .lxbcts(source.getLxbcts())
                .macdz(source.getMacdz())
                .sbbm(source.getSbbm())
                .sbcs(getsbcs(source.getSbcs()==null?"":source.getSbcs()))
                .sbmc(source.getSbmc())
                .sbxh(source.getSbxh())
                .sbzt(getsbzt(source.getSbzt()==null?"":source.getSbzt()))
                .ssbmhy(getssbmhy(source.getSsbmhy()==null?"":source.getSsbmhy()))
                .ssxqgajg(source.getSsxqgajg())
                .sxjbmgs(getsxjbmgs(source.getSxjbmgs()==null?"":source.getSxjbmgs()))
                .sxjgnlx(getsxjgnlx(source.getSxjgnlx()==null?"":source.getSxjgnlx()))
                .sxjlx(getsxjlx(source.getSxjlx()==null?"":source.getSxjlx()))
                .sxjwzlx(getsxjwzlx(source.getSxjwzlx()==null?"":source.getSxjwzlx()))
                .wd(source.getWd())
                .xzqy(source.getXzqy()+"")
                .build()
                ;
    }

    public static String getsbcs(String sbcs) {
        switch (sbcs) { //
            case "1":
                return "海康威视";
            case "2":
                return "大华";
            case "3":
                return "天地伟业";
            case "4":
                return "科达";
            case "5":
                return "安讯士";
            case "6":
                return "博世";
            case "7":
                return "亚安";
            case "8":
                return "英飞拓";
            case "9":
                return "宇视";
            case "10":
                return "海信";
            case "11":
                return "中星电子";
            case "12":
                return "明景";
            case "13":
                return "联想";
            case "14":
                return "中兴";
            case "15":
                return "华为";
            default:
                return "其他";
        }

    }
    public String getsxjlx(String sxjlx) {
        switch (sxjlx) { //1-球机；2-半球；3-固定枪机；4-遥控枪机；5.卡口枪机；99.未知
            case "1":
                return "球机";
            case "2":
                return "半球";
            case "3":
                return "固定枪机";
            case "4":
                return "遥控枪机";
            case "5":
                return "卡口枪机";
            default:
                return "未知";
        }

    }
    public String getjkdwlx(String jkdwlx) {
        switch (jkdwlx) { //1.一类视频监控点；2.二类视频监控点； 3.三类视频监控点； 4 公安内部视频监控点；9.其他点位
            case "1":
                return "一类视频监控点";
            case "2":
                return "二类视频监控点";
            case "3":
                return "三类视频监控点";
            case "4":
                return "公安内部视频监控点";
            default:
                return "其他点位";
        }

    }

    public String getsxjgnlx(String sxjgnlx) {
        String []s=sxjgnlx.split("/");
        String type="";
        for(int i=0;i<s.length;i++){
            switch (s[i]) { //1、 车辆卡口； 2、 人员卡口；3、 微卡口； 4、 特征摄像机；5、 普通监控；6、高空瞭望摄像机； 99其他， 多选各参数以
                case "1":
                    type+="车辆卡口/";
                    break;
                case "2":
                    type+= "人员卡口/";
                    break;
                case "3":
                    type+= "微卡口/";
                    break;
                case "4":
                    type+= "特征摄像机/";
                    break;
                case "5":
                    type+= "普通监控/";
                    break;
                case "6":
                    type+= "普通监控/";
                    break;
                default:
                    type+= "其他/";
            }

        }
        type=type.substring(0,type.lastIndexOf("/"));
        return type;
    }

    public String getbgsx(String bgsx) {
        switch (bgsx) { //1-无补光、2-红外补光、3-白光补光、9-其他补光
            case "1":
                return "无补光";
            case "2":
                return "红外补光";
            case "3":
                return "白光补光";
            default:
                return "其他补光";
        }

    }

    public String getsxjbmgs(String sxjbmgs) {
        switch (sxjbmgs) { //1.MPEG-4； 2.H.264； 3.SVAC； 4.H.265
            case "1":
                return "MPEG-4";
            case "2":
                return "H.264";
            case "3":
                return "SVAC";
            case "4":
                return "H.265";
            default:
                return "";
        }

    }

    public String getsxjwzlx(String sxjwzlx) {
        String[] s = sxjwzlx.split("/");
        String type = "";
        for (int i = 0; i < s.length; i++) {
            switch (s[i]) {// 1-省际检查站、2-党政机关、3-车站码头、4-中心广场、5-体育场馆、6-商业中心、7-宗教场所、8-校园周边、9-治安复杂区域、10-交通干线、11-医院周边、12-金融机构周边、13-危险物品场所周边、14-博物馆展览馆、15-重点水域、航道、96.市际公安检查站；97.涉外场所；98.边境沿线；99.旅游景区， 多选各参数以“ /” 分隔
                case "1":
                    type += "省际检查站/";
                    break;
                case "2":
                    type += "党政机关/";
                    break;
                case "3":
                    type += "车站码头/";
                    break;
                case "4":
                    type += "中心广场/";
                    break;
                case "5":
                    type += "体育场馆/";
                    break;
                case "6":
                    type += "商业中心/";
                    break;
                case "7":
                    type += "宗教场所/";
                    break;
                case "8":
                    type += "校园周边/";
                    break;
                case "9":
                    type += "治安复杂区域/";
                    break;
                case "10":
                    type += "交通干线/";
                    break;
                case "11":
                    type += "医院周边/";
                    break;
                case "12":
                    type += "金融机构周边/";
                    break;
                case "13":
                    type += "危险物品场所周边/";
                    break;
                case "14":
                    type += "博物馆展览馆/";
                    break;
                case "15":
                    type += "重点水域、航道/";
                    break;
                case "96":
                    type += "市际公安检查站/";
                    break;
                case "97":
                    type += "涉外场所/";
                    break;
                case "98":
                    type += "边境沿线/";
                    break;
                case "99":
                    type += "旅游景区/";
                    break;
                default:
                    type += "其他/";
            }

        }
        type = type.substring(0, type.lastIndexOf("/"));
        return type;
    }

    public String getjsfw(String jsfw) {
        switch (jsfw) { //1-东、2-西、3-南、4-北、5-东南、6-东北、7-西南、8-西北。9.全向
            case "1":
                return "东";
            case "2":
                return "西";
            case "3":
                return "南";
            case "4":
                return "北";
            case "5":
                return "东南";
            case "6":
                return "东北";
            case "7":
                return "西南";
            case "8":
                return "西北";
            default:
                return "全向";
        }

    }

    public String getlwsx(String lwsx) {
        switch (lwsx) {
            case "1":
                return "已联网";
            default:
                return "未联网";
        }

    }
    public String getsbzt(String sbzt) {
        switch (sbzt) {//1.在用；2.维修；3.拆除。
            case "1":
                return "在用";
            case "2":
                return "维修";
            default:
                return "拆除";
        }

    }
    public String getssbmhy(String ssbmhy) {
        String[] s = ssbmhy.split("/");
        String type = "";
        for (int i = 0; i < s.length; i++) {
            switch (s[i]) {// 1.公安机关； 2.环保部门;3.文博部门;4.医疗部门;5.旅游管理;6.新闻广电;7.食品医药监督管理部门;8.教育管理部门;9.检察院;10.法院;11.金融部门;12.交通部门;13.住房和城乡建设部门;14.水利部门;15.林业部门;16.安全生产监督部门;17.市政市容委;18.国土局,可扩展， 多选各参数以“ /” 分隔
                case "1":
                    type += "公安机关/";
                    break;
                case "2":
                    type += "环保部门/";
                    break;
                case "3":
                    type += "文博部门/";
                    break;
                case "4":
                    type += "医疗部门/";
                    break;
                case "5":
                    type += "旅游管理/";
                    break;
                case "6":
                    type += "新闻广电/";
                    break;
                case "7":
                    type += "食品医药监督管理部门/";
                    break;
                case "8":
                    type += "教育管理部门/";
                    break;
                case "9":
                    type += "检察院/";
                    break;
                case "10":
                    type += "法院/";
                    break;
                case "11":
                    type += "金融部门/";
                    break;
                case "12":
                    type += "交通部门/";
                    break;
                case "13":
                    type += "住房和城乡建设部门/";
                    break;
                case "14":
                    type += "水利部门/";
                    break;
                case "15":
                    type += "林业部门/";
                    break;
                case "16":
                    type += "安全生产监督部门/";
                    break;
                case "17":
                    type += "市政市容委/";
                    break;
                case "18":
                    type += "国土局/";
                    break;
                default:
                    type += "其他/";
            }

        }
        type = type.substring(0, type.lastIndexOf("/"));
        return type;

    }


}
