package com.fable.mssg.domain.subscribemanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.dsmanager.DataSource;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Entity
@Table(name = "mssg_subscribe_prv")
@Data
public class SubscribePrv extends AbstractUUIDPersistable {

    @Column(name = "RES_SUBSCRIBE_ID", nullable = true,length = 36)
    private String resSubscribeId;

    @Column(name = "REAL_PLAY", nullable = true)
    private Integer realPlay;

    @Column(name = "REAL_CONTROL", nullable = true)
    private Integer realControl;

    @Column(name = "PLAY_BACK", nullable = true)
    private Integer playBack;

    @Column(name="REAL_PLAY_STARTIME",nullable = true)
    private Timestamp realBeginTime;

    @Column(name="REAL_PLAY_ENDTIME",nullable = true)
    private Timestamp realEndTime;

    @Column(name="PLAY_BACK_STARTIME",nullable = true)
    private Timestamp hisBeginTime;

    @Column(name="PLAY_BACK_ENDTIME",nullable = true)
    private Timestamp hisEndTime;

    @Column(name = "RECORD", nullable = true)
    private Integer record;

    @Column(name = "REAL_SNAP", nullable = true)
    private Integer realSnap;

    @Column(name="HIST_SNAP")
    private Integer histSnap;

    @Column(name="DOWNLOAD")
    private Integer download;

    @Column(name="REALTIME_STR",length = 100)
    private String realTime;

    @Column(name="HISTIME_STR",length = 100)
    private String hisTime;
    @ManyToOne
    @JoinColumn(name = "DS_ID", nullable = true,referencedColumnName ="id")
    private DataSource dsId;

}
