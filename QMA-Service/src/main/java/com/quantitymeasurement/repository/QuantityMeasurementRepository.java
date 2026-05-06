package com.quantitymeasurement.repository;

import com.quantitymeasurement.model.OperationType;
import com.quantitymeasurement.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {
    List<QuantityMeasurementEntity> findByOperationTypeOrderByCreatedAtAsc(OperationType operationType);

    List<QuantityMeasurementEntity> findByLeftMeasurementTypeOrderByCreatedAtAsc(String measurementType);

    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime createdAt);

    @Query("select q from QuantityMeasurementEntity q where q.operationType = :operationType and q.error = false order by q.createdAt asc")
    List<QuantityMeasurementEntity> findSuccessfulByOperationType(OperationType operationType);

    long countByOperationTypeAndErrorFalse(OperationType operationType);

    List<QuantityMeasurementEntity> findByErrorTrueOrderByCreatedAtAsc();

    @Query("select q from QuantityMeasurementEntity q where q.user.email = :email order by q.createdAt desc")
    List<QuantityMeasurementEntity> findByUserEmail(String email);
}