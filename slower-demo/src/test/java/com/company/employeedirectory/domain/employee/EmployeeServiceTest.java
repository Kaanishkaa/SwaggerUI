package com.company.employeedirectory.domain.employee;

import com.company.employeedirectory.api.employee.dto.CreateEmployeeRequest;
import com.company.employeedirectory.api.employee.dto.UpdateEmployeeRequest;
import com.company.employeedirectory.exception.DuplicateEmailException;
import com.company.employeedirectory.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeServiceImpl service;

    private UUID employeeId;
    private Employee existingEmployee;
    private CreateEmployeeRequest createRequest;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        existingEmployee = new Employee(
                "Jane", "Doe", "jane@company.com", "555-0100",
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));

        createRequest = new CreateEmployeeRequest(
                "Jane", "Doe", "jane@company.com", "555-0100",
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));
    }

    // --- create ---

    @Test
    void createSavesAndReturnsEmployee() {
        when(repository.existsByEmail("jane@company.com")).thenReturn(false);
        when(repository.save(any(Employee.class))).thenReturn(existingEmployee);

        Employee result = service.create(createRequest);

        assertThat(result.getEmail()).isEqualTo("jane@company.com");
        verify(repository).save(any(Employee.class));
    }

    @Test
    void createThrowsDuplicateEmailWhenEmailExists() {
        when(repository.existsByEmail("jane@company.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(createRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("jane@company.com");
    }

    // --- findById ---

    @Test
    void findByIdReturnsEmployee() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));

        Employee result = service.findById(employeeId);

        assertThat(result.getEmail()).isEqualTo("jane@company.com");
    }

    @Test
    void findByIdThrowsNotFoundForUnknownId() {
        when(repository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(employeeId.toString());
    }

    // --- update ---

    @Test
    void updateReplacesAllFields() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.existsByEmailAndIdNot("jane@company.com", employeeId)).thenReturn(false);
        when(repository.save(any(Employee.class))).thenReturn(existingEmployee);

        CreateEmployeeRequest updatedRequest = new CreateEmployeeRequest(
                "Janet", "Smith", "jane@company.com", "555-9999",
                Department.PRODUCT, "Senior Engineer", null, LocalDate.of(2024, 6, 1));

        Employee result = service.update(employeeId, updatedRequest);

        assertThat(result).isNotNull();
        verify(repository).save(existingEmployee);
    }

    @Test
    void updateThrowsDuplicateEmailWhenEmailTakenByAnotherEmployee() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.existsByEmailAndIdNot("taken@company.com", employeeId)).thenReturn(true);

        CreateEmployeeRequest conflictRequest = new CreateEmployeeRequest(
                "Jane", "Doe", "taken@company.com", null,
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));

        assertThatThrownBy(() -> service.update(employeeId, conflictRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("taken@company.com");
    }

    // --- patch ---

    @Test
    void patchAppliesOnlyNonNullFields() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.save(any(Employee.class))).thenReturn(existingEmployee);

        UpdateEmployeeRequest patch = new UpdateEmployeeRequest(
                "Janet", null, null, null, null, null, null, null, null);

        service.patch(employeeId, patch);

        assertThat(existingEmployee.getFirstName()).isEqualTo("Janet");
        assertThat(existingEmployee.getLastName()).isEqualTo("Doe");   // unchanged
    }

    @Test
    void patchUpdatesStatus() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.save(any(Employee.class))).thenReturn(existingEmployee);

        UpdateEmployeeRequest patch = new UpdateEmployeeRequest(
                null, null, null, null, null, null, null, null, EmployeeStatus.INACTIVE);

        service.patch(employeeId, patch);

        assertThat(existingEmployee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    // --- delete ---

    @Test
    void deleteSetsStatusToInactive() {
        when(repository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(repository.save(any(Employee.class))).thenReturn(existingEmployee);

        service.delete(employeeId);

        assertThat(existingEmployee.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(repository).save(existingEmployee);
    }

    @Test
    void deleteThrowsNotFoundForUnknownId() {
        when(repository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    // --- findAll ---

    @Test
    @SuppressWarnings("unchecked")
    void findAllReturnsPaginatedResults() {
        Page<Employee> page = new PageImpl<>(List.of(existingEmployee));
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<Employee> result = service.findAll(null, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
    }
}
