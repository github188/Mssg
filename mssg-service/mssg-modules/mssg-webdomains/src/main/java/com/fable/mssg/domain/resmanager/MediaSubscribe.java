package com.fable.mssg.domain.resmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @Description
 * @Author wangmeng 2017/11/14
 */
@Data
@Entity(name = "mssg_sscinfo_mediainfo")
public class MediaSubscribe extends AbstractUUIDPersistable {
    @Column(name = "SSC_ID")
    String sscId;

    @Column(name = "MEDIAINFO_ID")
    String mediaId;

    @Column(name="SHARE_ID")
    String shareId;
}
