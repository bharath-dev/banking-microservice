package com.banking.customer.controller;

import com.banking.customer.dto.CustomerDTO;
import com.banking.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 1. @RestController = @Controller + @ResponseBody (auto-converts return values to JSON)
 * 2. @RequestMapping specifies base URL prefix: http://localhost:8080/api/customers
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    // Constructor Injection of our Service layer
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // POST /api/customers - Creates a new customer
    // @Valid validates DTO annotations (@NotBlank, @Email); @RequestBody parses incoming JSON
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO created = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED); // HTTP 201 Created
    }

    // GET /api/customers or GET /api/customers?city=London
    // @RequestParam(required = false) allows optional filtering by city
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(
            @RequestParam(required = false) String city) {
        List<CustomerDTO> customers;
        if (city != null && !city.isEmpty()) {
            customers = customerService.searchCustomersByCity(city);
        } else {
            customers = customerService.getAllCustomers();
        }
        return ResponseEntity.ok(customers); // HTTP 200 OK
    }

    // GET /api/customers/{id} - Fetches single customer by primary key
    // @PathVariable Long id extracts the number from the URL path
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer); // HTTP 200 OK
    }

    // PUT /api/customers/{id} - Updates customer details
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updated = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updated); // HTTP 200 OK
    }

    // DELETE /api/customers/{id} - Removes customer from database
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}