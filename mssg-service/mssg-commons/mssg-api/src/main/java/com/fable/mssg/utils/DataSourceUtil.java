package com.fable.mssg.utils;

import com.fable.mssg.domain.company.ComEquipLevel;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.vo.FlatDir;
import com.fable.mssg.vo.subscribe.VSubscribePrv;
import sun.java2d.pipe.SpanShapeRenderer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author wangmeng 2017/11/27
 */
public class DataSourceUtil {

    public static void filterCanView(List<ComEquipLevel> comEquipLevels, List<DataSource> dataSources) {
        boolean canViewAll = false;
        if (comEquipLevels != null) {
            for (ComEquipLevel comEquipLevel : comEquipLevels) {
                if (comEquipLevel.getEquipLevel().equals(ComEquipLevel.ALL)) {
                    canViewAll = true;
                }
            }

        }
        if (!canViewAll) {
            for (int i = dataSources.size() - 1; i >= 0; i--) {
                DataSource dataSource = dataSources.get(i);
                boolean flag = false;
                for (ComEquipLevel comEquipLevel : comEquipLevels) {
                    int dsType = dataSource.getDsType() == null ? 0 : dataSource.getDsType();
                    if (dsType != 5) {
                        flag = true;
                        //判断dataSource的级别是否为空
                    } else if (comEquipLevel.getEquipLevel().equals(dataSource.getDsLevel())) {
                        flag = true;
                    }

                }
                if (!flag) {
                    dataSources.remove(dataSource);
                }

            }

        }
    }

    public static void getRoot(VSubscribePrv vSubscribePrv, Map<String, VSubscribePrv> stringVSubscribePrvMap, Set<VSubscribePrv> roots) {

        String pid = vSubscribePrv.getPid();
        if (pid != null) {
            VSubscribePrv root = stringVSubscribePrvMap.get(pid);
            roots.add(root);
            if (root != null) {
                getRoot(root, stringVSubscribePrvMap, roots);
            }
        }

    }

    /**
     * 获取20位长度的数字id
     *
     * @return
     */
    public static int count = 1000000;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");

    public static String getSuffix() {
        count++;

        if (count > 9999999) {
            count = 1000000;
        }
        return count+"";
    }

    public static String getPrefix(){
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }


//    public static void main(String[] args) {
//        for (int i = 0;i<100;i++){
//            System.out.println(getPrefix()+"123"+getSuffix());
//
//        }
//    }

}
