package com.fable.mssg.resource.repository;

import com.fable.mssg.domain.resmanager.MediaSubscribe;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/11/14
 */
public interface MediaSubscribeRepository extends GenericJpaRepository<MediaSubscribe, String> {

    @Query(nativeQuery = true, value = "SELECT msm.* FROM mssg_sscinfo_mediainfo msm " +
            "JOIN mssg_media_info mmi ON msm.MEDIAINFO_ID = mmi.ID " +
            "WHERE mmi.DEVICE_ID = ?1")
    List<MediaSubscribe> findByMediaCode(String mediaId);

    MediaSubscribe findBySscIdAndMediaId(String sscId, String mediaId);

    List<MediaSubscribe> findBySscId(String id);
}
