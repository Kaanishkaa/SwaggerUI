package com.company.employeedirectory.domain.employee;

import com.company.employeedirectory.api.employee.dto.CreateEmployeeRequest;
import com.company.employeedirectory.api.employee.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EmployeeService {

    Employee create(CreateEmployeeRequest request);

    Employee findById(UUID id);

    Page<Employee> findAll(String name, Department department, EmployeeStatus status, Pageable pageable);

    Employee update(UUID id, CreateEmployeeRequest request);

    Employee patch(UUID id, UpdateEmployeeRequest request);

    void delete(UUID id);
}
