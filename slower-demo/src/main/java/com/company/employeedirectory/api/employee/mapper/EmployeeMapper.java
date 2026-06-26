package com.company.employeedirectory.api.employee.mapper;

import com.company.employeedirectory.api.employee.dto.EmployeeResponse;
import com.company.employeedirectory.domain.employee.Employee;

public final class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhone(),
                e.getDepartment(),
                e.getJobTitle(),
                e.getManagerId(),
                e.getStartDate(),
                e.getStatus(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
