package com.quantitymeasurement.repository;

import com.quantitymeasurement.util.ApplicationConfig;

public final class QuantityMeasurementRepositoryFactory {
    private QuantityMeasurementRepositoryFactory() {
    }

    public static IQuantityMeasurementRepository create(ApplicationConfig config) {
        if (config.useDatabaseRepository()) {
            return new QuantityMeasurementDatabaseRepository(config);
        }
        return QuantityMeasurementCacheRepository.getInstance();
    }
}