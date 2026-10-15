package com.mx.lv.ordermanagment.configuration.spring;


import com.mx.lv.ordermanagment.application.port.in.CreateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.DeleteCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetAllCustomersUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetCustomerByIdUseCase;
import com.mx.lv.ordermanagment.application.port.in.UpdateCustomerUseCase;
import com.mx.lv.ordermanagment.application.usecase.CreateCustomerService;
import com.mx.lv.ordermanagment.application.usecase.DeleteCustomerService;
import com.mx.lv.ordermanagment.application.usecase.GetAllCustomersService;
import com.mx.lv.ordermanagment.application.usecase.GetCustomerByIdService;
import com.mx.lv.ordermanagment.application.usecase.UpdateCustomerService;
import tools.jackson.databind.ObjectMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.CustomerWebMapper;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.CustomerMongoAdapter;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.mapper.CustomerMongoMapper;
import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.repository.CustomerMongoRepository;
import com.mx.lv.ordermanagment.application.port.out.CustomerPersistencePort;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfiguration {

    @Bean
    public CustomerMongoMapper customerMongoMapper() {
        return new CustomerMongoMapper();
    }

    @Bean
    public CustomerPersistencePort customerPersistencePort(CustomerMongoRepository customerMongoRepository,
            CustomerMongoMapper customerMongoMapper) {
        return new CustomerMongoAdapter(customerMongoRepository, customerMongoMapper);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(
            CustomerPersistencePort customerPersistencePort) {
        return new CreateCustomerService(customerPersistencePort);
    }

    @Bean
    public GetCustomerByIdUseCase getCustomerByIdUseCase(CustomerPersistencePort customerPersistencePort,
                                                         LoadOrdersPort loadOrdersPort) {
        return new GetCustomerByIdService(customerPersistencePort, loadOrdersPort);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(CustomerPersistencePort customerPersistencePort) {
        return new UpdateCustomerService(customerPersistencePort);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(CustomerPersistencePort customerPersistencePort) {
        return new DeleteCustomerService(customerPersistencePort);
    }

    @Bean
    public CustomerWebMapper customerWebMapper() {
        return new CustomerWebMapper();
    }

    @Bean
    public GetAllCustomersUseCase getAllCustomersUseCase(CustomerPersistencePort customerPersistencePort, LoadOrdersPort loadOrdersPort) {
        return new GetAllCustomersService(customerPersistencePort, loadOrdersPort);
    }
    @Bean
    public CustomerDataInitializer customerDataInitializer(
            CustomerPersistencePort customerPersistencePort, ObjectMapper objectMapper) {
        return new CustomerDataInitializer(customerPersistencePort, objectMapper);
    }
}
