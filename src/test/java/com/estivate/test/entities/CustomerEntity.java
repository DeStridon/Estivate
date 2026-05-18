package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.Entity.InsertDate;
import com.estivate.Entity.UpdateDate;
import com.estivate.index.Annotations.TableIndexes;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;

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
@TableIndexes({
	@TableIndex(type = IndexType.UNIQUE, columns = { @IndexColumn(value = CustomerEntity.Fields.email) }),
	@TableIndex(columns = { @IndexColumn(value = CustomerEntity.Fields.name) }),
	@TableIndex(name = "FT_NAME", type = IndexType.FULLTEXT, columns = { @IndexColumn(value = CustomerEntity.Fields.name) })
})
public class CustomerEntity extends AbstractEntity{

    String name;

    String email;

    String address;

	@Enumerated(EnumType.STRING)
    Country country;

    boolean emailVerified;

	@InsertDate
    Date created;

    @UpdateDate
    Date updated;

    Date archived;
    
    public enum Country{
    	GERMANY,
    	SPAIN,
    	FRANCE,
    	USA,
    	UK,
    	AUSTRALIA,
    	NEW_ZEALAND,
    	SOUTH_AFRICA,
    	INDIA,
    	CHINA,
    	JAPAN,
    	KOREA,
    	INDONESIA,
    	PHILIPPINES,
    	THAILAND,
    	VIETNAM,
    }

}
