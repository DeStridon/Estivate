package com.estivate.test.manager;

import java.util.List;

import com.estivate.manager.ManagerInterceptor.EntityManager;
import com.estivate.spring.EstivateManager;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.projection.CustomerProjection.CustomerCountAliasByCountryProjection;

@EstivateManager
public abstract class CustomerProjectionManager extends EntityManager<CustomerEntity> {

    public abstract List<CustomerCountAliasByCountryProjection> findAll();

}
