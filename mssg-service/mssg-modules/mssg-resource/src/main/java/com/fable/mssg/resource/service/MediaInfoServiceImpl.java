package com.fable.mssg.resource.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.catalog.repository.MediaSourceRepository;
import com.fable.mssg.service.mediainfo.SourceCommonService;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.resource.repository.MediaInfoRepository;
import com.fable.mssg.service.resource.MediaInfoService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * description  流媒体接口实现类
 * @author  xiejk 2017/9/30
 */
@Slf4j
@Service
@Exporter(interfaces = MediaInfoService.class)
public class  MediaInfoServiceImpl implements  MediaInfoService {

    //流媒体dao层操作对象
    @Autowired
    MediaInfoRepository mediaInfoRepository;

    @Autowired
    MediaSourceRepository mediaSourceRepository;

    @Autowired
    SourceCommonService sourceCommonService;
    /**
     * 分页显示媒体流信息   slave 查询
     * @param mediaName  媒体流名称
     * @param page  当前页数
     * @param size  每页显示个数
     * @return  分页信息
     */
    @Override
    public Page<MediaInfo> findAllPageMediaInfoByCondition(String mediaName,Long mediaType, int page, int size) {
        log.info("开始查询");
        //方法是生成查询条件的 在criteria 查询中，查询条件通过Predicate或Expression实例应用到CriteriaQuery对象上
        return mediaInfoRepository.findAll(
                (root, query, cb) -> {
                    Predicate predicate=cb.equal(root.get("mediaType"),mediaType);//上级
                    //根据mediaName 加上模糊查询条件
                    if(mediaName!=null&&!mediaName.equals("")){
                        predicate=cb.and(cb.like(root.get("mediaName"),"%"+mediaName+"%"));
                    }
                    return predicate;
                },new PageRequest(page,size)
        );
    }

    @Override
    public Page<MediaInfo> findAllPageMediaInfoByCondition(String mediaName, int page, int size) {
        return mediaInfoRepository.findAll(
                (root, query, cb) -> {
                    Predicate predicate=null;//上级下级都有
                    //根据mediaName 加上模糊查询条件
                    if(mediaName!=null&&!mediaName.equals("")){
                        predicate=cb.like(root.get("mediaName"),"%"+mediaName+"%");
                    }
                    return predicate;
                },new PageRequest(page,size)
        );
    }

    /**
     * 查询单个流媒体信息
     * @param mid  流媒体id号
     * @return 单个流媒体信息
     */
    @Override
    public MediaInfo findOneMediaInfo(String mid) {
        return  mediaInfoRepository.findOne(mid);
    }

    /**
     * 修改媒体流信息
     * @param mediaInfo  流媒体对象
     * @return  是否修改成功
     */
    @Override
    @Transactional
    public boolean updateMediaInfo(MediaInfo mediaInfo) {
        //标识符
        Boolean result=false;
        try {
            mediaInfoRepository.save(mediaInfo);
            result=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 新增媒体流信息
     * @param mediaInfo  媒体流对象
     * @return 是否新增成功
     */
    @Override
    @Transactional
    public boolean insertMedia(MediaInfo mediaInfo) {
        Boolean result=false;
        try {
            mediaInfoRepository.save(mediaInfo);
            result=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除媒体流信息
     * @param mid  媒体流id
     * @return   返回是否删除成功
     */
    @Override
    @Transactional
    public boolean delMedia(String mid) {
        Boolean result=false;
        try {
            //删除orginaldatasource
            sourceCommonService.deleteOriginalDs(findOneMediaInfo(mid).getDeviceId());
            //删除datasource
            //删除媒体源
            mediaInfoRepository.delete(mid);
            result=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<MediaInfo> findAll() {
        return mediaInfoRepository.findAll();
    }

    @Override
    public MediaInfo findByDeviceId(String sipId) {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setDeviceId(sipId);

        return mediaInfoRepository.findOne(Example.of(mediaInfo));
    }

    /*
     *  暂时没用到
     */
    @Override
    public List<MediaInfo> findByCompany(String subscribeId, String comId) {
        //只查询上级
        return mediaInfoRepository.findByComIdAndMediaType(comId,1L);
    }

    @Override
    public List<MediaInfo> findLowLevel() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setMediaType(2L);

        return mediaInfoRepository.findAll(Example.of(mediaInfo));
    }

}
