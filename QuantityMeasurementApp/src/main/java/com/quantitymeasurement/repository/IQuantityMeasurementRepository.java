package com.quantitymeasurement.repository;

import com.quantitymeasurement.model.QuantityMeasurementEntity;
import java.util.List;

public interface IQuantityMeasurementRepository {
    QuantityMeasurementEntity save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType);

    List<QuantityMeasurementEntity> getMeasurementsByMeasurementType(String measurementType);

    int getTotalCount();

    void deleteAllMeasurements();

    default void clear() {
        deleteAllMeasurements();
    }

    default String getPoolStatistics() {
        return "Connection pool statistics unavailable";
    }

    default void releaseResources() {
    }
}