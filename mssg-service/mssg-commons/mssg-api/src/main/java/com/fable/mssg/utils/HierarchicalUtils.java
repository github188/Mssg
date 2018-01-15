package com.fable.mssg.utils;


import com.fable.mssg.vo.FlatDir;

import java.util.*;

/**
 * @Description 层及目录工具
 * @Author wangmeng 2017/09/01
 */
public class HierarchicalUtils{
    /**
     * 扁平到层级
     * @param sources 源目录
     * @param parentId 最高级父id(null)
     * @return 层级目录
     */
    public static List<FlatDir> toHierarchical(List<FlatDir> sources, String parentId){
        List<FlatDir> result=new ArrayList<>();
        for(int i=sources.size()-1;i>=0;i--){
            FlatDir source=sources.get(i);
            if(parentId==null && source.parentId()==null
                    ||parentId!=null&&parentId.equals(source.parentId())){
                //sources.remove(i);
                List<FlatDir> child=toHierarchical(sources,source.thisId());
                i-=child.size();
                source.getChildren().addAll(child);
                result.add(source);

            }
        }
        return result;
    }

    /**
     * 层级到扁平
     * @param source 源List层级目录
     * @return 结果
     */
    public static List<FlatDir> toFlat(List<FlatDir> source){
        List<FlatDir> result = new ArrayList<>();
        for(FlatDir dir:source){
            List child=dir.getChildren();
            result.add(dir);
            if(null!=child && 0!=child.size()) {
                result.addAll(toFlat(dir.getChildren()));
            }

        }

        return result;
    }


}
