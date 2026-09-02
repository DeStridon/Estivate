package com.estivate.test.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Table(name = "PRECISION_SCALE_ENTITY")
public class PrecisionScaleEntity extends AbstractEntity {

    @Column(precision = 10, scale = 2)
    BigDecimal amount;

    @Column(columnDefinition = "DECIMAL(8,4)")
    BigDecimal rate;

    String label;
}
