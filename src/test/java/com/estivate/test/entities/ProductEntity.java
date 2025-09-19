package com.estivate.test.entities;

import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Enumerated;

import com.estivate.test.entities.CustomerEntity.Country;
import com.estivate.test.entities.misc.StringListConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ProductEntity extends AbstractEntity{

    String name;

    String description;

    @Enumerated
    ProductCategory category;

    Float price;

    Integer stock;

    @Convert(converter = StringListConverter.class)
    List<String> tags;

    @Enumerated
    List<Country> availableCountries;

    public enum ProductCategory{
        Camera,
        Computer,
        Screen,
        Accessories
    }

}
