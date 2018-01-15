package com.fable.mssg.catalog.service;

import com.fable.mssg.catalog.repository.CatalogRepository;
import com.fable.mssg.catalog.service.exception.CatalogException;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.resource.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @Description
 * @Author wangmeng 2017/10/10
 */
@Service
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Override
    public Catalog save(Catalog catalog) {
        Catalog example = new Catalog();
        example.setCatalogName(catalog.getCatalogName());
        if(catalogRepository.exists(Example.of(example))){
            throw new CatalogException(CatalogException.CATALOG_NAME_ALREADY_EXIST);
        }
        example.setCatalogName(null);
        example.setCatalogCode(catalog.getCatalogCode());
        if(catalogRepository.exists(Example.of(example))){
            throw new CatalogException(CatalogException.CATALOG_CODE_ALREADY_EXIST);
        }

        return catalogRepository.save(catalog);
    }

    @Override
    public Catalog findById(String id){
        return catalogRepository.findOne(id);
    }

    @Override
    public void modify(Catalog catalog) {
        Catalog catalogInDB = findById(catalog.getId());
        if(catalogInDB==null){
            throw new CatalogException(CatalogException.CATALOG_NOT_FOUND);
        }
        //TODO 名称不重复
        catalogInDB.setApproval(catalog.getApproval());
        catalogInDB.setCatalogCode(catalog.getCatalogCode());
        catalogInDB.setCatalogName(catalog.getCatalogName());
        catalogInDB.setDescription(catalog.getDescription());
        catalogInDB.setUpdateUser(catalog.getUpdateUser());
        catalogInDB.setUpdateTime(catalog.getUpdateTime());
        catalogInDB.setComId(catalog.getComId());
        catalogInDB.setApproval(catalog.getApproval());
        catalogRepository.save(catalogInDB);
    }

    @Override
    public void delete(String id) {
        Catalog catalog = new Catalog();
        catalog.setId(id);
        //删除之前要先将其下的资源删除
        if(resourceRepository.findByCatalogId(catalog).size()!=0){
            throw new CatalogException(CatalogException.CATALOG_REMAIN_RESOURCE);
        }
        if(catalogRepository.exists(id)){
            catalogRepository.delete(id);
        }

    }

    @Override
    public List<Catalog> findAll() {
        return catalogRepository.findAll();
    }


    @Override
    public List<Catalog> findByComId(String comId) {
        Company company = new Company();
        company.setId(comId);
        return catalogRepository.findByComId(company);
    }
}
