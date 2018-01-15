package com.fable.mssg.catalog.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.equipment.EquipAttribute;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.catalog.repository.EquipAttributeRepository;
import com.fable.mssg.catalog.repository.EquipmentCatalogRepository;
import com.fable.mssg.catalog.repository.MediaSourceInfoRepository;
import com.fable.mssg.catalog.service.EquipmentCatalogService;
import com.fable.mssg.catalog.service.exception.EquipmentCatalogException;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.equipment.MediaInfoBean;
import com.fable.mssg.service.datasource.DataSourceService;
import com.fable.mssg.utils.HSSFUtil;
import com.fable.mssg.vo.equipment.VEquipment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author: yuhl
 * @Description:
 * @Date: Created on 13:39 2017/9/1 0001
 */
@Exporter(interfaces = {EquipmentCatalogService.class})
@Slf4j
public class EquipmentCatalogServiceImpl implements EquipmentCatalogService {

    @Autowired
    public EquipmentCatalogRepository catalogRepository;

    @Autowired
    public MediaSourceInfoRepository mediaInfoRepository;

    @Autowired
    public EquipAttributeRepository equipAttributeRepository;

    @Autowired
    public DataSourceService dataSourceService;

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EquipmentExcelService equipmentExcelService;

    /**
     * 保存设备目录信息
     *
     * @param bean
     * @return
     */
    @Override
    public EquipmentCatalogBean saveCatalogInfo(EquipmentCatalogBean bean) {
        return catalogRepository.save(bean);
    }

    /**
     * 查询所有的设备目录信息
     *
     * @return
     */
    @Override
    public List<EquipmentCatalogBean> findAll() {
        return catalogRepository.findAll();
    }

    /**
     * 查询所有媒体源信息
     *
     * @return
     */
    @Override
    public List<MediaInfoBean> findAllMediaInfo() {
        return mediaInfoRepository.findAll();
    }

    /**
     * 根据deviceCode查询设备目录信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    public EquipmentCatalogBean getCatalogInfoByDeviceCode(String deviceCode) {
        return catalogRepository.queryByDeviceCode(deviceCode);
    }

    /**
     * 根据id查询设备目录信息
     *
     * @param id
     * @return
     */
    @Override
    public EquipmentCatalogBean getCatalogInfoById(String id) {
        return catalogRepository.findOne(id);
    }

    /**
     * 根据deviceCode更新设备信息
     *
     * @param bean
     * @return
     */
    @Override
    public void updateCatalogInfoByDeviceCode(EquipmentCatalogBean bean) {
        catalogRepository.deleteCatalogByDeviceCode(bean.getDeviceId()); // 先删除
        catalogRepository.save(bean); // 再新增
    }

    /**
     * 根据deviveCode删除设备信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    public int deleteCatalogByDeviceCode(String deviceCode) {
        return catalogRepository.deleteCatalogByDeviceCode(deviceCode);
    }

    /**
     * 根据deviceId更新设备目录
     *
     * @param status
     * @param deviceId
     * @return
     */
    @Override
    public int updateCatalogStatus(String status, String deviceId) {
        return catalogRepository.updateCatalogStatus(status, deviceId);
    }

