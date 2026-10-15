package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatchCustomerRequest(
        @Size(
                min = 2,
                max = 50,
                message = "Name must contain between 2 and 50 characters"
        )
        @Pattern(
                regexp = "^(?=.*\\p{L})[\\p{L} ]+$",
                message = "Name must contain only letters and spaces"
        )
        String name,

        @Size(
                min = 2,
                max = 50,
                message = "Father's last name must contain between 2 and 50 characters"
        )
        @Pattern(
                regexp = "^(?=.*\\p{L})[\\p{L} ]+$",
                message = "Father's last name must contain only letters and spaces"
        )
        String fathersLastName,

        @Size(
                min = 2,
                max = 50,
                message = "Mother's last name must contain between 2 and 50 characters"
        )
        @Pattern(
                regexp = "^(?=.*\\p{L})[\\p{L} ]+$",
                message = "Mother's last name must contain only letters and spaces"
        )
        String mothersLastName,

        @Email(message = "Email format is invalid")
        @Size(
                max = 100,
                message = "Email must not exceed 100 characters"
        )
        @Pattern(
                regexp = "^(?!\\s*$).+",
                message = "Email must not be blank"
        )
        String email,

        @Size(
                min = 5,
                max = 200,
                message = "Address must contain between 5 and 200 characters"
        )
        @Pattern(
                regexp = ".*\\S.*",
                message = "Address must not be blank"
        )
        String address
) {
}
