package com.fable.mssg.domain.resmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @Description
 * @Author wangmeng 2017/11/20
 */
@Entity
@Data
@Table(name = "mssg_res_config")
public class ResourceConfig extends AbstractUUIDPersistable {

    @Column(name = "REAL_PLAY", nullable = true)
    private String realPlay;

    @Column(name = "REAL_CONTROL", nullable = true)
    private Integer realControl;

    @Column(name = "PLAY_BACK", nullable = true)
    private String playBack;

    @Column(name = "RECORD", nullable = true)
    private Integer record;

    @Column(name = "REAL_SNAP", nullable = true)
    private Integer realSnap;

    @Column(name="HIST_SNAP")
    private Integer histSnap;

    @Column(name="DOWNLOAD")
    private Integer download;


}
