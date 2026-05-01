package com.learning.dbscaling.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer implements Serializable {

    @Id
    private String id;
    private String name;
    @Indexed(unique = true, name = "idx_customer_email")
    private String email;
    private String phone;
    private String address;
    private String tier;
    private Instant createdAt;
}