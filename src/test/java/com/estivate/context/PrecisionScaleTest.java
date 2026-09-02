package com.estivate.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.query.SelectQuery;
import com.estivate.reconciliation.ColumnModel.EntityColumn;
import com.estivate.reconciliation.ColumnTypeParts;
import com.estivate.reconciliation.EstivateReconciliation.Mismatch;
import com.estivate.reconciliation.EstivateReconciliation.ModifyColumnDelta;
import com.estivate.reconciliation.ReconciliationManager;
import com.estivate.reconciliation.TableField;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.PrecisionScaleEntity;
import com.estivate.util.FieldUtils;

import jakarta.persistence.Column;

public class PrecisionScaleTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void parseDecimalColumnType() {
        ColumnTypeParts parts = context.parseColumnType("DECIMAL(10,2)");
        assertEquals("DECIMAL", parts.getType());
        assertEquals(10, parts.getLength());
        assertEquals(2, parts.getScale());
    }

    @Test
    public void entityColumnReadsPrecisionAndScale() {
        EntityColumn amountColumn = Context.getEntityColumn(FieldUtils.findField(PrecisionScaleEntity.class, PrecisionScaleEntity.Fields.amount));
        assertEquals(10, amountColumn.getDesignedPrecision());
        assertEquals(2, amountColumn.getDesignedScale());

        EntityColumn rateColumn = Context.getEntityColumn(FieldUtils.findField(PrecisionScaleEntity.class, PrecisionScaleEntity.Fields.rate));
        assertEquals("DECIMAL", rateColumn.getDesignedType());
        assertEquals(8, rateColumn.getDesignedPrecision());
        assertEquals(4, rateColumn.getDesignedScale());
    }

    @Test
    public void tableFieldIncludesPrecisionAndScale() {
        TableField amountField = context.getTableField(FieldUtils.findField(PrecisionScaleEntity.class, PrecisionScaleEntity.Fields.amount));
        assertEquals("DECIMAL", amountField.getType());
        assertEquals(10, amountField.getDimension());
        assertEquals(2, amountField.getScale());
    }

    @Test
    public void createTableDeclarationIncludesDecimalPrecisionAndScale() {
        TableField amountField = context.getTableField(FieldUtils.findField(PrecisionScaleEntity.class, PrecisionScaleEntity.Fields.amount));
        TableField rateField = context.getTableField(FieldUtils.findField(PrecisionScaleEntity.class, PrecisionScaleEntity.Fields.rate));
        assertEquals(10, amountField.getDimension());
        assertEquals(2, amountField.getScale());
        assertEquals(8, rateField.getDimension());
        assertEquals(4, rateField.getScale());
    }

    @Test
    public void selectAndUpdateRoundTripAppliesScale() {
        context.execute(Estivate.Tools.createTableFullQuery(PrecisionScaleEntity.class).ifNotExists());

        PrecisionScaleEntity entity = PrecisionScaleEntity.builder()
                .amount(new BigDecimal("123.456"))
                .rate(new BigDecimal("1.23456"))
                .label("test")
                .build();
        context.insert(entity);

        SelectQuery<PrecisionScaleEntity> query = new SelectQuery<>(PrecisionScaleEntity.class)
                .eq(AbstractEntity.Fields.id, entity.getId());
        PrecisionScaleEntity loaded = context.fetchSingle(query);

        assertEquals(new BigDecimal("123.46"), loaded.getAmount());
        assertEquals(new BigDecimal("1.2346"), loaded.getRate());

        loaded.setAmount(new BigDecimal("99.999"));
        context.update(loaded);

        PrecisionScaleEntity updated = context.fetchSingle(query);
        assertEquals(new BigDecimal("100.00"), updated.getAmount());
    }

    @Test
    public void reconciliationDetectsScaleMismatch() {
        context.execute(Estivate.Tools.createTableFullQuery(PrecisionScaleEntity.class).ifNotExists());

        context.execute(Estivate.alterQuery(PrecisionScaleEntity.class)
                .modifyColumn(PrecisionScaleEntity.Fields.amount, amountColumnWithScale(3)));

        ReconciliationManager manager = new ReconciliationManager(context)
                .addEntities(PrecisionScaleEntity.class);

        ModifyColumnDelta delta = manager.getDifferences(PrecisionScaleEntity.class).stream()
                .filter(ModifyColumnDelta.class::isInstance)
                .map(ModifyColumnDelta.class::cast)
                .filter(diff -> PrecisionScaleEntity.Fields.amount.equals(diff.getEntityField().getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(delta);
        assertTrue(delta.getMismatchs().contains(Mismatch.SCALE));
        assertEquals(2, delta.getProjectedDefinition().getScale());
        assertEquals(3, delta.getDatabaseDefinition().getScale());


        assertEquals("NUMERIC", delta.getDatabaseDefinition().getType());

    }

    private static EntityColumn amountColumnWithScale(int scale) {
        return EntityColumn.builder()
                .name(PrecisionScaleEntity.Fields.amount)
                .type(BigDecimal.class)
                .designedPrecision(10)
                .designedScale(scale)
                .build();
    }

    public static class ColumnDefinitionEntity {
        @Column(columnDefinition = "NUMERIC(12,3)")
        BigDecimal value;
    }

    @Test
    public void columnDefinitionOnlySetsPrecisionAndScale() {
        EntityColumn column = Context.getEntityColumn(FieldUtils.findField(ColumnDefinitionEntity.class, "value"));
        assertEquals("NUMERIC", column.getDesignedType());
        assertEquals(12, column.getDesignedPrecision());
        assertEquals(3, column.getDesignedScale());
    }
}
