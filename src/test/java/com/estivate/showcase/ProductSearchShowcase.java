package com.estivate.showcase;

import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class ProductSearchShowcase {

    @Builder
    @AllArgsConstructor
    public static class SearchDto{

        

    }

    @Test
    public void productSearch(){

        SearchDto searchDto = SearchDto.builder()
        .build();

        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .importCriterion(searchDto);

    }

}
