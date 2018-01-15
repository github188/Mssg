package com.fable.mssg.resource.repository;


import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 流媒体平台dao层
 *
 * @author xiejk 2017/9/30
 */
public interface MediaInfoRepository extends GenericJpaRepository<MediaInfo, String>, JpaSpecificationExecutor<MediaInfo> {


    List<MediaInfo> findByComId(String comId);

    List<MediaInfo> findByComIdAndMediaType(String comId, long i);
}
