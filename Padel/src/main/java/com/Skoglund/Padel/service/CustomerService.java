package com.Skoglund.Padel.service;

import com.Skoglund.Padel.dto.AddressDTO;
import com.Skoglund.Padel.dto.CustomerDTO;
import com.Skoglund.Padel.entity.Address;
import com.Skoglund.Padel.entity.Customer;
import com.Skoglund.Padel.repository.AddressRepository;
import com.Skoglund.Padel.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        Customer saved = customerRepository.save(customer);
        logger.info("admin created customer {} (id={})", saved.getUsername(), saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        customer.setUsername(dto.getUsername());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        Customer saved = customerRepository.save(customer);
        logger.info("admin updated customer {} (id={})", saved.getUsername(), saved.getId());
        return toDTO(saved);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
        logger.info("admin deleted customer {} (id={})", customer.getUsername(), id);
    }

    @Transactional
    public AddressDTO addAddress(Long customerId, AddressDTO dto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setCustomer(customer);
        Address saved = addressRepository.save(address);
        logger.info("admin added address (id={}) to customer {} (id={})", saved.getId(), customer.getUsername(), customerId);
        return toAddressDTO(saved);
    }

    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
        logger.info("admin deleted address (id={}) from customer (id={})", addressId, customerId);
    }

    public CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setAddresses(customer.getAddresses().stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private AddressDTO toAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        return dto;
    }
}