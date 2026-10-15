package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsAValidCreateRequestWithAccentedNamesAndAnOptionalId() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                null, "José María", "López", "Díaz", "jose@example.com", "Calle Central 123"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsMissingAndTooLongIdsInCreateRequests() {
        CreateCustomerRequest missingId = createRequest(null);
        CreateCustomerRequest tooLongId = createRequest("x".repeat(51));

        assertTrue(validator.validate(missingId).isEmpty());
        assertInvalidFields(tooLongId, "userId");
    }

    @Test
    void rejectsBlankShortOrNonLetterNamesInCreateRequests() {
        assertInvalidFields(createRequestWithName(" "), "name");
        assertInvalidFields(createRequestWithName("A"), "name");
        assertInvalidFields(createRequestWithName("Alex1"), "name");
        assertInvalidFields(createRequestWithSurnames("L", "Diaz"), "fathersLastName");
        assertInvalidFields(createRequestWithSurnames("Lopez", "Diaz-1"), "mothersLastName");
    }

    @Test
    void rejectsInvalidEmailAndAddressInCreateRequests() {
        assertInvalidFields(createRequestWithEmail("not-an-email"), "email");
        assertInvalidFields(createRequestWithAddress("1234"), "address");
        assertInvalidFields(createRequestWithAddress("x".repeat(201)), "address");
    }

    @Test
    void acceptsValidUpdateRequests() {
        UpdateCustomerRequest request = updateRequest(
                "Ana María", "Pérez", "Gómez", "ana@example.com", "Avenida Reforma 100"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidNamesEmailAndAddressInUpdateRequests() {
        assertInvalidFields(updateRequest("Ana2", "Pérez", "Gómez", "ana@example.com", "Avenida Reforma"),
                "name");
        assertInvalidFields(updateRequest("Ana", "P", "Gómez", "ana@example.com", "Avenida Reforma"),
                "fathersLastName");
        assertInvalidFields(updateRequest("Ana", "Pérez", "", "ana@example.com", "Avenida Reforma"),
                "mothersLastName");
        assertInvalidFields(updateRequest("Ana", "Pérez", "Gómez2", "ana@example.com", "Avenida Reforma"),
                "mothersLastName");
        assertInvalidFields(updateRequest("Ana", "Pérez", "Gómez", "bad-email", "Avenida Reforma"),
                "email");
        assertInvalidFields(updateRequest("Ana", "Pérez", "Gómez", "ana@example.com", "1234"),
                "address");
    }

    @Test
    void rejectsFieldsThatExceedTheirMaximumLengths() {
        assertInvalidFields(createRequestWithName("A".repeat(51)), "name");
        assertInvalidFields(updateRequest("Ana", "B".repeat(51), "Gómez", "ana@example.com",
                "Avenida Reforma"), "fathersLastName");
        assertInvalidFields(updateRequest("Ana", "Pérez", "G".repeat(51), "ana@example.com",
                "Avenida Reforma"), "mothersLastName");
        assertInvalidFields(updateRequest("Ana", "Pérez", "Gómez", "a".repeat(46) + "@x.com",
                "Avenida Reforma"), "email");
    }

    @Test
    void acceptsPatchRequestsWithOnlyOneFieldProvided() {
        PatchCustomerRequest request = new PatchCustomerRequest("Ana María", null, null, null, null);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void acceptsMissingPatchFieldsButRejectsInvalidValuesThatAreProvided() {
        assertTrue(validator.validate(new PatchCustomerRequest(null, null, null, null, null)).isEmpty());
        assertInvalidFields(new PatchCustomerRequest(" ", null, null, null, null), "name");
        assertInvalidFields(new PatchCustomerRequest(null, null, null, " ", null), "email");
        assertInvalidFields(new PatchCustomerRequest(null, null, null, null, "   "), "address");
        assertInvalidFields(new PatchCustomerRequest(null, "A", null, null, null), "fathersLastName");
        assertInvalidFields(new PatchCustomerRequest(null, null, "Gomez2", null, null), "mothersLastName");
    }

    private void assertInvalidFields(Object request, String... expectedFields) {
        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertEquals(Set.of(expectedFields), invalidFields);
    }

    private CreateCustomerRequest createRequest(String userId) {
        return new CreateCustomerRequest(userId, "Alex", "Lopez", "Diaz", "alex@example.com",
                "Main Street 10");
    }

    private CreateCustomerRequest createRequestWithName(String name) {
        return new CreateCustomerRequest("user-123", name, "Lopez", "Diaz", "alex@example.com",
                "Main Street 10");
    }

    private CreateCustomerRequest createRequestWithEmail(String email) {
        return new CreateCustomerRequest("user-123", "Alex", "Lopez", "Diaz", email, "Main Street 10");
    }

    private CreateCustomerRequest createRequestWithSurnames(String fathersLastName, String mothersLastName) {
        return new CreateCustomerRequest("user-123", "Alex", fathersLastName, mothersLastName,
                "alex@example.com", "Main Street 10");
    }

    private CreateCustomerRequest createRequestWithAddress(String address) {
        return new CreateCustomerRequest("user-123", "Alex", "Lopez", "Diaz", "alex@example.com", address);
    }

    private UpdateCustomerRequest updateRequest(
            String name,
            String fathersLastName,
            String mothersLastName,
            String email,
            String address
    ) {
        return new UpdateCustomerRequest(name, fathersLastName, mothersLastName, email, address);
    }
}
