package com.fable.mssg.service.resource;


import com.fable.mssg.domain.mediainfo.MediaInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * description  流媒体平台接口
 * @author xiejk 2017/9/30
 */
public interface MediaInfoService {

    /**
     * 分页查询平台媒体信息
     * @param mediaName  媒体流平台名称
     * @param page 当前页数
     * @param size 每页显示的数量
     * @return  Page<MediaInfo>
     */
    Page<MediaInfo> findAllPageMediaInfoByCondition(String mediaName,Long mediaType, int page, int size);

    Page<MediaInfo> findAllPageMediaInfoByCondition(String mediaName, int page, int size);

    /**
     * 查询单个的流媒体信息
     * @param mid 流媒体id号
     * @return  流媒体对象
     */
    MediaInfo findOneMediaInfo(String mid);

    /**
     * 修改媒体流信息
     * @param mediaInfo  流媒体对象
     * @return  是否修改成功
     */
    boolean updateMediaInfo(MediaInfo mediaInfo);

    /**
     * 新增媒体流信息
     * @param mediaInfo  流媒体对象
     * @return  是否新增成功
     */
    boolean  insertMedia(MediaInfo mediaInfo);

    /**
     * 删除媒体流信息
     * @param mid  流媒体id号
     * @return 是否删除成功
     */
    boolean  delMedia(String mid);


    List<MediaInfo> findAll();

    MediaInfo findByDeviceId(String sipId);

    List<MediaInfo> findByCompany(String subscribeId,String comId);

    List<MediaInfo> findLowLevel();
}
