package com.fable.mssg.domain.dsmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "MSSG_EQUIP_ATTRIBUTE")
@Data
@Entity
public class EquipAttributeBean extends AbstractUUIDPersistable {

  @Column(name = "SBBM")
  private String sbbm;
  @Column(name = "SBMC")
  private String sbmc;
  @Column(name = "SBCS")
  private String sbcs;
  @Column(name = "XZQY")
  private Long xzqy;
  @Column(name = "JKDWLX")
  private String jkdwlx;
  @Column(name = "SBXH")
  private String sbxh;
  @Column(name = "DWSC")
  private String dwsc;
  @Column(name = "IPV4")
  private String ipv4;
  @Column(name = "IPV6")
  private String ipv6;
  @Column(name = "MACDZ")
  private String macdz;
  @Column(name = "SXJLX")
  private String sxjlx;
  @Column(name = "SXJGNLX")
  private String sxjgnlx;
  @Column(name = "BGSX")
  private String bgsx;
  @Column(name = "SXJBMGS")
  private String sxjbmgs;
  @Column(name = "AZDZ")
  private String azdz;
  @Column(name = "JD")
  private Double jd;
  @Column(name = "WD")
  private Double wd;
  @Column(name = "SXJWZLX")
  private String sxjwzlx;
  @Column(name = "JSFW")
  private String jsfw;
  @Column(name = "LWSX")
  private String lwsx;
  @Column(name = "SSXQGAJG")
  private String ssxqgajg;
  @Column(name = "AZSJ")
  private java.sql.Timestamp azsj;
  @Column(name = "GLDW")
  private String gldw;
  @Column(name = "GLDWLXFS")
  private String gldwlxfs;
  @Column(name = "LXBCTS")
  private Long lxbcts;
  @Column(name = "SBZT")
  private String sbzt;
  @Column(name = "SSBMHY")
  private String ssbmhy;


}
