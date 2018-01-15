package com.fable.mssg.service.resource;

import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.resmanager.ResourceConfig;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.vo.subscribe.VResAndSubscribeStatus;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

/**
 * description  资源操作接口
 *
 * @author xiejk 2017/9/30
 */
public interface ResourceService {

    /**
     * 根据查询条件,查询方式查询资源
     *
     * @param type       查询的方式  0.表示查询全部 3.表示查询待发布 5.表示查询已发布 6.表示查询已撤销
     * @param resName    资源名称
     * @param catalogId  目录id
     * @param searchTime 查询时间
     * @return List<Resource>
     */
    Page<Resource> findAllResourceByCondition(String type, String resName, String catalogId, String searchTime,Integer page,Integer size);

    /**
     * 分页查询对应目录资源信息
     *
     * @param cid  目录id
     * @param size 每页显示数量
     * @param page 当前页数
     * @return Page<Resource>
     */
    Page<Resource> findByCatalogId(String cid, int size, int page);

    /**
     * 创建新资源
     *
     * @param resource 新资源对象
     */
    void save(Resource resource);

    /**
     * 根据id 查询单个的资源
     *
     * @param rid 资源id
     * @return Resource
     */
    Resource findOneRes(String rid);

    /**
     * 修改待提交的资源
     *
     * @param res 资源对象
     * @return 是否修改成功
     */
    void updateResBeforePending(Resource res);

    /**
     * 删除资源（未提交才能删除）
     *
     * @param rid 资源id
     * @return 是否删除成功
     */
    void delRes(String rid);

    /**
     * 提交资源（更新资源到待审核状态）
     *
     * @param rid 资源id
     * @return 是否更新成功
     */
    void pendRes(String rid);

    /**
     * 审核资源
     *
     * @param rid       资源id
     * @param resStatus 资源状态
     * @return 是否审核成功
     */
    void approvalRes(String rid, int resStatus);

    /**
     * 撤销资源
     *
     * @param rid 资源id
     * @return 是否撤销成功
     */
    void revokeRes(String rid);

    /**
     * 发布资源
     *
     * @param rid      资源id
     * @param dataSources
     * @param sysUser
     * @return 是否发布成功
     */
    void publishRes(String rid, List<DataSource> dataSources, ResourceConfig defaultPrv, SysUser sysUser);


    Page<Resource> findByApprovalAndCatalog(String approvalId,String catalogId,Integer page,Integer size);

    long countPublish();

    List findTopSubRes(Timestamp timestamp, Integer size);

    List findPopRes(Timestamp timestamp, Integer size);

    List findPopResByComId(Timestamp timestamp, Integer size, String comId);

    /**
     * 根据资源id和单位id查询可以观看的数据源
     * @param resourceId
     * @param company
     * @return
     */
    Resource findResourceInfoCanView(String resourceId, Company company);

    List<VResAndSubscribeStatus> findResAndSubscribeStatus(Integer resMain,Integer resIndustry,String comId,String resName,String catalogId,String searchTime,String status,Integer page, Integer size);

    void republish(String resId);

    void unSubmit(String resId);
}
