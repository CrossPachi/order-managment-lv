package com.mx.lv.ordermanagment.configuration.spring;

import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.domain.model.Customer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

public class CustomerDataInitializer implements ApplicationRunner {

    private static final String CUSTOMER_SEED_FILE = "initialdata/customer.json";

    private final CustomerPersistencePort customerPersistencePort;
    private final ObjectMapper objectMapper;

    public CustomerDataInitializer(
            CustomerPersistencePort customerPersistencePort,
            ObjectMapper objectMapper
    ) {
        this.customerPersistencePort = customerPersistencePort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (customerPersistencePort.count() > 0) {
            return;
        }
        List<Customer> customers = loadCustomers();
        saveCustomers(customers);
    }

    private List<Customer> loadCustomers() throws Exception {
        ClassPathResource resource = new ClassPathResource(CUSTOMER_SEED_FILE);

        try (InputStream inputStream = resource.getInputStream()) {
            TypeReference<List<Customer>> customerListType = new TypeReference<>() {
            };
            return objectMapper.readValue(
                    inputStream,
                    customerListType
            );
        }
    }

    private void saveCustomers(List<Customer> customers) {
        customers.forEach(customer -> customerPersistencePort.save(customer));
    }
}
