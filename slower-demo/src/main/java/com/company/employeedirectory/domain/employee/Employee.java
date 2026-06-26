package com.company.employeedirectory.domain.employee;

import com.company.employeedirectory.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Department department;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    protected Employee() {}

    public Employee(String firstName, String lastName, String email, String phone,
                    Department department, String jobTitle, UUID managerId, LocalDate startDate) {
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.email      = email;
        this.phone      = phone;
        this.department = department;
        this.jobTitle   = jobTitle;
        this.managerId  = managerId;
        this.startDate  = startDate;
    }

    // --- Getters ---

    public String getFirstName()       { return firstName; }
    public String getLastName()        { return lastName; }
    public String getEmail()           { return email; }
    public String getPhone()           { return phone; }
    public Department getDepartment()  { return department; }
    public String getJobTitle()        { return jobTitle; }
    public UUID getManagerId()         { return managerId; }
    public LocalDate getStartDate()    { return startDate; }
    public EmployeeStatus getStatus()  { return status; }

    // --- Setters (used by service layer for updates) ---

    public void setFirstName(String firstName)       { this.firstName = firstName; }
    public void setLastName(String lastName)         { this.lastName = lastName; }
    public void setEmail(String email)               { this.email = email; }
    public void setPhone(String phone)               { this.phone = phone; }
    public void setDepartment(Department department) { this.department = department; }
    public void setJobTitle(String jobTitle)         { this.jobTitle = jobTitle; }
    public void setManagerId(UUID managerId)         { this.managerId = managerId; }
    public void setStartDate(LocalDate startDate)    { this.startDate = startDate; }
    public void setStatus(EmployeeStatus status)     { this.status = status; }
}
