package com.fable.mssg.domain.equipment;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

/**
 * @Description
 * @Author wangmeng 2017/11/15
 */
@Entity
@Table(name = "mssg_equip_attribute")
@Data
public class EquipAttribute extends AbstractUUIDPersistable {

    @Column(name = "SBBM", nullable = true, length = 20)
    private String sbbm;
    @Column(name = "SBMC", nullable = true, length = 100)
    private String sbmc;
    @Column(name = "SBCS", nullable = true, length = 2)
    private Integer sbcs;
    @Column(name = "XZQY", nullable = true, length = 6)
    private String xzqy;
    @Column(name = "JKDWLX", nullable = true, length = 1)
    private String jkdwlx;
    @Column(name = "SBXH", nullable = true, length = 50)
    private String sbxh;
    @Column(name = "DWSC", nullable = true, length = 100)
    private String dwsc;
    @Column(name = "IPV4", nullable = true, length = 30)
    private String ipv4;
    @Column(name = "IPV6", nullable = true, length = 64)
    private String ipv6;
    @Column(name = "MACDZ", nullable = true, length = 32)
    private String macdz;
    @Column(name = "SXJLX", nullable = true, length = 2)
    private Integer sxjlx;
    @Column(name = "SXJGNLX", nullable = true, length = 30)
    private Integer sxjgnlx;
    @Column(name = "BGSX", nullable = true, length = 1)
    private Integer bgsx;
    @Column(name = "SXJBMGS", nullable = true, length = 1)
    private Integer sxjbmgs;
    @Column(name = "AZDZ", nullable = true, length = 100)
    private String azdz;
    @Column(name = "JD", nullable = true, precision = 6)
    private Double jd;
    @Column(name = "WD", nullable = true, precision = 6)
    private Double wd;
    @Column(name = "SXJWZLX", nullable = true, length = 50)
    private String sxjwzlx;
    @Column(name = "JSFW", nullable = true, length = 1)
    private Integer jsfw;
    @Column(name = "LWSX", nullable = true, length = 1)
    private Integer lwsx;
    @Column(name = "SSXQGAJG", nullable = true, length = 12)
    private String ssxqgajg;
    @Column(name = "AZSJ", nullable = true)
    private Date azsj;
    @Column(name = "GLDW", nullable = true, length = 100)
    private String gldw;
    @Column(name = "GLDWLXFS", nullable = true, length = 30)
    private String gldwlxfs;
    @Column(name = "LXBCTS", nullable = true)
    private Integer lxbcts;
    @Column(name = "SBZT", nullable = true, length = 1)
    private Integer sbzt;
    @Column(name = "SSBMHY", nullable = true, length = 50)
    private Integer ssbmhy;


}
