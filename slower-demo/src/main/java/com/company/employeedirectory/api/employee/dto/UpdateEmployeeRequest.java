package com.company.employeedirectory.api.employee.dto;

import com.company.employeedirectory.domain.employee.Department;
import com.company.employeedirectory.domain.employee.EmployeeStatus;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateEmployeeRequest(
        String firstName,
        String lastName,
        @Email String email,
        String phone,
        Department department,
        String jobTitle,
        UUID managerId,
        LocalDate startDate,
        EmployeeStatus status
) {}
