package com.estivate.showcase;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.query.QueryMapping;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;
import com.estivate.test.entities.UserProductRatingEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;

public class ProductSearchShowcase {
	
	Context context = DatabaseGenerator.getContext();

    @Builder
    @AllArgsConstructor
    public static class ProductSearchInput{
    	
    	@QueryMapping.LikeIfNotNull(entity = ProductEntity.class, attribute = ProductEntity.Fields.name)
    	String nameLike;
    	
    	@QueryMapping.GteIfNotNull(entity = ProductEntity.class, attribute = ProductEntity.Fields.price)
    	Float priceMin;
    	
    	@QueryMapping.LteIfNotNull(entity = ProductEntity.class, attribute = ProductEntity.Fields.price)
    	Float priceMax;
    	
    	@QueryMapping.InIfNotEmpty(entity = ProductEntity.class, attribute = ProductEntity.Fields.category)
    	List<ProductCategory> categoryIn;
    	
    	@QueryMapping.GteIfNotNull(entity = UserProductRatingEntity.class, attribute = UserProductRatingEntity.Fields.rating)
    	Integer ratingMin;

    }
    
    public static class ProductSearchOutput{
    	
    	@Projection.Attribute(entity = ProductEntity.class, attribute = ProductEntity.Fields.name)
    	String name;
    	
    	@Projection.Attribute(entity = ProductEntity.class, attribute = ProductEntity.Fields.price)
    	Float price;
    	
    	@Projection.Count(entity = OrderLineEntity.class, attribute = AbstractEntity.Fields.id, alias = "inCartCount")
    	Integer inCartCount;
    	
		@Projection.Avg(entity = UserProductRatingEntity.class, attribute = UserProductRatingEntity.Fields.rating, alias = "rating")
		Float rating;
    	
    }

	@Test
    public void productSearch(){

		// Faking the search input
        ProductSearchInput searchInput = ProductSearchInput.builder()
			.ratingMin(4)
			.priceMin(100f)
			.priceMax(1000f)
			.categoryIn(Arrays.asList(ProductCategory.Clothing, ProductCategory.Shoes, ProductCategory.Jewelry))
        .build();

		// Building the query
        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
        	.joinInner(ProductEntity.class, OrderLineEntity.class)
        	.joinInner(ProductEntity.class, UserProductRatingEntity.class)
			.groupBy(ProductEntity.class, AbstractEntity.Fields.id)
            .importCriterionFromQueryMapping(searchInput)
        	.selectAll(ProductSearchOutput.class);
        
        // Executing the query
        List<ProductSearchOutput> results = query.fetchListAs(context, ProductSearchOutput.class);

		Assert.assertEquals(4, query.getSelects().size());

		System.out.println(results);

    }

}
