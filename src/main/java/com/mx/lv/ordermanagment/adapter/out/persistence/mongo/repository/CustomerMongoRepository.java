package com.mx.lv.ordermanagment.adapter.out.persistence.mongo.repository;

import com.mx.lv.ordermanagment.adapter.out.persistence.mongo.document.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerMongoRepository
        extends MongoRepository<CustomerDocument, String> {
}
