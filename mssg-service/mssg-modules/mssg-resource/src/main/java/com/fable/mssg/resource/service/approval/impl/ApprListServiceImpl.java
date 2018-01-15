package com.fable.mssg.resource.service.approval.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.fable.mssg.resource.converter.ConverterConstants;
import com.fable.mssg.resource.repository.approval.ApprListRepository;

import com.fable.mssg.resource.repository.dict.DictItemRepository;
import com.fable.mssg.service.user.ApprListService;
import com.fable.mssg.vo.VRegisterApprList;
import com.fable.mssg.resource.vo.vApprlist.VSubApprList;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Exporter(interfaces = ApprListService.class)
@Service
public class ApprListServiceImpl  implements ApprListService {

    @Autowired
    ApprListRepository apprListRepository;
    @Autowired
    EntityManagerFactory emf;
    @Autowired
    DictItemRepository dictItemRepository;

    //分页查询资源订阅
    @Override
    public Map findAllPageByCondition(int apprStatus,int size,int page,String resName,String catalogid,Timestamp searchTime) {
        StringBuffer sb=new StringBuffer();
        StringBuffer sbcount=new StringBuffer();
        sb.append("select ml.id as mid ,mr.id as rid ,mr.res_name,mr.res_type,mr.icon_root,\n" +
                "mr.res_abstract,mr.hy_category,mr.main_category,mr.res_status,mr.update_time ,ms.apply_doc_path ,ml.APPR_STATUS ,msu.USER_NAME,mc.NAME " +
                "from mssg_appr_list  ml \n" +
                "INNER JOIN  mssg_res_subscribe ms on ml.appr_id=ms.id\n" +
                "INNER JOIN mssg_resource mr on mr.id=ms.res_id " +
                " LEFT JOIN mssg_sys_user msu ON ms.APPLY_USER = msu.ID "+
                " LEFT JOIN mssg_company mc ON mc.ID = ms.COM_ID "+
                "where ml.appr_type=2 ");
        sbcount.append("select count(1) from (   select ml.id as mid ,mr.id as rid ,mr.res_name,mr.res_type,mr.icon_root,\n" +
                "  mr.res_abstract,mr.hy_category,mr.main_category,mr.res_status,mr.update_time,ms.apply_doc_path  ,ml.APPR_STATUS " +
                "  from mssg_appr_list  ml \n" +
                " INNER JOIN  mssg_res_subscribe ms on ml.appr_id=ms.id \n" +
                " INNER JOIN mssg_resource mr on mr.id=ms.res_id" +
                " where ml.appr_type=2  ");

        if(apprStatus!=0){
            sb.append(" and ml.appr_status="+apprStatus);
            sbcount.append(" and ml.appr_status="+apprStatus );
        }
        if(resName!=null&&!"".equals(resName)){
            sb.append(" and mr.res_name like '%"+resName+"%' ");
            sbcount.append(" and mr.res_name like '%"+resName+"%' ");
        }
        if(catalogid!=null&&!"".equals(catalogid)){
            sb.append(" and mr.clog_id='"+catalogid+"' ");
            sbcount.append(" and mr.clog_id='"+catalogid+"' ");
        }
        if(searchTime!=null) {
            sb.append(" and mr.create_time <" + searchTime);
            sbcount.append("nd mr.create_time <" + searchTime);
        }
        sb.append(" LIMIT "+size*(page-1)+","+size+"");
        sbcount.append(" ) a ");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sb.toString());
        Query querycount = em.createNativeQuery(sbcount.toString());
        List list = query.getResultList();
        int count=Integer.valueOf( querycount.getSingleResult().toString());
        List<VSubApprList> vslist=new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0;i<list.size();i++){
            Object[] object =(Object[]) list.get(i);
            VSubApprList vs=new VSubApprList();
            vs.setId(object[0]==null?"":object[0].toString());//id
            vs.setResid(object[1]==null?"":object[1].toString());//资源id
            vs.setResName(object[2]==null?"":object[2].toString());
            vs.setResType(object[3]==null?"":ConverterConstants.getType(Integer.valueOf(object[3].toString())));
            vs.setResIcon(object[4]==null?"":object[4].toString());
            vs.setResAbstract(object[5]==null?"":object[5].toString());
            vs.setHyCategory(object[6]==null?"":dictItemRepository.findByDictItemCode(Long.valueOf(object[6].toString())).getDictItemName());
            vs.setMainCategory(object[7]==null?"":dictItemRepository.findByDictItemCode(Long.valueOf(object[7].toString())).getDictItemName());
            vs.setResStatus(object[8]==null?"":ConverterConstants.getResourceStatus(Integer.valueOf(object[8].toString())));
            vs.setPubTime(object[9]==null?"":sdf.format((Date)object[9]));
            vs.setApprmaterial(object[10]==null?"":object[10].toString());//申请材料
            vs.setApprStatus(object[11]==null?"":object[11].toString());//审批状态
            vs.setApplyMan(object[12]==null?"":object[12].toString());
            vs.setApplyCompany(object[13]==null?"":object[13].toString());
            vslist.add(vs);
        }
        em.close();
        Map map=new HashMap();
        map.put("count",count);
        map.put("vslist",vslist);
        return map;
    }



    @Override
    public ApprList findOneApprList(String id) {
        return apprListRepository.findOne(id);
    }

    @Override
    public boolean update(ApprList apprList) {
        boolean flage=false;
        try {
            apprListRepository.save(apprList);
            flage=  true;
        } catch (Exception e) {
            e.printStackTrace();
            flage=  false;
        }
        return  flage;
    }



    //分页查询注册审批
    @Override
    public Map findAllResigterPage(int size, int page) {
        StringBuffer sb=new StringBuffer();
        sb.append(" select ml.id , ml.appr_status,mu.login_name,mu.user_name,mc.`name` from  mssg_appr_list  ml \n" +
                "INNER JOIN mssg_sys_user mu on mu.id=ml.appr_id\n" +
                "INNER JOIN mssg_company mc on mc.id=mu.com_id\n" +
                "where ml.appr_type=3 ");
        sb.append(" LIMIT "+size*(page-1)+","+size+"");
        StringBuffer sbcount=new StringBuffer();
        sbcount.append("select count(1) from (\n" +
                "select ml.id , ml.appr_status,mu.login_name,mu.user_name,mc.`name` from  mssg_appr_list  ml \n" +
                "INNER JOIN mssg_sys_user mu on mu.id=ml.appr_id\n" +
                "INNER JOIN mssg_company mc on mc.id=mu.com_id\n" +
                "where ml.appr_type=3  ) a");
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sb.toString());
        Query querycount = em.createNativeQuery(sbcount.toString());
        int count=Integer.valueOf( querycount.getSingleResult().toString());//获得总数
        List list = query.getResultList();
        List<VRegisterApprList> vslist=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            Object[] object =(Object[]) list.get(i);
            VRegisterApprList vr=new VRegisterApprList();
            vr.setId(object[0]==null?"":object[0].toString());//id
            vr.setApprStatus(object[1]==null?"":object[1].toString());//审批状态
            vr.setLoginName(object[2]==null?"":object[2].toString());//登录名称
            vr.setUserName(object[3]==null?"":object[3].toString());//用户名称
            vr.setCompanyName(object[4]==null?"":object[4].toString());//公司名称
            vslist.add(vr);
        }
        em.close();
        Map map=new HashMap();
        map.put("count",count);
        map.put("vslist",vslist);
        return map;
    }

    //查询单个注册审批详细信息
    @Override
    public VRegisterApprList findOneVRegisterApprList(String id) {
        StringBuffer sb=new StringBuffer();
        sb.append("select ml.id ,mu.login_name,mu.user_name,mu.position as userposition ,mu.id_card,mu.`password`\n" +
                ",mu.telphone as usertelphone ,mu.cell_phone_number,mc.`name`,mc.address,mc.contacts,mc.position\n" +
                ",mc.telphone ,ml.APPR_STATUS,mc.COM_LEVEL from  mssg_appr_list  ml \n" +
                "INNER JOIN mssg_sys_user mu on mu.id=ml.appr_id\n" +
                "INNER JOIN mssg_company mc on mc.id=mu.com_id\n" +
                "where ml.appr_type=3   and ml.id=" +id);
        EntityManager em = emf.createEntityManager();
        Query query = em.createNativeQuery(sb.toString());
        List list = query.getResultList();
        VRegisterApprList vr=new VRegisterApprList();
        for(int i=0;i<list.size();i++){
            Object[] object =(Object[]) list.get(i);
            vr.setId(object[0]==null?"":object[0].toString());//id
            vr.setLoginName(object[1]==null?"":object[1].toString());//登录名称
            vr.setUserName(object[2]==null?"":object[2].toString());//用户名称
            vr.setPosition(object[3]==null?"":object[3].toString());//用户职位
            vr.setCareID(object[4]==null?"":object[4].toString());//身份证号
            vr.setPwd(object[5]==null?"":object[5].toString());//密码
            vr.setTelphone(object[6]==null?"":object[6].toString());//用户联系电话
            vr.setCellphonenumber(object[7]==null?"":object[7].toString());//用户手机
            vr.setCompanyName(object[8]==null?"":object[8].toString());//公司名称
            vr.setCompanyAddress(object[9]==null?"":object[9].toString());//公司地址
            vr.setLinkMan(object[10]==null?"":object[10].toString());//公司联系人
            vr.setLinkManposition(object[11]==null?"":object[11].toString());//公司联系人职位
            vr.setCompanytelphone(object[12]==null?"":object[12].toString());//公司联系电话
            vr.setApprStatus(object[13]==null?"":object[13].toString());//公司联系电话
            vr.setComLevel(object[14]==null?"":object[14].toString());
        }
        em.close();
        return vr;
    }

    @Override
    public void save(ApprList apprList) {
        apprListRepository.save(apprList);
    }
}
