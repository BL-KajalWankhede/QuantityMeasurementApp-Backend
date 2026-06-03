package com.quantitymeasurement.model;

import java.time.LocalDateTime;
import java.util.List;

public class QuantityMeasurementDTO {
    private Long id;
    private LocalDateTime createdAt;
    private Double thisValue;
    private String thisUnit;
    private String thisMeasurementType;
    private Double thatValue;
    private String thatUnit;
    private String thatMeasurementType;
    private String operation;
    private String resultString;
    private Double resultValue;
    private String resultUnit;
    private String resultMeasurementType;
    private String errorMessage;
    private boolean error;

    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity entity) {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.id = entity.getId();
        dto.createdAt = entity.getCreatedAt();
        dto.thisValue = entity.getLeftValue();
        dto.thisUnit = entity.getLeftUnit();
        dto.thisMeasurementType = entity.getLeftMeasurementType();
        dto.thatValue = entity.getRightValue();
        dto.thatUnit = entity.getRightUnit();
        dto.thatMeasurementType = entity.getRightMeasurementType();
        dto.operation = entity.getOperationType().name();
        dto.resultString = entity.getResultString();
        dto.resultValue = entity.getResultValue() != null ? entity.getResultValue() : entity.getScalarResult();
        dto.resultUnit = entity.getResultUnit();
        dto.resultMeasurementType = entity.getResultMeasurementType();
        dto.errorMessage = entity.getErrorMessage();
        dto.error = entity.isError();
        return dto;
    }

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream().map(QuantityMeasurementDTO::fromEntity).toList();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Double getThisValue() {
        return thisValue;
    }

    public String getThisUnit() {
        return thisUnit;
    }

    public String getThisMeasurementType() {
        return thisMeasurementType;
    }

    public Double getThatValue() {
        return thatValue;
    }

    public String getThatUnit() {
        return thatUnit;
    }

    public String getThatMeasurementType() {
        return thatMeasurementType;
    }

    public String getOperation() {
        return operation;
    }

    public String getResultString() {
        return resultString;
    }

    public Double getResultValue() {
        return resultValue;
    }

    public String getResultUnit() {
        return resultUnit;
    }

    public String getResultMeasurementType() {
        return resultMeasurementType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return error;
    }

    @Override
    public String toString() {
        if (error) {
            return "ERROR: " + errorMessage;
        }

        String finalResult = resultString;
        if (finalResult == null) {
            if (resultUnit != null) {
                finalResult = resultValue + " " + resultUnit;
            } else if (resultValue != null) {
                finalResult = String.valueOf(resultValue);
            } else {
                finalResult = "UNKNOWN";
            }
        }

        if (operation == null || operation.equals("CONVERT")) {
            return thisValue + " " + thisUnit + " = " + finalResult;
        }

        String symbol = switch (operation) {
            case "ADD" -> "+";
            case "SUBTRACT" -> "-";
            case "DIVIDE" -> "/";
            case "COMPARE" -> "compared to";
            default -> operation;
        };

        if (thatUnit == null || thatUnit.isBlank()) {
            return thisValue + " " + thisUnit + " " + symbol + " " + thatValue + " = " + finalResult;
        }

        return thisValue + " " + thisUnit + " " + symbol + " " + thatValue + " " + thatUnit + " = " + finalResult;
    }
}
