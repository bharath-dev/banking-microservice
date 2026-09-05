package com.banking.customer.service.impl;

import com.banking.customer.dto.CustomerDTO;
import com.banking.customer.entity.Customer;
import com.banking.customer.exception.ResourceNotFoundException;
import com.banking.customer.repository.CustomerRepository;
import com.banking.customer.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 1. @Service marks this class as a Spring-managed Service component containing business logic.
 * 2. Spring automatically injects CustomerRepository via the constructor below.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // Constructor Injection: Recommended in Spring Boot (no need for @Autowired on single constructor)
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        String cleanEmail = dto.getEmail() != null ? dto.getEmail().trim() : "";
        String cleanNationalId = dto.getNationalId() != null ? dto.getNationalId().trim() : "";

        // Business Rule 1: Check if email is already registered in DB (case-insensitive)
        if(customerRepository.findByEmailIgnoreCase(cleanEmail).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + cleanEmail);
        }
        // Business Rule 2: Check if national ID / SSN is already registered
        if(customerRepository.findByNationalId(cleanNationalId).isPresent()) {
            throw new IllegalArgumentException("National ID already exists: " + cleanNationalId);
        }

        // Convert incoming DTO -> DB Entity, save to H2, then convert saved Entity -> DTO
        Customer customer = mapToEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        // findById() returns Optional<Customer>. If empty, .orElseThrow() fires our custom 404 exception!
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        /**
         * =========================================================================
         * HOW THIS STREAM PIPELINE WORKS:
         * 1. customerRepository.findAll() -> Returns List<Customer> from H2 database
         * 2. .stream()                   -> Opens a conveyor belt / pipeline of items
         * 3. .map(this::mapToDTO)        -> For every Customer, calls mapToDTO(customer)
         *                                   (shorthand for: customer -> this.mapToDTO(customer))
         * 4. .collect(Collectors.toList())-> Gathers all CustomerDTOs back into a List
         * =========================================================================
         *
         * NOTE: You can also write this with a standard for-loop! Both do the exact same thing:
         *
         * List<Customer> customers = customerRepository.findAll();
         * List<CustomerDTO> dtoList = new ArrayList<>();
         * for (Customer customer : customers) {
         *     CustomerDTO dto = mapToDTO(customer);
         *     dtoList.add(dto);
         * }
         * return dtoList;
         */
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> searchCustomersByCity(String city) {
        // Same streaming pattern: transforms list of Customer entities from city query into CustomerDTOs
        return customerRepository.findByCityIgnoreCase(city).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Update editable fields
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setAccountStatus(dto.getAccountStatus());
        existing.setAccountBalance(dto.getAccountBalance());

        // Save updated entity and return as DTO
        Customer updated = customerRepository.save(existing);
        return mapToDTO(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(existing);
    }

    // =========================================================================
    // HELPER MAPPERS: Convert between Database Entity <---> API DTO
    // =========================================================================

    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setNationalId(dto.getNationalId());
        customer.setAddress(dto.getAddress());
        customer.setCity(dto.getCity());
        customer.setAccountStatus(dto.getAccountStatus());
        customer.setAccountBalance(dto.getAccountBalance());
        return customer;
    }

    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setNationalId(customer.getNationalId());
        dto.setAddress(customer.getAddress());
        dto.setCity(customer.getCity());
        dto.setAccountStatus(customer.getAccountStatus());
        dto.setAccountBalance(customer.getAccountBalance());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}