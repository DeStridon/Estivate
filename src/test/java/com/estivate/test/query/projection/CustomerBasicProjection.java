package com.estivate.test.query.projection;

import com.estivate.query.Projection;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

/**
 * Simple projection class with basic attributes
 */
public class CustomerBasicProjection {
    @Projection.Attribute(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id)
    Long id;
    
    @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.name)
    String name;
    
    @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.email)
    String email;
    
    public CustomerBasicProjection() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return "CustomerBasicProjection{id=" + id + ", name=" + name + ", email=" + email + "}";
    }
}

