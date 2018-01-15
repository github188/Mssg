package com.fable.mssg.resource.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.MediaSubscribe;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.repository.MediaSubscribeRepository;
import com.fable.mssg.resource.repository.ResSubscribeRepository;
import com.fable.mssg.resource.repository.ResourceRepository;
import com.fable.mssg.resource.repository.SubscribePrvRepository;
import com.fable.mssg.exception.ResSubscribeException;
import com.fable.mssg.resource.service.exception.ResourceException;
import com.fable.mssg.bean.SubscribeCondition;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.resource.ResourceService;
import com.fable.mssg.service.user.ApprListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
@Exporter(interfaces = ResSubscribeService.class)
@Service
@Slf4j
public class ResSubscribeServiceImpl implements ResSubscribeService {
    @Autowired
    ResSubscribeRepository resSubscribeRepository;

    @Autowired
    SubscribePrvRepository subscribePrvRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    MediaSubscribeRepository mediaSubscribeRepository;

    @Autowired
    ResourceService resourceService;

    @Autowired
    private ApprListService apprListService;

    @Value("${subscribe.file.path}")
    String subscribeFilePath;

    @Transactional
    @Override
    public ResSubscribe save(ResSubscribe resSubscribe, String resId, Company company) {
        Resource resource = resourceRepository.findOne(resId);
        if (null == resource) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        resSubscribe.setApprovalUser(resource.getCreateUser());
        resSubscribe.setResId(new Resource(resId));
        subscribePrvRepository.save(resSubscribe.getSubscribePrvList());

        //判断该单位是否订阅过该资源 若订阅过 判断状态是否是去订阅 否则不让订阅
        ResSubscribe subscribePersist = resSubscribeRepository.findByComIdAndResId(company, new Resource(resId));
        if (subscribePersist != null) {
            if (subscribePersist.getState() == 4 || subscribePersist.getState() == 3) {//若是去订阅了或者已拒绝 则直接该状态
                resSubscribe.setId(subscribePersist.getId());
            } else {
                throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_ALREADY_EXIST);//资源已经订阅了
            }
        }

