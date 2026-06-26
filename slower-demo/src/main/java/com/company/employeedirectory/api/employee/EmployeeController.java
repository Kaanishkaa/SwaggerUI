package com.company.employeedirectory.api.employee;

import com.company.employeedirectory.api.employee.dto.CreateEmployeeRequest;
import com.company.employeedirectory.api.employee.dto.EmployeeResponse;
import com.company.employeedirectory.api.employee.dto.UpdateEmployeeRequest;
import com.company.employeedirectory.api.employee.mapper.EmployeeMapper;
import com.company.employeedirectory.domain.employee.Department;
import com.company.employeedirectory.domain.employee.EmployeeService;
import com.company.employeedirectory.domain.employee.EmployeeStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) EmployeeStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        return service.findAll(name, department, status, pageable)
                      .map(EmployeeMapper::toResponse);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable UUID id) {
        return EmployeeMapper.toResponse(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        return EmployeeMapper.toResponse(service.create(request));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateEmployeeRequest request) {
        return EmployeeMapper.toResponse(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public EmployeeResponse patch(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return EmployeeMapper.toResponse(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
