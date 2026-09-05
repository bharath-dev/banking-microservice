package com.banking.customer.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// 1. @Entity tells JPA/Hibernate this class maps to a database table
@Entity
// 2. @Table customizes the database table name to "customers"
@Table(name = "customers")
public class Customer {

    // 3. @Id marks this field as the primary key of the table
    // 4. @GeneratedValue(strategy = IDENTITY) instructs H2 to auto-increment the ID (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 5. @Column defines SQL column constraints: NOT NULL and max 50 characters
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    // 6. unique = true prevents duplicate email addresses in the database
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    // 7. National ID / SSN must also be unique across all bank customers
    @Column(nullable = false, unique = true, length = 20)
    private String nationalId;

    // Standard optional text columns
    private String address;
    private String city;

    // Account lifecycle state (e.g., ACTIVE, SUSPENDED, CLOSED)
    @Column(nullable = false)
    private String accountStatus;

    // BigDecimal avoids floating-point rounding errors in financial balances
    @Column(nullable = false)
    private BigDecimal accountBalance;

    // Audit timestamp: cannot be changed after customer is first created
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 8. @PrePersist automatically runs right before Hibernate saves a new customer to the DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.accountBalance == null) {
            this.accountBalance = BigDecimal.ZERO;
        }
    }

    // ===================================================================
    // GETTERS AND SETTERS (Used by Spring and Jackson JSON Serializer)
    // ===================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public BigDecimal getAccountBalance() { return accountBalance; }
    public void setAccountBalance(BigDecimal accountBalance) { this.accountBalance = accountBalance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}