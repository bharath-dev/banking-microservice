package com.banking.customer.repository;

import com.banking.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 1. @Repository indicates that this interface is a Spring Data repository bean.
 * 2. By extending JpaRepository<Customer, Long>:
 *    - Customer: The entity type this repository manages.
 *    - Long: The data type of the entity's Primary Key (@Id).
 *    Spring automatically generates methods: save(), findById(), findAll(), deleteById(), etc.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * DERIVED QUERY: You declare the method, Spring writes the SQL behind the scenes!
     * SQL generated: SELECT * FROM customers WHERE LOWER(email) = LOWER(?)
     * Case-insensitive to prevent duplicate emails like test@bank.com vs Test@bank.com
     */
    Optional<Customer> findByEmailIgnoreCase(String email);

    /**
     * SQL generated: SELECT * FROM customers WHERE national_id = ?
     */
    Optional<Customer> findByNationalId(String nationalId);

    /**
     * SQL generated: SELECT * FROM customers WHERE LOWER(city) = LOWER(?)
     * Returns a List because multiple customers can live in the same city.
     */
    List<Customer> findByCityIgnoreCase(String city);
}