package com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
public record CustomerDocument(
        @Id String customerId,
        String name,
        String fathersLastName,
        String mothersLastName,
        String email,
        String address
) {
}
