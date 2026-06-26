package com.company.employeedirectory.domain.employee;

import com.company.employeedirectory.api.employee.dto.CreateEmployeeRequest;
import com.company.employeedirectory.api.employee.dto.UpdateEmployeeRequest;
import com.company.employeedirectory.exception.DuplicateEmailException;
import com.company.employeedirectory.exception.EmployeeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Employee create(CreateEmployeeRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        Employee employee = new Employee(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.department(),
                request.jobTitle(),
                request.managerId(),
                request.startDate()
        );
        return repository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> findAll(String name, Department department, EmployeeStatus status, Pageable pageable) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecification.nameContains(name))
                .and(EmployeeSpecification.hasDepartment(department))
                .and(EmployeeSpecification.hasStatus(status));
        return repository.findAll(spec, pageable);
    }

    @Override
    public Employee update(UUID id, CreateEmployeeRequest request) {
        Employee employee = findById(id);
        if (repository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException(request.email());
        }
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setDepartment(request.department());
        employee.setJobTitle(request.jobTitle());
        employee.setManagerId(request.managerId());
        employee.setStartDate(request.startDate());
        return repository.save(employee);
    }

    @Override
    public Employee patch(UUID id, UpdateEmployeeRequest request) {
        Employee employee = findById(id);
        if (request.email() != null && repository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException(request.email());
        }
        if (request.firstName()  != null) employee.setFirstName(request.firstName());
        if (request.lastName()   != null) employee.setLastName(request.lastName());
        if (request.email()      != null) employee.setEmail(request.email());
        if (request.phone()      != null) employee.setPhone(request.phone());
        if (request.department() != null) employee.setDepartment(request.department());
        if (request.jobTitle()   != null) employee.setJobTitle(request.jobTitle());
        if (request.managerId()  != null) employee.setManagerId(request.managerId());
        if (request.startDate()  != null) employee.setStartDate(request.startDate());
        if (request.status()     != null) employee.setStatus(request.status());
        return repository.save(employee);
    }

    @Override
    public void delete(UUID id) {
        Employee employee = findById(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        repository.save(employee);
    }
}
