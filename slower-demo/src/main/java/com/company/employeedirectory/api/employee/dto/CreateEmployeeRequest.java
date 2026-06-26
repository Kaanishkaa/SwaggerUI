package com.company.employeedirectory.api.employee.dto;

import com.company.employeedirectory.domain.employee.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String phone,
        @NotNull Department department,
        @NotBlank String jobTitle,
        UUID managerId,
        @NotNull LocalDate startDate
) {}
