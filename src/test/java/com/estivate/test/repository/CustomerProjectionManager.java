package com.estivate.test.repository;

import java.util.List;

import com.estivate.repository.Repository;
import com.estivate.spring.EstivateManager;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.projection.CustomerProjection.CustomerCountAliasByCountryProjection;

@EstivateManager
public abstract class CustomerProjectionManager extends Repository<CustomerEntity> {

    public abstract List<CustomerCountAliasByCountryProjection> findAll();

}
