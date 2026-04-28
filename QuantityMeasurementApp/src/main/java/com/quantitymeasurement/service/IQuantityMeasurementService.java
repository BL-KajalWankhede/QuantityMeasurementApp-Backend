package com.quantitymeasurement.service;


import com.quantitymeasurement.model.QuantityDTO;
import com.quantitymeasurement.model.QuantityMeasurementEntity;

public interface IQuantityMeasurementService {
    QuantityMeasurementEntity compare(QuantityDTO leftQuantity, QuantityDTO rightQuantity);

    QuantityMeasurementEntity convert(QuantityDTO sourceQuantity, String targetUnitName);

    QuantityMeasurementEntity add(QuantityDTO leftQuantity, QuantityDTO rightQuantity);

    QuantityMeasurementEntity add(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String targetUnitName);

    QuantityMeasurementEntity subtract(QuantityDTO leftQuantity, QuantityDTO rightQuantity);

    QuantityMeasurementEntity subtract(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String targetUnitName);

    QuantityMeasurementEntity divide(QuantityDTO leftQuantity, QuantityDTO rightQuantity);
}