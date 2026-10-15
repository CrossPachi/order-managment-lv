package com.mx.lv.ordermanagment.adapter.in.web.spring.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(

        @Size(max = 50, message = "UserId is too long")
        String userId,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must contain between 2 and 50 characters")
        @Pattern(
                regexp = "^[\\p{L} ]+$",
                message = "Name must contain only letters and spaces"
        )
        String name,

        @NotBlank(message = "Father's last name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Father's last name must contain between 2 and 50 characters"
        )
        @Pattern(
                regexp = "^[\\p{L} ]+$",
                message = "Father's last name must contain only letters and spaces"
        )
        String fathersLastName,

        @NotBlank(message = "Mother's last name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Mother's last name must contain between 2 and 50 characters"
        )
        @Pattern(
                regexp = "^[\\p{L} ]+$",
                message = "Mother's last name must contain only letters and spaces"
        )
        String mothersLastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 50, message = "Email must not exceed 100 characters")
        String email,

        @NotBlank(message = "Address is required")
        @Size(
                min = 5,
                max = 200,
                message = "Address must contain between 5 and 200 characters"
        )
        String address
) {
}
