package com.company.employeedirectory.api.department;

import com.company.employeedirectory.domain.employee.Department;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    @GetMapping
    public List<String> listDepartments() {
        return Arrays.stream(Department.values())
                     .map(Enum::name)
                     .toList();
    }
}
