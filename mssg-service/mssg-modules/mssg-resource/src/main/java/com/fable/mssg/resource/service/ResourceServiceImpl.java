package com.fable.mssg.resource.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.datasource.repository.DataSourceRepository;
import com.fable.mssg.domain.company.ComEquipLevel;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.resmanager.ResourceConfig;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.repository.ResourceConfigRepository;
import com.fable.mssg.resource.repository.ResourceRepository;
import com.fable.mssg.resource.service.exception.ResourceException;
import com.fable.mssg.resource.vo.ResourceStatus;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.datasource.DataSourceService;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.resource.ResourceService;
import com.fable.mssg.service.slave.FileLoadService;
import com.fable.mssg.utils.DataSourceUtil;
import com.fable.mssg.vo.subscribe.VResAndSubscribeStatus;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * description
 *
 * @author xiejk 2017/9/30
 */
@Exporter(interfaces = FileLoadService.class)
@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService, FileLoadService {


    //资源dao层操作对象
    @Autowired
    private ResourceRepository resourceRepository;
    //数据源dao层操作对象
    @Autowired
    private DataSourceService dataSourceService;
    //发布方位dao层操作对象
    @Autowired
    private PublishRegionService publishRegionService;


    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ResourceConfigRepository resourceConfigRepository;

    @Autowired
    private ResSubscribeService resSubscribeService;

    @Autowired
    private DictItemService dictItemService;

    @Value("${resource.icon.path}")
    private String resImagePath;

    /**
     * 根据查询条件,查询方式查询资源
     *
     * @param resStatus  查询的方式  0.表示查询全部  其他.表示查询对应情况
     * @param resName    资源名称
     * @param catalogId  目录id
     * @param searchTime 查询时间
     * @return List<Resource>
     */
    @Override
    public Page<Resource> findAllResourceByCondition(String resStatus, String resName, String catalogId, String searchTime, Integer page, Integer size) {

        return resourceRepository.findAll(new Specification<Resource>() {
            @Override
            public Predicate toPredicate(Root<Resource> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Predicate predicate;

                if (resStatus != null && !"".equals(resStatus)) {
                    predicate = cb.equal(root.get("resStatus"), resStatus);
                } else {
                    predicate = cb.and(cb.notEqual(root.get("resStatus"), "1"), cb.notEqual(root.get("resStatus"), "2"));//不查询未提交的
                }
                if (resName != null && !"".equals(resName)) {
                    predicate = cb.and(predicate, cb.like(root.get("resName"), "%" + resName + "%"));
                }
                if (catalogId != null && !"".equals(catalogId)) {
                    predicate = cb.and(predicate, cb.equal(root.get("catalogId").get("id"), catalogId));

                }
                if (searchTime != null && !"".equals(searchTime)) {
                    Calendar calendar = Calendar.getInstance();
                    switch (searchTime) {

                        case "0"://一周之内
                            calendar.add(Calendar.WEEK_OF_YEAR, -1);
                            break;
                        case "1"://一个月
                            calendar.add(Calendar.MONTH, -1);
                            break;

                        case "2"://一年
                            calendar.add(Calendar.YEAR, -1);
                            break;
                        case "3"://三年
                            calendar.add(Calendar.YEAR, -3);
                            break;
                        default:
                    }
                    predicate = cb.and(predicate, cb.greaterThan(root.get("createTime"), new Timestamp(calendar.getTimeInMillis())));
                }

                return predicate;
            }
        }, new PageRequest(page - 1, size, Sort.Direction.DESC, "createTime"));
    }

    /**
     * 分页查询对应目录下的资源信息
     *
     * @param size 每页显示页数
     * @param page 当前页数
     * @return Page<Resource>
     */
    @Override
    public Page<Resource> findByCatalogId(String cid, int size, int page) {
        return resourceRepository.findAll(
                //匿名类使用lambda表达式标识
                (root, query, cb) -> {
                    //加上对应catalogId的条件
                    return cb.equal(root.get("catalogId").get("id"), cid);
                }
                , new PageRequest(page - 1, size, new Sort(Sort.Direction.DESC, "createTime")));
    }

    /**
     * 业务保存资源
     */
    @Override
    public void save(Resource resource) {
        if (resourceRepository.findByResCode(resource.getResCode()) != null) {
            throw new ResourceException(ResourceException.RESOURCE_CODE_DUPLICATION);
        }
        if (resourceRepository.findByCatalogIdAndResName(resource.getCatalogId(), resource.getResName()) != null) {
            throw new ResourceException(ResourceException.RESOURCE_NAME_DUPLICATION);
        }

        resourceRepository.save(resource);

    }

    /**
     * 根据id 查询一个的resource
     *
     * @param rid 资源id
     * @return 修改对象
     */
    public Resource findOneRes(String rid) {
        return resourceRepository.findOne(rid);
    }

    /**
     * 修改待提交的资源
     *
     * @param res 资源对象
     */
    @Override
    public void updateResBeforePending(Resource res) {
        if (ResourceStatus.PUBLISHED == res.getResStatus() || ResourceStatus.TO_PUBLISH == res.getResStatus() || ResourceStatus.REVOKED == res.getResStatus()) {
            throw new ResourceException(ResourceException.CAN_NOT_MODIFY);
        }
        //更新资源信息
        resourceRepository.save(res);
    }

    /**
     * 删除对应的资源   删除时要判断资源的状态   只有在待提交和已撤销的状态才能删除
     *
     * @param rid 资源id
     */
    @Transactional
    @Override
    public void delRes(String rid) {
        Resource res = resourceRepository.findOne(rid);
        if (res == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        //要保证资源的状态是待提交或者已经撤销状态   是否还有其他的级联操作  删除对应的datasource
        if (res.getResStatus() == ResourceStatus.PUBLISHED) {
            throw new ResourceException(ResourceException.CAN_NOT_DELETE);
        }
        resourceRepository.delete(rid);
    }

    /**
     * 提交资源
     *
     * @param rid 资源id
     */
    @Override
    public void pendRes(String rid) {
        Resource res = resourceRepository.findOne(rid);
        if (res == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        //判断是否为提交状态  才能更新
        if (res.getResStatus() == ResourceStatus.TO_SUBMIT) {
            //不需要审核
            res.setResStatus(ResourceStatus.TO_PUBLISH);
            //创建修改时间
            Timestamp updatetime = new Timestamp(System.currentTimeMillis());
            //更新修改时间
            res.setUpdateTime(updatetime);
            //更新修改人
            //res.setUpdateUser();
            //更新
            resourceRepository.saveAndFlush(res);
        }
    }


    /**
     * 审核资源(Unused)
     *
     * @param rid 资源id
     * @return 是否审核成功
     */
    @Override
    @Transactional
    public void approvalRes(String rid, int resStatus) {
        Resource res = resourceRepository.findOne(rid);
        if (res == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        //判断为待审核状态
        if (res.getResStatus() == ResourceStatus.CHECK_PENDING) {
            //设置资源状态
            res.setResStatus(resStatus);
            //设置审核意见
            Timestamp updatetime = new Timestamp(System.currentTimeMillis());
            res.setUpdateTime(updatetime);
            //设置更新人
            //res.setUpdateUser();
            resourceRepository.saveAndFlush(res);
        } else {
            throw new ResourceException("");
        }

    }

    /**
     * 撤销资源
     *
     * @param rid 资源id
     * @return 是否撤销成功
     */
    @Transactional
    @Override
    public void revokeRes(String rid) {
        Resource resource = resourceRepository.findOne(rid);

        if (resource == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        //更新为撤销状态
        resource.setResStatus(ResourceStatus.REVOKED);
        Timestamp updatetime = new Timestamp(System.currentTimeMillis());
        //设置更新时间
        resource.setUpdateTime(updatetime);
        resSubscribeService.deleteByResourceId(rid);
        //先删除对应的数据源信息
        dataSourceRepository.delete(dataSourceRepository.findByRsId(rid));
        resourceConfigRepository.delete(resource.getResourceConfig());

        //设置更人
        //res.setUpdateUser();
        resourceRepository.saveAndFlush(resource);
    }

    /**
     * 发布资源  单击确定时保存数据 根据资源id进行发布
     *
     * @param rid     资源id
     * @param sysUser
     * @return 返回是否发布成功
     */
    @Transactional
    @Override
    public void publishRes(String rid, List<DataSource> dataSources, ResourceConfig config, SysUser sysUser) {

        Resource resource = resourceRepository.findOne(rid);
        if (null == resource) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        if (!resource.getResStatus().equals(ResourceStatus.TO_PUBLISH) && !resource.getResStatus().equals(ResourceStatus.REVOKED)) {
            throw new ResourceException(ResourceException.CAN_NOT_PUBLISH);
        }
        //生成20数字不重复主键

        for (DataSource dataSource : dataSources) {
            if (dataSource.getDsType().equals(4)) {//虚拟目录
                dataSource.setId(DataSourceUtil.getPrefix() + "200" + DataSourceUtil.getSuffix());
            } else if (dataSource.getDsCode().length() <= 10) {//位数小于等于10
                dataSource.setId(DataSourceUtil.getPrefix() + "001" + DataSourceUtil.getSuffix());
            } else {//第11位到13位
                dataSource.setId(DataSourceUtil.getPrefix() + dataSource.getDsCode().substring(10, 13) + DataSourceUtil.getSuffix());

            }
        }
        dataSourceRepository.save(dataSources);
        resource.setResStatus(ResourceStatus.PUBLISHED);
        resource.setResourceConfig(resourceConfigRepository.save(config));//默认权限
        resource.setUpdateUser(sysUser);//发布时间
        resource.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        resourceRepository.save(resource);

    }

    @Override
    public Page<Resource> findByApprovalAndCatalog(String approvalId, String catalogId, Integer page, Integer size) {

        return resourceRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.and(cb.equal(root.get("approvalMan").get("id"), approvalId),
                    cb.equal(root.get("catalogId").get("id"), catalogId));

            return predicate;
        }, new PageRequest(page - 1, size));

    }

    @Override
    public long countPublish() {
        Resource resource = new Resource();
        resource.setResStatus(5);
        return resourceRepository.count(Example.of(resource));
    }

    @Override
    public List findTopSubRes(Timestamp endTime, Integer size) {
        return resourceRepository.findTopSubRes(endTime, size);
    }

    @Override
    public List findPopRes(Timestamp endTime, Integer size) {
        return resourceRepository.findPopRes(endTime, size);
    }

    @Override
    public List findPopResByComId(Timestamp endTime, Integer size, String comId) {

        return resourceRepository.findPopResByComId(endTime, size, comId);
    }

    @Override
    public Resource findResourceInfoCanView(String resourceId, Company company) {

        List<ComEquipLevel> comEquipLevels = companyService.findComLevel(company.getComLevel());

        Resource resource = resourceRepository.findOne(resourceId);
        List<DataSource> dataSources = resource.getDas();
        //过滤掉非该单位可视的设备
        DataSourceUtil.filterCanView(comEquipLevels, dataSources);
        return resource;
    }

    @Override
    public List<VResAndSubscribeStatus> findResAndSubscribeStatus(Integer resMain, Integer resIndustry, String comId, String resName, String catalogId, String searchTime, String status, Integer page, Integer size) {
        Page<Resource> resources = resourceRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("resStatus"), ResourceStatus.PUBLISHED);
            if (resName != null && !"".equals(resName)) {
                predicate = cb.and(predicate, cb.like(root.get("resName"), "%" + resName + "%"));
            }
            if (catalogId != null && !"".equals(catalogId)) {
                predicate = cb.and(predicate, cb.equal(root.get("catalogId").get("id"), catalogId));

            }
            if (searchTime != null && !"".equals(searchTime)) {
                Calendar calendar = Calendar.getInstance();
                switch (searchTime) {

                    case "0"://一周之内
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        break;
                    case "1"://一个月
                        calendar.add(Calendar.MONTH, -1);
                        break;

                    case "2"://一年
                        calendar.add(Calendar.YEAR, -1);
                        break;
                    case "3"://三年
                        calendar.add(Calendar.YEAR, -3);
                        break;
                    default:
                }
                predicate = cb.and(predicate, cb.greaterThan(root.get("updateTime"), new Timestamp(calendar.getTimeInMillis())));
            }
            if (resMain != null) {
                predicate = cb.and(predicate, cb.equal(root.get("mainCategory"), resMain));
            }
            if (resIndustry != null) {
                predicate = cb.and(predicate, cb.equal(root.get("hyCategory"), resIndustry));
            }

            return predicate;
        }, new PageRequest(page - 1, size, Sort.Direction.DESC, "createTime"));//只查询已经发布的资源
        List<VResAndSubscribeStatus> vResAndSubscribeStatuses = new ArrayList<>();
        for (Resource resource : resources) {
            VResAndSubscribeStatus vResAndSubscribeStatus = new VResAndSubscribeStatus();
            if (resource.getCreateTime() != null) {
                vResAndSubscribeStatus.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(resource.getCreateTime()));
            }
            vResAndSubscribeStatus.setResId(resource.getId());
            vResAndSubscribeStatus.setResCase(resource.getResCase());
            vResAndSubscribeStatus.setResName(resource.getResName());
            vResAndSubscribeStatus.setResAbstract(resource.getResAbstract());
            vResAndSubscribeStatus.setResMain(dictItemService.getName(resource.getMainCategory()));
            vResAndSubscribeStatus.setResIndustry(dictItemService.getName(resource.getHyCategory()));
            vResAndSubscribeStatus.setIconRoot(resource.getIconRoot());
//            if (null == resource.getResSubscribes() || resource.getResSubscribes().size() == 0) {
//                vResAndSubscribeStatuses.add(vResAndSubscribeStatus);
            vResAndSubscribeStatus.setState(0);
//            }

            for (ResSubscribe resSubscribe : resource.getResSubscribes()) {
                if (comId.equals(resSubscribe.getComId().getId())) {
                    vResAndSubscribeStatus.setState(resSubscribe.getState());//如果查询到订阅则设为相应的订阅状态
                    vResAndSubscribeStatus.setResSubscribeId(resSubscribe.getId());
                }

            }
            String subscribeStatus = vResAndSubscribeStatus.getState() + "";
            if (status == null || "".equals(status)) {//两种情况 1.状态为空或为空串 2.状态和订阅状态一致
                vResAndSubscribeStatuses.add(vResAndSubscribeStatus);
            } else if ("2".equals(status)) {//已审核和已经拒绝的
                if (subscribeStatus.equals("2") || subscribeStatus.equals("3") || subscribeStatus.equals("5")) {
                    vResAndSubscribeStatuses.add(vResAndSubscribeStatus);
                }
            } else if ("0".equals(status)) {//未订阅和去订阅和已拒绝的
                if (subscribeStatus.equals("0") || subscribeStatus.equals("4") || subscribeStatus.equals("3")) {
                    vResAndSubscribeStatuses.add(vResAndSubscribeStatus);
                }
            } else if (subscribeStatus.equals(status)) {
                vResAndSubscribeStatuses.add(vResAndSubscribeStatus);
            }

        }

        return vResAndSubscribeStatuses;
    }
    /**
     * 重新发布(未使用)
     *
     * @param resId
     */
    @Override
    public void republish(String resId) {
        Resource resource = resourceRepository.findOne(resId);
        if (resource == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        if (!resource.getResStatus().equals(ResourceStatus.TO_PUBLISH)) {
            throw new ResourceException(ResourceException.CAN_NOT_REPUBLISH);
        }
        resource.setResStatus(ResourceStatus.PUBLISHED);
        resourceRepository.save(resource);


    }

    //撤销提交
    @Override
    public void unSubmit(String resId) {
        Resource resource = resourceRepository.findOne(resId);
        if (resource == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        if (resource.getResStatus() == ResourceStatus.PUBLISHED) {
            //发布不能改为待提交
            throw new ResourceException(ResourceException.CAN_NOT_UN_SUBMIT);
        }
        resource.setResStatus(ResourceStatus.TO_SUBMIT);
        resourceRepository.save(resource);
    }


    /**
     * 因为slave需要读取图片的输入流
     *
     * @param fileName
     * @return
     */
    @Override
    @SneakyThrows
    public byte[] download(String fileName) {
        log.debug("fileName:" + fileName);
        File file = new File(resImagePath + fileName);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int ch;
        while ((ch = inputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, ch);
        }
        byte data[] = byteStream.toByteArray();
        byteStream.close();
        return data;
    }

}
