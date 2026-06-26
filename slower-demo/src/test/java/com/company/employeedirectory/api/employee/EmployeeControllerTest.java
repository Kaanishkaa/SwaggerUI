package com.company.employeedirectory.api.employee;

import com.company.employeedirectory.api.employee.dto.CreateEmployeeRequest;
import com.company.employeedirectory.api.employee.dto.UpdateEmployeeRequest;
import com.company.employeedirectory.domain.employee.Department;
import com.company.employeedirectory.domain.employee.Employee;
import com.company.employeedirectory.domain.employee.EmployeeService;
import com.company.employeedirectory.domain.employee.EmployeeStatus;
import com.company.employeedirectory.exception.DuplicateEmailException;
import com.company.employeedirectory.exception.EmployeeNotFoundException;
import com.company.employeedirectory.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService service;

    private UUID employeeId;
    private Employee sampleEmployee;
    private CreateEmployeeRequest createRequest;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        sampleEmployee = buildEmployee(employeeId, "jane@company.com");
        createRequest = new CreateEmployeeRequest(
                "Jane", "Doe", "jane@company.com", "555-0100",
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));
    }

    // --- GET /api/v1/employees ---

    @Test
    void listReturnsPageOfEmployees() throws Exception {
        when(service.findAll(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleEmployee)));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("jane@company.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // --- GET /api/v1/employees/{id} ---

    @Test
    void getByIdReturnsEmployee() throws Exception {
        when(service.findById(employeeId)).thenReturn(sampleEmployee);

        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@company.com"))
                .andExpect(jsonPath("$.department").value("ENGINEERING"));
    }

    @Test
    void getByIdReturns404WhenNotFound() throws Exception {
        when(service.findById(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Employee Not Found"));
    }

    // --- POST /api/v1/employees ---

    @Test
    void createReturns201WithBody() throws Exception {
        when(service.create(any(CreateEmployeeRequest.class))).thenReturn(sampleEmployee);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane@company.com"));
    }

    @Test
    void createReturns400OnMissingRequiredFields() throws Exception {
        String invalidBody = "{}";

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.violations").isMap());
    }

    @Test
    void createReturns409OnDuplicateEmail() throws Exception {
        when(service.create(any(CreateEmployeeRequest.class)))
                .thenThrow(new DuplicateEmailException("jane@company.com"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate Email"));
    }

    @Test
    void createReturns400OnInvalidEmail() throws Exception {
        CreateEmployeeRequest badEmail = new CreateEmployeeRequest(
                "Jane", "Doe", "not-an-email", null,
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations.email").exists());
    }

    // --- PUT /api/v1/employees/{id} ---

    @Test
    void updateReturnsUpdatedEmployee() throws Exception {
        when(service.update(eq(employeeId), any(CreateEmployeeRequest.class))).thenReturn(sampleEmployee);

        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@company.com"));
    }

    @Test
    void updateReturns404WhenEmployeeNotFound() throws Exception {
        when(service.update(eq(employeeId), any(CreateEmployeeRequest.class)))
                .thenThrow(new EmployeeNotFoundException(employeeId));

        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isNotFound());
    }

    // --- PATCH /api/v1/employees/{id} ---

    @Test
    void patchAppliesPartialUpdate() throws Exception {
        when(service.patch(eq(employeeId), any(UpdateEmployeeRequest.class))).thenReturn(sampleEmployee);

        String patchBody = """
                { "firstName": "Janet" }
                """;

        mockMvc.perform(patch("/api/v1/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@company.com"));
    }

    // --- DELETE /api/v1/employees/{id} ---

    @Test
    void deleteReturns204() throws Exception {
        doNothing().when(service).delete(employeeId);

        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenEmployeeNotFound() throws Exception {
        doThrow(new EmployeeNotFoundException(employeeId)).when(service).delete(employeeId);

        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNotFound());
    }

    // --- helper ---

    private Employee buildEmployee(UUID id, String email) {
        Employee e = new Employee(
                "Jane", "Doe", email, "555-0100",
                Department.ENGINEERING, "Engineer", null, LocalDate.of(2023, 1, 1));
        // reflectively set id and timestamps for test realism
        try {
            var idField = com.company.employeedirectory.domain.common.BaseEntity.class
                    .getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(e, id);

            var createdField = com.company.employeedirectory.domain.common.BaseEntity.class
                    .getDeclaredField("createdAt");
            createdField.setAccessible(true);
            createdField.set(e, Instant.now());

            var updatedField = com.company.employeedirectory.domain.common.BaseEntity.class
                    .getDeclaredField("updatedAt");
            updatedField.setAccessible(true);
            updatedField.set(e, Instant.now());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return e;
    }
}
