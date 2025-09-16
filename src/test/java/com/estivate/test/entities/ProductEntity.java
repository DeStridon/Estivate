package com.estivate.test.entities;

import java.util.List;

import javax.persistence.Convert;

import com.estivate.test.entities.misc.StringListConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

    ProductCategory category;

    Float price;

    Integer available;

    @Convert(converter = StringListConverter.class)
    List<String> tags;


    public enum ProductCategory{
        Camera,
        Computer,
        Screen,
        Accessories
    }

}
