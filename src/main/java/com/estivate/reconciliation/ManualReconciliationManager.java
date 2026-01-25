package com.estivate.reconciliation;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.estivate.Estivate;
import com.estivate.Entity.InsertDate;
import com.estivate.context.Context;
import com.estivate.reconciliation.EstivateReconciliation.IManualResolver;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages manual reconciliations that should be executed only once.
 * Tracks executed reconciliations in the ESTIVATE_RECONCILIATION table.
 */
@Slf4j
public class ManualReconciliationManager {

    private final Context context;

//    @Getter
//    private boolean tableInitialized = false;

    public ManualReconciliationManager(Context context) {
        this.context = context;

        if(!context.showTables().contains(context.nameMapper.toTableName(ManualReconciliationEntity.class))) {
            log.info("Creating reconciliation tracking table: {}", context.nameMapper.toTableName(ManualReconciliationEntity.class));
            context.createTable(ManualReconciliationEntity.class);
        }
    }


    

    /**
     * Checks if a resolver has already been executed (regardless of status).
     */
    @SneakyThrows
    public ManualReconciliationEntity getExecutionRecord(String resolverId) {

        return Estivate.selectQuery(ManualReconciliationEntity.class)
            .eq(ManualReconciliationEntity::getResolverId, resolverId)
            .fetchSingle(context);
            
    }

    private void recordExecution(String resolverId, ReconciliationResult result, String reason) {
        ManualReconciliationEntity entity = new ManualReconciliationEntity();
        entity.resolverId = resolverId;
        entity.setStatus(result);
        entity.setReason(reason);

    }


    /**
     * Executes a single manual resolver if it hasn't been executed before.
     * 
     * @param resolver The resolver to execute
     * @return true if executed successfully (or already executed), false if failed
     */
    public boolean executeResolver(IManualResolver resolver) {


        String resolverId = resolver.getClass().getName();
        ManualReconciliationEntity executionRecord = getExecutionRecord(resolverId);

        // Check if already executed
        if (executionRecord != null && executionRecord.getStatus() == ReconciliationResult.SOLVED) {
            log.debug("Skipping already executed resolver: {}", resolverId);
            return true;
        }

        // Execute the resolver
        try {
            log.info("Executing manual resolver: {}", resolverId);
            resolver.resolve(context);
            recordExecution(resolverId, ReconciliationResult.SOLVED, null);
            log.info("Successfully executed resolver: {}", resolverId);
            return true;
        } catch (Exception e) {
            String errorMessage = e.getClass().getName() + ": " + e.getMessage();
            recordExecution(resolverId, ReconciliationResult.FAILED, errorMessage);
            log.error("Failed to execute resolver: {} - {}", resolverId, errorMessage, e);
            return false;
        }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table
    public static class ManualReconciliationEntity {

        @Id
        @Column(length = 512)
        String resolverId;

        @InsertDate
        LocalDateTime executedAt;

        @Enumerated(EnumType.STRING)
        @Column(length = 32)
        ReconciliationResult status;

        @Column(length = 2048)
        String reason;

    }

    

}
