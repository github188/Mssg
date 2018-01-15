package com.fable.mssg.catalog.repository;

import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.resmanager.Catalog;
import com.slyak.spring.jpa.GenericJpaRepository;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/10
 */
public interface CatalogRepository extends GenericJpaRepository<Catalog,String> {

    List<Catalog> findByComId(Company company);
}
