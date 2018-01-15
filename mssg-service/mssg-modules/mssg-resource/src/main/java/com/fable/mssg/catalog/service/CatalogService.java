package com.fable.mssg.catalog.service;

import com.fable.mssg.domain.resmanager.Catalog;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/10
 */
public interface CatalogService {
    Catalog save(Catalog catalog);

    Catalog findById(String id);

    void modify(Catalog catalog);

    void delete(String id);

    List<Catalog> findAll();

    List<Catalog> findByComId(String comId);
}
