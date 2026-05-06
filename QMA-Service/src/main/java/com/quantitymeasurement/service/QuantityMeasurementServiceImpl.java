package com.quantitymeasurement.service;

import com.equality.*;
import com.quantitymeasurement.exception.QuantityMeasurementException;
import com.quantitymeasurement.model.*;
import com.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.quantitymeasurement.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {
    private final QuantityMeasurementRepository repository;
    private final UserRepository userRepository;

    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String userEmail) {
        return execute(OperationType.COMPARE, leftQuantity, rightQuantity, userEmail, () -> {
            boolean isEqual = compareQuantities(toModel(leftQuantity), toModel(rightQuantity));
            return new QuantityMeasurementEntity(OperationType.COMPARE, leftQuantity, rightQuantity, isEqual, null);
        });
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO sourceQuantity, String targetUnitName, String userEmail) {
        return execute(OperationType.CONVERT, sourceQuantity, null, userEmail, () -> {
            QuantityModel<?> sourceModel = toModel(sourceQuantity);
            IMeasurable targetUnit = resolveTargetUnit(sourceModel.getUnit().getMeasurementType(), targetUnitName);
            Quantity<?> result = convertTo(sourceModel, targetUnit);
            return new QuantityMeasurementEntity(OperationType.CONVERT, sourceQuantity, toDto(result));
        });
    }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String targetUnitName, String userEmail) {
        return execute(OperationType.ADD, leftQuantity, rightQuantity, userEmail, () -> {
            Quantity<?> result = addQuantities(toModel(leftQuantity), toModel(rightQuantity), targetUnitName);
            return new QuantityMeasurementEntity(OperationType.ADD, leftQuantity, rightQuantity, toDto(result));
        });
    }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String targetUnitName, String userEmail) {
        return execute(OperationType.SUBTRACT, leftQuantity, rightQuantity, userEmail, () -> {
            Quantity<?> result = subtractQuantities(toModel(leftQuantity), toModel(rightQuantity), targetUnitName);
            return new QuantityMeasurementEntity(OperationType.SUBTRACT, leftQuantity, rightQuantity, toDto(result));
        });
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO leftQuantity, QuantityDTO rightQuantity, String userEmail) {
        return execute(OperationType.DIVIDE, leftQuantity, rightQuantity, userEmail, () -> {
            double result = divideQuantities(toModel(leftQuantity), toModel(rightQuantity));
            return new QuantityMeasurementEntity(OperationType.DIVIDE, leftQuantity, rightQuantity, null, result);
        });
    }

    @Override
    public List<QuantityMeasurementDTO> getOperationHistory(OperationType operationType) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByOperationTypeOrderByCreatedAtAsc(operationType));
    }

    @Override
    public List<QuantityMeasurementDTO> getMeasurementHistory(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByLeftMeasurementTypeOrderByCreatedAtAsc(measurementType.trim().toUpperCase()));
    }

    @Override
    public long getOperationCount(OperationType operationType) {
        return repository.countByOperationTypeAndErrorFalse(operationType);
    }

    @Override
    public List<QuantityMeasurementDTO> getErroredHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByErrorTrueOrderByCreatedAtAsc());
    }

    @Override
    public List<QuantityMeasurementDTO> getUserHistory(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            return List.of();
        }
        return QuantityMeasurementDTO.fromEntityList(repository.findByUserEmail(userEmail));
    }

    private QuantityMeasurementDTO execute(OperationType operationType, QuantityDTO leftQuantity,
            QuantityDTO rightQuantity, String userEmail,
            Operation operation) {
        validateDto(leftQuantity);
        if (operationType != OperationType.CONVERT) {
            validateDto(rightQuantity);
        }
        try {
            return saveAndConvert(operation.perform(), userEmail);
        } catch (RuntimeException exception) {
            QuantityMeasurementEntity errorEntity = new QuantityMeasurementEntity(operationType, leftQuantity,
                    rightQuantity, mapException(operationType, exception).getMessage());
            saveAndConvert(errorEntity, userEmail);
            throw mapException(operationType, exception);
        }
    }

    private QuantityMeasurementDTO saveAndConvert(QuantityMeasurementEntity entity, String userEmail) {
        findUser(userEmail).ifPresent(entity::setUser);
        return QuantityMeasurementDTO.fromEntity(repository.save(entity));
    }

    private java.util.Optional<UserEntity> findUser(String userEmail) {
        if (userEmail != null && !userEmail.isBlank()) {
            return userRepository.findByEmailIgnoreCase(userEmail);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.Optional.empty();
        }
        String email = authentication.getName();
        if (email == null || "anonymousUser".equalsIgnoreCase(email)) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(email);
    }

    private void validateDto(QuantityDTO quantityDTO) {
        if (quantityDTO == null) {
            throw new QuantityMeasurementException("Quantity input must not be null");
        }
        if (quantityDTO.getValue() == null) {
            throw new QuantityMeasurementException("Quantity value must not be null");
        }
        if (Double.isNaN(quantityDTO.getValue()) || Double.isInfinite(quantityDTO.getValue())) {
            throw new QuantityMeasurementException("Quantity value must be a finite number");
        }
    }

    private QuantityModel<?> toModel(QuantityDTO dto) {
        try {
            IMeasurable unit = IMeasurable.resolveUnit(dto.getMeasurementType(), dto.getUnitName());
            return new QuantityModel<>(dto.getValue(), unit);
        } catch (IllegalArgumentException exception) {
            throw new QuantityMeasurementException("Invalid quantity unit or measurement type", exception);
        }
    }

    private IMeasurable resolveTargetUnit(String measurementType, String targetUnitName) {
        try {
            return IMeasurable.resolveUnit(measurementType, targetUnitName);
        } catch (IllegalArgumentException exception) {
            throw new QuantityMeasurementException("Invalid target unit", exception);
        }
    }

    private Quantity<?> convertTo(QuantityModel<?> sourceModel, IMeasurable targetUnit) {
        if (sourceModel.getUnit() instanceof LengthUnit source && targetUnit instanceof LengthUnit target) {
            return new Quantity<>(sourceModel.getValue(), source).convertTo(target);
        }
        if (sourceModel.getUnit() instanceof WeightUnit source && targetUnit instanceof WeightUnit target) {
            return new Quantity<>(sourceModel.getValue(), source).convertTo(target);
        }
        if (sourceModel.getUnit() instanceof VolumeUnit source && targetUnit instanceof VolumeUnit target) {
            return new Quantity<>(sourceModel.getValue(), source).convertTo(target);
        }
        if (sourceModel.getUnit() instanceof TemperatureUnit source && targetUnit instanceof TemperatureUnit target) {
            return new Quantity<>(sourceModel.getValue(), source).convertTo(target);
        }
        throw new QuantityMeasurementException("Cannot convert quantities of different categories");
    }

    private Quantity<?> addQuantities(QuantityModel<?> leftModel, QuantityModel<?> rightModel, String targetUnitName) {
        IMeasurable targetUnit = resolveTargetUnit(leftModel.getUnit().getMeasurementType(), targetUnitName);
        if (leftModel.getUnit() instanceof LengthUnit left && rightModel.getUnit() instanceof LengthUnit right
                && targetUnit instanceof LengthUnit target) {
            return new Quantity<>(leftModel.getValue(), left).add(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof WeightUnit left && rightModel.getUnit() instanceof WeightUnit right
                && targetUnit instanceof WeightUnit target) {
            return new Quantity<>(leftModel.getValue(), left).add(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof VolumeUnit left && rightModel.getUnit() instanceof VolumeUnit right
                && targetUnit instanceof VolumeUnit target) {
            return new Quantity<>(leftModel.getValue(), left).add(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof TemperatureUnit left && rightModel.getUnit() instanceof TemperatureUnit right
                && targetUnit instanceof TemperatureUnit target) {
            return new Quantity<>(leftModel.getValue(), left).add(new Quantity<>(rightModel.getValue(), right), target);
        }
        throw new QuantityMeasurementException("Cannot add quantities of different categories");
    }

    private Quantity<?> subtractQuantities(QuantityModel<?> leftModel, QuantityModel<?> rightModel,
            String targetUnitName) {
        IMeasurable targetUnit = resolveTargetUnit(leftModel.getUnit().getMeasurementType(), targetUnitName);
        if (leftModel.getUnit() instanceof LengthUnit left && rightModel.getUnit() instanceof LengthUnit right
                && targetUnit instanceof LengthUnit target) {
            return new Quantity<>(leftModel.getValue(), left)
                    .subtract(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof WeightUnit left && rightModel.getUnit() instanceof WeightUnit right
                && targetUnit instanceof WeightUnit target) {
            return new Quantity<>(leftModel.getValue(), left)
                    .subtract(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof VolumeUnit left && rightModel.getUnit() instanceof VolumeUnit right
                && targetUnit instanceof VolumeUnit target) {
            return new Quantity<>(leftModel.getValue(), left)
                    .subtract(new Quantity<>(rightModel.getValue(), right), target);
        }
        if (leftModel.getUnit() instanceof TemperatureUnit left && rightModel.getUnit() instanceof TemperatureUnit right
                && targetUnit instanceof TemperatureUnit target) {
            return new Quantity<>(leftModel.getValue(), left)
                    .subtract(new Quantity<>(rightModel.getValue(), right), target);
        }
        throw new QuantityMeasurementException("Cannot subtract quantities of different categories");
    }

    private double divideQuantities(QuantityModel<?> leftModel, QuantityModel<?> rightModel) {
        if (leftModel.getUnit() instanceof LengthUnit left && rightModel.getUnit() instanceof LengthUnit right) {
            return new Quantity<>(leftModel.getValue(), left).divide(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof WeightUnit left && rightModel.getUnit() instanceof WeightUnit right) {
            return new Quantity<>(leftModel.getValue(), left).divide(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof VolumeUnit left && rightModel.getUnit() instanceof VolumeUnit right) {
            return new Quantity<>(leftModel.getValue(), left).divide(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof TemperatureUnit left
                && rightModel.getUnit() instanceof TemperatureUnit right) {
            return new Quantity<>(leftModel.getValue(), left).divide(new Quantity<>(rightModel.getValue(), right));
        }
        throw new QuantityMeasurementException("Cannot divide quantities of different categories");
    }

    private QuantityDTO toDto(Quantity<?> quantity) {
        return QuantityDTO.from(quantity.getValue(), quantity.getUnit());
    }

    private boolean compareQuantities(QuantityModel<?> leftModel, QuantityModel<?> rightModel) {
        if (leftModel.getUnit() instanceof LengthUnit left && rightModel.getUnit() instanceof LengthUnit right) {
            return new Quantity<>(leftModel.getValue(), left).equals(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof WeightUnit left && rightModel.getUnit() instanceof WeightUnit right) {
            return new Quantity<>(leftModel.getValue(), left).equals(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof VolumeUnit left && rightModel.getUnit() instanceof VolumeUnit right) {
            return new Quantity<>(leftModel.getValue(), left).equals(new Quantity<>(rightModel.getValue(), right));
        }
        if (leftModel.getUnit() instanceof TemperatureUnit left
                && rightModel.getUnit() instanceof TemperatureUnit right) {
            return new Quantity<>(leftModel.getValue(), left).equals(new Quantity<>(rightModel.getValue(), right));
        }
        return false;
    }

    private QuantityMeasurementException mapException(OperationType operationType, RuntimeException exception) {
        if (exception instanceof QuantityMeasurementException quantityMeasurementException) {
            return quantityMeasurementException;
        }
        return new QuantityMeasurementException(
                operationType.name().toLowerCase() + " Error: " + exception.getMessage(),
                exception);
    }

    @FunctionalInterface
    private interface Operation {
        QuantityMeasurementEntity perform();
    }
}