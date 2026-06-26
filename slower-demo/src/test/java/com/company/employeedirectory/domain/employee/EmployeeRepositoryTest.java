package com.company.employeedirectory.domain.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void savesAndRetrievesEmployee() {
        Employee saved = repository.save(newEmployee("jane.doe@company.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Employee found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getEmail()).isEqualTo("jane.doe@company.com");
        assertThat(found.getFirstName()).isEqualTo("Jane");
        assertThat(found.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        assertThat(repository.findById(java.util.UUID.randomUUID())).isEmpty();
    }

    @Test
    void existsByEmailReturnsTrueForExistingEmail() {
        repository.save(newEmployee("existing@company.com"));

        assertThat(repository.existsByEmail("existing@company.com")).isTrue();
        assertThat(repository.existsByEmail("other@company.com")).isFalse();
    }

    @Test
    void duplicateEmailViolatesUniqueConstraint() {
        repository.save(newEmployee("dup@company.com"));
        repository.flush();

        assertThatThrownBy(() -> {
            repository.save(newEmployee("dup@company.com"));
            repository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void defaultStatusIsActive() {
        Employee saved = repository.save(newEmployee("active@company.com"));
        assertThat(saved.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void softDeleteSetsStatusToInactive() {
        Employee employee = repository.save(newEmployee("softdelete@company.com"));
        employee.setStatus(EmployeeStatus.INACTIVE);
        Employee updated = repository.save(employee);

        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        assertThat(repository.findById(updated.getId())).isPresent();
    }

    private Employee newEmployee(String email) {
        return new Employee(
                "Jane",
                "Doe",
                email,
                "555-0100",
                Department.ENGINEERING,
                "Software Engineer",
                null,
                LocalDate.of(2023, 1, 15)
        );
    }
}
