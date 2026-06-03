package com.quantitymeasurement.controller;

import com.quantitymeasurement.model.*;
import com.quantitymeasurement.service.IQuantityMeasurementService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private final IQuantityMeasurementService quantityMeasurementService;

    public QuantityMeasurementController(IQuantityMeasurementService quantityMeasurementService) {
        this.quantityMeasurementService = quantityMeasurementService;
    }

    private String getUserId(String email) {
        return (email != null && !email.isBlank()) ? email : "anonymous";
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody QuantityInputDTO input) {

        QuantityMeasurementDTO result = quantityMeasurementService.compare(
                input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to another unit")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody QuantityInputDTO input) {

        QuantityMeasurementDTO result = quantityMeasurementService.convert(
                input.getThisQuantityDTO(), input.getThatQuantityDTO().getUnitName(), userEmail);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody QuantityInputDTO input) {

        QuantityMeasurementDTO result = quantityMeasurementService.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), input.getThatQuantityDTO().getUnitName(), userEmail);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody QuantityInputDTO input) {

        QuantityMeasurementDTO result = quantityMeasurementService.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), input.getThatQuantityDTO().getUnitName(), userEmail);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @Valid @RequestBody QuantityInputDTO input) {

        QuantityMeasurementDTO result = quantityMeasurementService.divide(
                input.getThisQuantityDTO(), input.getThatQuantityDTO(), userEmail);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/history")
    @Operation(summary = "Get current user's measurement history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getMyHistory(
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {

        if (userEmail == null || userEmail.isBlank()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(quantityMeasurementService.getUserHistory(userEmail));
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get quantity measurement history by operation")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(@PathVariable String operation) {
        return ResponseEntity.ok(quantityMeasurementService.getOperationHistory(OperationType.from(operation)));
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get quantity measurement history by measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getMeasurementHistory(@PathVariable String measurementType) {
        return ResponseEntity.ok(quantityMeasurementService.getMeasurementHistory(measurementType));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get errored quantity measurement history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErroredHistory() {
        return ResponseEntity.ok(quantityMeasurementService.getErroredHistory());
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get successful operation count")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        return ResponseEntity.ok(quantityMeasurementService.getOperationCount(OperationType.from(operation)));
    }
}
