package com.estivate.test.managers;

import java.util.List;

import com.estivate.manager.ManagerInterceptor.EntityManager;
import com.estivate.spring.EstivateManager;
import com.estivate.test.entities.CustomerEntity;

/**
 * Example manager demonstrating Spring autowiring integration.
 * 
 * <p>By annotating with {@code @EstivateManager}, this class will be
 * automatically implemented and registered as a Spring bean when
 * {@code @EnableEstivateManagers} is used.</p>
 */
@EstivateManager
public abstract class CustomerManager extends EntityManager<CustomerEntity> {
    
    public abstract CustomerEntity findById(long id);

    public abstract List<CustomerEntity> findByIdInIfNotEmpty(List<Long> ids);

    public abstract List<CustomerEntity> findByNameAndEmail(String name, String email);

    public abstract List<CustomerEntity> findByNameAndEmailAndAddress(String name, String email, String address);

    public abstract List<CustomerEntity> findByIdBetween(long id1, long id2);
    
    public abstract List<CustomerEntity> findByCountryOrderByCreatedDesc(CustomerEntity.Country country);

    public abstract List<CustomerEntity> findByIdNotEq(long id);
}