        resSubscribe = resSubscribeRepository.save(resSubscribe);
        if (resourceService.findOneRes(resId).getResLevel() == 0) { //完全共享的情况不需要审核
            resSubscribe.setState(2);//直接通过审核
            resSubscribe.setUpdateUser(resource.getCreateUser());
            resSubscribe.setUpdateTime(resource.getCreateTime());

        } else {
            resSubscribe.setState(1);
            ApprList apprList = new ApprList();
            apprList.setApprId(resSubscribe.getId());
            apprList.setApprType(2L);
            apprList.setApprStatus(1);
            apprList.setCreateTime(new Timestamp(System.currentTimeMillis()));
            apprList.setCreateUser(resSubscribe.getCreateUser().getId());
            apprListService.save(apprList);
        }
        return resSubscribeRepository.save(resSubscribe);
    }

    @Override
    public void delete(String id) {
        resSubscribeRepository.delete(id);
    }

    /**
     * 订阅审批
     *
     * @param
     * @param sysUser
     */
    @Transactional
    @Override
    public void approval(ResSubscribe resSubscribe, SysUser sysUser) {
        //保存新的权限
        subscribePrvRepository.save(resSubscribe.getSubscribePrvList());
        //记录审核通过时间
        resSubscribe.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        resSubscribe.setUpdateUser(sysUser);
        resSubscribeRepository.save(resSubscribe);
    }


    @Override
    public ResSubscribe findById(String id) {
        ResSubscribe resSubscribe = resSubscribeRepository.findOne(id);
        if (resSubscribe == null) {
            throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_NOT_FOUND);
        }
        return resSubscribe;
    }

    /**
     * 取消订阅
     *
     * @param id
     */
    @Transactional
    @Override
    public boolean cancel(String id) {
        ResSubscribe resSubscribe = findById(id);
        if (resSubscribe == null) {
            throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_NOT_FOUND);
        }
        if (resSubscribe.getState().equals(3) || resSubscribe.getState().equals(2) || resSubscribe.getState().equals(5)) {//已审核状态或已拒绝
            resSubscribe.setState(4);
            mediaSubscribeRepository.delete(mediaSubscribeRepository.findBySscId(id));
            subscribePrvRepository.delete(resSubscribe.getSubscribePrvList());
            resSubscribe.setSubscribePrvList(null);
            resSubscribeRepository.save(resSubscribe);
            return true;
        } else {
            throw new ResSubscribeException(ResSubscribeException.CAN_NOT_CANCEL);
        }
    }

    @Override
    public List<ResSubscribe> findByCondition(SubscribeCondition condition) {


        return resSubscribeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = null;
            //predicate = criteriaBuilder.equal(root.get("approvalUser").get("id"), condition.getApprovalId());
            if (condition.getCatalogId() != null && !condition.getCatalogId().equals("")) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("resId").get("catalogId").get("id"),
                        condition.getCatalogId()));

            }
            if (condition.getResName() != null && !condition.getResName().equals("")) {
                condition.setResName("%" + condition.getResName() + "%");
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("resId").get("resName"), condition.getResName()));
            }
            if (condition.getState() != null && !condition.getState().equals("")) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("state"), condition.getState()));
            }
            if (condition.getTime() != null && !condition.getTime().equals("")) {
                Calendar calendar = Calendar.getInstance();
                String[] times = condition.getTime().split(":");
                switch (times[1]) {
                    case "week":
                        calendar.add(Calendar.WEEK_OF_YEAR, -Integer.parseInt(times[0]));
                        break;
                    case "month":
                        calendar.add(Calendar.MONTH, -Integer.parseInt(times[0]));
                        break;
                    case "year":
                        calendar.add(Calendar.YEAR, -Integer.parseInt(times[0]));
                        break;
                    case "day":
                        calendar.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(times[0]));
                        break;
                    default:
                }

                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("time"), new Timestamp(calendar.getTimeInMillis())));

            }
            return predicate;
        });

    }

    /**
     * （暂时没用）
     *
     * @param sysUser
     * @return
     */
    @Override
    public List<SubscribePrv> findByUser(SysUser sysUser) {
        List<ResSubscribe> resSubscribes = resSubscribeRepository.findByStateAndApplyUser(2, sysUser);
        List<SubscribePrv> subscribePrvs = new ArrayList<>();
        for (ResSubscribe resSubscribe : resSubscribes) {
            subscribePrvs.addAll(resSubscribe.getSubscribePrvList());
        }

        return subscribePrvs;
    }

    /**
     * 根据用户所在单位查询订阅权限
     *
     * @param company
     * @param dsLevel
     * @param locationType
     * @param dsName       @return
     */
    @Override
    public List<ResSubscribe> findByCompany(Company company, Integer dsLevel, Integer locationType, String dsName) {

        List<ResSubscribe> resSubscribes = resSubscribeRepository.findSubscribe(company.getId());
        //TODO 过滤


        return resSubscribes;
    }

    @Override
    public List<ResSubscribe> findByApplyUser(SysUser sysUser) {

        return resSubscribeRepository.findByApplyUser(sysUser);
    }

    @Transactional
    @Override
    public void shared(String[] mediaIds, String subscribeId, String userId) {
        List<MediaSubscribe> mediaSubscribes = new ArrayList<>(mediaIds.length);
        for (String mediaId : mediaIds) {
            MediaSubscribe mediaSubscribe = new MediaSubscribe();
            mediaSubscribe.setSscId(subscribeId);
            mediaSubscribe.setMediaId(mediaId);
            mediaSubscribe.setShareId(userId);
            if (mediaSubscribeRepository.findBySscIdAndMediaId(subscribeId, mediaId) != null) {
                throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_ALREADY_SHARED);
            }
            mediaSubscribes.add(mediaSubscribe);
        }
        ResSubscribe resSubscribe = resSubscribeRepository.findOne(subscribeId);
        if (resSubscribe == null) {
            throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_NOT_FOUND);
        }
        resSubscribe.setState(5);//已共享
        resSubscribeRepository.save(resSubscribe);
        mediaSubscribeRepository.save(mediaSubscribes);
    }

    @Override
    public List<DataSource> findDataSourceByMediaId(String mediaId) {
        List<MediaSubscribe> mediaSubscribes = mediaSubscribeRepository.findByMediaCode(mediaId);

        List<String> subscribeIds = new ArrayList<>();
        for (MediaSubscribe mediaSubscribe : mediaSubscribes) {
            subscribeIds.add(mediaSubscribe.getSscId());
        }
        List<ResSubscribe> resSubscribes = resSubscribeRepository.findAll(subscribeIds);
        List<DataSource> dataSources = new ArrayList<>();
        for (ResSubscribe resSubscribe : resSubscribes) {
            for (SubscribePrv subscribePrv : resSubscribe.getSubscribePrvList()) {
                dataSources.add(subscribePrv.getDsId());
            }

        }

        return dataSources;
    }

    @Override
    public long count() {
        return resSubscribeRepository.count((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.or(criteriaBuilder.equal(root.get("state"), 2)
                    , criteriaBuilder.equal(root.get("state"), 5));//状态是已审核或者已共享
        });
    }

    @Override
    public long count(ResSubscribe example) {
        return resSubscribeRepository.count(Example.of(example));
    }

    @Override
    public long countByComId(String id) {
        ResSubscribe resSubscribe = new ResSubscribe();
        resSubscribe.setComId(new Company(id));
        return resSubscribeRepository.count(Example.of(resSubscribe));
    }

    @Override
    public void deleteByResourceId(String rid) {
        List<ResSubscribe> resSubscribes = resSubscribeRepository.findByResId(new Resource(rid));
        for (ResSubscribe resSubscribe : resSubscribes) {
            subscribePrvRepository.delete(subscribePrvRepository.findByResSubscribeId(resSubscribe.getId()));
        }
        resSubscribeRepository.delete(resSubscribes);
    }

    //slave 端上传文件
    @Override
    public String uploadFile(String originFileName, InputStream inputStream) throws IOException {
        String suffix = originFileName.substring(originFileName.lastIndexOf("."));
        File filePath = new File(subscribeFilePath);

        if (!filePath.exists() || filePath.exists() && filePath.isFile()) {
            filePath.mkdirs();
            log.info("创建订阅文件路径:" + subscribeFilePath);
        }
        String fileName = System.currentTimeMillis() + suffix;
        File file = new File(subscribeFilePath + fileName);
        file.createNewFile();
        log.info("创建订阅申请材料文件" + file.getAbsolutePath());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[1024 * 1024];
        int length = -1;
        while ((length = bufferedInputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, length);
        }
        return fileName;
    }

    @Override
    public void deletePrv(Set<SubscribePrv> subscribePrvList) {
        subscribePrvRepository.delete(subscribePrvList);
    }


}
