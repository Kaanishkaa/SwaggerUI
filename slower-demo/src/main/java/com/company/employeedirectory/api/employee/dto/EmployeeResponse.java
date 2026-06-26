package com.company.employeedirectory.api.employee.dto;

import com.company.employeedirectory.domain.employee.Department;
import com.company.employeedirectory.domain.employee.EmployeeStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Department department,
        String jobTitle,
        UUID managerId,
        LocalDate startDate,
        EmployeeStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
