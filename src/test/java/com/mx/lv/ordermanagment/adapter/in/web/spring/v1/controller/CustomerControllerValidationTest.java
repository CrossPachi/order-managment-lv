package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.controller;

import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.mapper.CustomerWebMapper;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.PatchCustomerRequest;
import com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request.UpdateCustomerRequest;
import com.mx.lv.ordermanagment.application.port.in.CreateCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.DeleteCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetAllCustomersUseCase;
import com.mx.lv.ordermanagment.application.port.in.GetCustomerByIdUseCase;
import com.mx.lv.ordermanagment.application.port.in.PatchCustomerUseCase;
import com.mx.lv.ordermanagment.application.port.in.UpdateCustomerUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CustomerControllerValidationTest {

    private static ValidatorFactory validatorFactory;
    private static ExecutableValidator executableValidator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        executableValidator = validatorFactory.getValidator().forExecutables();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsCustomerIdsAtTheMinimumAndMaximumAllowedLengths() throws Exception {
        CustomerController controller = controller();

        assertTrue(validatePathId(controller, "getCustomerById", "A".repeat(10)).isEmpty());
        assertTrue(validatePathId(controller, "getCustomerById", "A".repeat(100)).isEmpty());
        assertTrue(validatePathId(controller, "patchCustomer", "A".repeat(10)).isEmpty());
    }

    @Test
    void rejectsShortLongOrInvalidCustomerIdsForGetUpdateAndDelete() throws Exception {
        CustomerController controller = controller();
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10"
        );

        assertInvalidPathId(controller, "getCustomerById", new Object[]{"short"});
        assertInvalidPathId(controller, "getCustomerById", new Object[]{"A".repeat(101)});
        assertInvalidPathId(controller, "getCustomerById", new Object[]{"customer id"});
        assertInvalidPathId(controller, "updateCustomer", new Object[]{"short", request});
        assertInvalidPathId(controller, "patchCustomer", new Object[]{
                "short", new PatchCustomerRequest("Alex", null, null, null, null)
        });
        assertInvalidPathId(controller, "deleteCustomer", new Object[]{"short"});
    }

    private Set<ConstraintViolation<CustomerController>> validatePathId(
            CustomerController controller,
            String methodName,
            Object customerId
    ) throws Exception {
        Object[] arguments = methodName.equals("updateCustomer")
                ? new Object[]{customerId, new UpdateCustomerRequest(
                        "Alex", "Lopez", "Diaz", "alex@example.com", "Main Street 10"
                )}
                : methodName.equals("patchCustomer")
                        ? new Object[]{customerId, new PatchCustomerRequest("Alex", null, null, null, null)}
                : new Object[]{customerId};
        Method method = findMethod(methodName);

        return executableValidator.validateParameters(controller, method, arguments);
    }

    private void assertInvalidPathId(CustomerController controller, String methodName, Object[] arguments)
            throws Exception {
        Method method = findMethod(methodName);
        Set<ConstraintViolation<CustomerController>> violations =
                executableValidator.validateParameters(controller, method, arguments);

        assertEquals(1, violations.size());
        assertEquals("CustomerId invalid format", violations.iterator().next().getMessage());
    }

    private Method findMethod(String methodName) throws NoSuchMethodException {
        if (methodName.equals("getCustomerById")) {
            return CustomerController.class.getMethod(methodName, String.class);
        }
        if (methodName.equals("deleteCustomer")) {
            return CustomerController.class.getMethod(methodName, String.class);
        }
        if (methodName.equals("patchCustomer")) {
            return CustomerController.class.getMethod(methodName, String.class, PatchCustomerRequest.class);
        }
        return CustomerController.class.getMethod(methodName, String.class, UpdateCustomerRequest.class);
    }

    private CustomerController controller() {
        return new CustomerController(
                mock(CreateCustomerUseCase.class),
                mock(GetCustomerByIdUseCase.class),
                mock(UpdateCustomerUseCase.class),
                mock(DeleteCustomerUseCase.class),
                mock(GetAllCustomersUseCase.class),
                mock(PatchCustomerUseCase.class),
                new CustomerWebMapper()
        );
    }
}