    @Override
    public List<EquipmentCatalogBean> findByFilter(String[] mediaIds, Integer[] eqLevels, Integer[] positions, String eqName) {

        if (mediaIds.length == 0 && eqLevels.length == 0 && positions.length == 0 && (eqName == null || eqName.equals(""))) {
            return catalogRepository.findAll();
        }


        String query = "SELECT mods.ID,mods.DS_NAME,mods.DEVICE_ID,\n" +
                "mods.DS_TYPE,mods.MANU_NAME,mods.MANU_NAME,mods.MODEL,mods.`OWNER`,mods.CIVIL_CODE,mods.BLOCK,mods.ADDRESS,mods.PARENTAL, " +
                "mods.PARENTID,mods.REGISTER_WAY,mods.SECRECY,mods.`STATUS`,mods.BUS_GROUP_ID,mods.LOGIN_PWD,mods.PARENT_ID,mods.MEDIA_DEVICE_ID, " +
                "mods.MEDIA_DEVICE_ID,IFNULL(mea.JKDWLX,0) as DS_LEVEL,mods.LNG,mods.LAT,IFNULL(mea.SXJWZLX,0) as LOCATION_TYPE,mods.IP_ADDRESS,mods.EQUIP_TYPE,mods.CREATE_USER, " +
                "mods.CREATE_TIME,mods.UPDATE_USER,mods.UPDATE_TIME " +
                "FROM mssg_original_ds mods " +
                "LEFT JOIN mssg_equip_attribute mea ON mods.DEVICE_ID = mea.SBBM  " +
                "WHERE mods.DS_TYPE = 5 ";
        if (mediaIds != null && mediaIds.length != 0) {
            query += "AND ";
            query += "mods.MEDIA_DEVICE_ID  IN( ";
            for (int i = 0; i < mediaIds.length; i++) {
                query += "'" + mediaIds[i] + "' ";
                if (i != mediaIds.length - 1) {
                    query += ",";
                }

            }
            query += ") ";
        }

        if (eqLevels.length != 0) {
            query += "AND mea.JKDWLX IN( ";
            for (int i = 0; i < eqLevels.length; i++) {
                query += eqLevels[i];
                if (i != eqLevels.length - 1) {
                    query += ",";
                }

            }
            query += ") ";
        }
        if (positions.length != 0) {
            query += "AND mea.SXJWZLX IN( ";
            for (int i = 0; i < positions.length; i++) {
                query += positions[i];
                if (i != positions.length - 1) {
                    query += ",";
                }
            }
            query += ") ";
        }
        if (eqName != null && !eqName.equals("")) {
            query += "AND mods.DS_NAME like '%" + eqName + "%'";
        }
        List<EquipmentCatalogBean> satisfiedEquipments = entityManager.createNativeQuery(query, EquipmentCatalogBean.class).getResultList();

        Set<EquipmentCatalogBean> allParents = new HashSet<>();

        for (EquipmentCatalogBean equipmentCatalogBean : satisfiedEquipments) {
            //调用mysql函数
            log.debug(equipmentCatalogBean.getDeviceId());
            List<EquipmentCatalogBean> parents = catalogRepository.findParent(equipmentCatalogBean.getDeviceId());
            allParents.addAll(parents);

        }
        satisfiedEquipments.addAll(allParents);

        return satisfiedEquipments;
    }

    @Transactional
    @Override
    public int[] importEquipInfo(InputStream inputStream) {
        int[] results = new int[2];
        int success = 0;
        int failed = 0;
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.error("导入异常", e);
            throw new EquipmentCatalogException(EquipmentCatalogException.EQUIPMENT_IMPORT_EXP);
        }
        HSSFSheet sheet = workbook.getSheet("equipment");
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
            EquipAttribute equipAttribute = new EquipAttribute();

            equipAttribute.setSbbm(row.getCell(0).getStringCellValue());//不可为空

            EquipAttribute equipAttributePersist = equipAttributeRepository.findOne(Example.of(equipAttribute));
            if (equipAttributePersist != null) {
                equipAttribute.setId(equipAttributePersist.getId());
            }
            //更新设备DataSource
            List<DataSource> dataSources = dataSourceService.findByDeviceId(equipAttribute.getSbbm());
            if (catalogRepository.queryByDeviceCode(equipAttribute.getSbbm()) != null) {
                equipAttribute.setSbmc(HSSFUtil.getString(row.getCell(1)));
                equipAttribute.setSbcs(HSSFUtil.getInteger(row.getCell(2)));
                equipAttribute.setXzqy(HSSFUtil.getString(row.getCell(3)));
                Integer jkdwlx = HSSFUtil.getInteger(row.getCell(4));
                equipAttribute.setJkdwlx(jkdwlx == 0 ? "9" : jkdwlx.toString());
                equipAttribute.setSbxh(HSSFUtil.getString(row.getCell(5)));
                equipAttribute.setDwsc(HSSFUtil.getString(row.getCell(6)));
                equipAttribute.setIpv4(HSSFUtil.getString(row.getCell(7)));
                equipAttribute.setIpv6(HSSFUtil.getString(row.getCell(8)));
                equipAttribute.setMacdz(HSSFUtil.getString(row.getCell(9)));
                equipAttribute.setSxjlx(HSSFUtil.getInteger(row.getCell(10)));
                equipAttribute.setSxjgnlx(HSSFUtil.getInteger(row.getCell(11)));
                equipAttribute.setBgsx(HSSFUtil.getInteger(row.getCell(12)));
                equipAttribute.setSxjbmgs(HSSFUtil.getInteger(row.getCell(13)));
                equipAttribute.setAzdz(HSSFUtil.getString(row.getCell(14)));
                equipAttribute.setJd(HSSFUtil.getDouble(row.getCell(15)));
                equipAttribute.setWd(HSSFUtil.getDouble(row.getCell(16)));
                Integer sxjwzlx = HSSFUtil.getInteger(row.getCell(17));
                equipAttribute.setSxjwzlx(sxjwzlx == 0 ? "99" : sxjwzlx.toString());
                equipAttribute.setJsfw(HSSFUtil.getInteger(row.getCell(18)));
                equipAttribute.setLwsx(HSSFUtil.getInteger(row.getCell(19)));
                equipAttribute.setSsxqgajg(HSSFUtil.getString(row.getCell(20)));
                equipAttribute.setAzsj(HSSFUtil.getDate(row.getCell(21)));
                equipAttribute.setGldw(HSSFUtil.getString(row.getCell(22)));
                equipAttribute.setGldwlxfs(HSSFUtil.getString(row.getCell(23)));
                equipAttribute.setLxbcts(HSSFUtil.getInteger(row.getCell(24)));
                equipAttribute.setSbzt(HSSFUtil.getInteger(row.getCell(25)));
                equipAttribute.setSsbmhy(HSSFUtil.getInteger(row.getCell(26)));
                equipAttributeRepository.save(equipAttribute);
                for (DataSource dataSource : dataSources) {
                    if (!"".equals(equipAttribute.getJkdwlx())) {
                        int ii = Integer.parseInt(equipAttribute.getJkdwlx());
                        dataSource.setDsLevel(ii);
                    } else {
                        dataSource.setDsLevel(0);
                    }
                    if (!"".equals(equipAttribute.getJkdwlx())) {
                        int ii = Integer.parseInt(equipAttribute.getJkdwlx());
                        dataSource.setLocationType(ii);

                    } else {
                        dataSource.setLocationType(0);
                    }

                }
                dataSourceService.save(dataSources);
                success++;
                log.info("成功导入一条设备信息");
            } else {
                log.info("在网关上没发现该设备编码的设备,跳过该条");
                failed++;
            }

        }

        results[0] = success;
        results[1] = failed;
        return results;
    }


    @Override
    public HSSFWorkbook exportEquipInfo() {
        List<EquipmentCatalogBean> equipmentCatalogBeans = catalogRepository.findAll();
        List<EquipAttribute> equipAttributes = new ArrayList<>();
        for (EquipmentCatalogBean equipmentCatalogBean : equipmentCatalogBeans) {
            if (equipmentCatalogBean.getCatalogType() == 5) {
                EquipAttribute equipAttribute = equipAttributeRepository.findBySbbm(equipmentCatalogBean.getDeviceId());
                if (equipAttribute == null) {
                    equipAttribute = new EquipAttribute();
                    equipAttribute.setSbbm(equipmentCatalogBean.getDeviceId());
                    equipAttribute.setSbmc(equipmentCatalogBean.getCatalogName());
                }
                equipAttribute.setIpv4(equipmentCatalogBean.getIpAddress());
                equipAttribute.setJd(equipmentCatalogBean.getLat());
                equipAttribute.setWd(equipmentCatalogBean.getLng());
                equipAttributes.add(equipAttribute);
            }
        }

        HSSFWorkbook workbook = equipmentExcelService.export(equipAttributes);

        log.info("导出{}条设备属性", equipAttributes.size());
        return workbook;
    }

    @Override
    public Map findAllPageByCondition(int size, int page, String dsName,
                                      String jkdwlx, String locationType, String mediaDeviceId) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbcount = new StringBuffer();
        sb.append(" select md.ID,coalesce(ma.SBMC,md.DS_NAME) as  SBMC ,md.IP_ADDRESS,ma.JKDWLX,ma.SXJLX,ma.SXJWZLX,ma.SBCS from mssg_original_ds md LEFT  JOIN mssg_equip_attribute ma\n" +
                "               on md.DEVICE_ID=ma.SBBM where 1=1 and md.DS_TYPE=5 ");
        sbcount.append("  select count(1) from (select md.ID,ma.SBMC,md.IP_ADDRESS,ma.JKDWLX,ma.SXJLX,ma.SXJWZLX,ma.SBCS from mssg_original_ds md LEFT  JOIN mssg_equip_attribute ma \n" +
                "    on md.DEVICE_ID=ma.SBBM where 1=1 and md.DS_TYPE=5 ");

        if (null != dsName && !"".equals(dsName)) {
            sb.append(" and SBMC like '%" + dsName + "%' ");
            sbcount.append(" and ma.SBMC like '%" + dsName + "%' ");
        }
        if (null != jkdwlx && !"".equals(jkdwlx)) {
            sb.append(" and ma.JKDWLX =" + jkdwlx);
            sbcount.append(" and ma.JKDWLX =" + jkdwlx);
        }
        if (null != mediaDeviceId && !"".equals(mediaDeviceId)) {
            sb.append(" and md.MEDIA_DEVICE_ID = '" + mediaDeviceId + "'");
            sbcount.append(" and md.MEDIA_DEVICE_ID = '" + mediaDeviceId + "'");
        }
        if (null != locationType && !"".equals(locationType)) {
            sb.append(" and (");
            sbcount.append(" and (");
            String[] locationTypes = locationType.split("/");
            for (int i = 0; i < locationTypes.length; i++) {
                if (i == locationTypes.length - 1) {
                    sb.append(" ma.SXJWZLX = '" + locationTypes[i] + "'  ");
                    sbcount.append(" ma.SXJWZLX = '" + locationTypes[i] + "'  ");
                } else {
                    sb.append(" ma.SXJWZLX = '" + locationTypes[i] + "' or ");
                    sbcount.append(" ma.SXJWZLX = '" + locationTypes[i] + "' or ");
                }

            }
            sb.append(" ) ");
            sbcount.append(" ) ");
        }
        sb.append(" LIMIT " + size * (page - 1) + "," + size + "");
        sbcount.append(" ) a ");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sb.toString());
        Query querycount = em.createNativeQuery(sbcount.toString());
        List list = query.getResultList();
        int count = Integer.valueOf(querycount.getSingleResult().toString());
        List<VEquipment> vslist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Object[] object = (Object[]) list.get(i);
            VEquipment vs = new VEquipment();
            vs.setId(object[0] == null ? "" : object[0].toString());//id
            vs.setEquipmentName(object[1] == null ? "" : object[1].toString());
            vs.setIpAddress(object[2] == null ? "" : object[2].toString());
            vs.setJkdwlx(object[3] == null ? "" : getjkdwlx(object[3].toString()));
            vs.setEquipmentType(object[4] == null ? "" : getEquipType(object[4].toString()));
            vs.setSxjkwzlx(object[5] == null ? "" : getsxjkwzlx(object[5].toString()));
            vs.setManuName(object[6] == null ? "" : object[6].toString());
            vslist.add(vs);
        }
        em.close();
        Map map = new HashMap();
        map.put("count", count);
        map.put("vslist", vslist);
        return map;
    }

    @Override
    public void updateEquipAttribute(EquipAttribute equipAttribute) {
        equipAttributeRepository.save(equipAttribute);
    }

    @Override
    public EquipmentCatalogBean findOne(String oid) {
        return catalogRepository.findOne(oid);
    }

    @Override
    public EquipAttribute findOneEquipBydsCode(String deviceId) {
        return equipAttributeRepository.findBySbbm(deviceId);
    }

    @Override
    public EquipmentCatalogBean findByDeviceId(String deviceId) {
        return catalogRepository.queryByDeviceCode(deviceId);
    }


    public String getjkdwlx(String jkdwlx) {
        switch (jkdwlx) {
            case "1":
                return "一类视频监控点";
            case "2":
                return "二类视频监控点";
            case "3":
                return "三类视频监控点";
            case "4":
                return "公安内部视频监控点";
            default:
                return "其他点位";
        }

    }

    public String getsxjkwzlx(String locationtype) {
        String[] str = locationtype.split("/");
        StringBuffer sb = new StringBuffer();
        for (String s : str) {
            switch (s) {
                case "1":
                    sb.append("省际检查站");
                    break;
                case "2":
                    sb.append("党政机关");
                    break;
                case "3":
                    sb.append("车站码头");
                    break;
                case "4":
                    sb.append("中心广场");
                    break;
                case "5":
                    sb.append("体育场馆");
                    break;
                case "6":
                    sb.append("商业中心");
                    break;
                case "7":
                    sb.append("宗教场所");
                    break;
                case "8":
                    sb.append("校园周边");
                    break;
                case "9":
                    sb.append("治安复杂区域");
                    break;
                case "10":
                    sb.append("交通干线");
                    break;
                case "11":
                    sb.append("医院周边");
                    break;
                case "12":
                    sb.append("金融机构周边");
                    break;
                case "13":
                    sb.append("危险物品场");
                    break;
                case "14":
                    sb.append("博物馆展览馆");
                    break;
                case "15":
                    sb.append("重点水域");
                    break;
                default:
                    sb.append("其他点位");
                    break;
            }
        }
        return sb.toString();
    }

    String getEquipType(String equipType) {
        switch (equipType) {
            case "1":
                return "球机";
            case "2":
                return "半球";
            case "3":
                return "固定枪机";
            case "4":
                return "遥控枪";
            default:
                return "其他";
        }
    }


}
