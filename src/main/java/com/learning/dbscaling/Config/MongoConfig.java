package com.learning.dbscaling.Config;

import com.learning.dbscaling.Monitoring.MongoCommandListener;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.event.CommandListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.concurrent.TimeUnit;

/**
 * MongoDB Config — Connection Pooling + Read/Write Separation

 * CONNECTION POOLING — WHY IT'S CRITICAL UNDER HIGH LOAD

 * Every MongoDB operation needs a TCP connection. Creating a new TCP
 * connection involves:
 * 1. TCP 3-way handshake (~1–3 ms on LAN, 50–200 ms across regions)
 * 2. TLS handshake if SSL is enabled (~additional 5–30 ms)
 * 3. MongoDB authentication (SCRAM-SHA-256, ~1 round-trip)

 * Without pooling, 1000 concurrent requests = 1000 new connections.
 * Each costs ~50–200 ms overhead = massive latency + OS file-descriptor exhaustion.

 * With pooling, connections are REUSED:
 * - Warm connections sit idle in the pool, ready instantly
 * - Thread borrows a connection, uses it, returns it
 * - Max pool size caps resource usage (e.g., 100 connections)
 * - Min pool size keeps connections warm (no cold-start penalty)


 * READ/WRITE SEPARATION
 * In a MongoDB Replica Set:
 * - PRIMARY handles all writes
 * - SECONDARY replicas receive oplog entries and replicate data
 */

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${app.mongodb.pool.min-size:10}")
    private int poolMinSize;

    @Value("${app.mongodb.pool.max-size:100}")
    private int poolMaxSize;

    @Value("${app.mongodb.pool.max-wait-time-ms:5000}")
    private long poolMaxWaitTimeMs;

    @Value("${app.mongodb.pool.max-idle-time-ms:60000}")
    private long poolMaxIdleTimeMs;

    /**
     Primary MongoTemplate used for writes and strong-consistency reads
     */
    @Bean
    public MongoTemplate mongoTemplate(CommandListener mongoCommandListener) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .applyToConnectionPoolSettings(pool -> configurePool(pool))
                .addCommandListener(mongoCommandListener)
                .build();

        MongoDatabaseFactory factory =
                new SimpleMongoClientDatabaseFactory(
                        com.mongodb.client.MongoClients.create(settings),
                        new ConnectionString(mongoUri).getDatabase());

        return new MongoTemplate(factory);
    }

    /**
     * Secondary MongoTemplate used for eventually-consistent reads.
     */
    @Bean("secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate(CommandListener mongoCommandListener) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .applyToConnectionPoolSettings(pool -> configurePool(pool))
                .addCommandListener(mongoCommandListener)
                // In production replica-set, uncomment:
                // .readPreference(com.mongodb.ReadPreference.secondaryPreferred())
                .build();

        MongoDatabaseFactory factory =
                new SimpleMongoClientDatabaseFactory(
                        com.mongodb.client.MongoClients.create(settings),
                        new ConnectionString(mongoUri).getDatabase());
        return new MongoTemplate(factory);
    }

    @Bean
    public CommandListener mongoCommandListener() {
        return new MongoCommandListener();
    }

    /**
     * Shared pool configuration applied to both primary and secondary clients.
     */
    private void configurePool(ConnectionPoolSettings.Builder pool) {
        pool.minSize(poolMinSize)
                .maxSize(poolMaxSize)
                .maxWaitTime(poolMaxWaitTimeMs, TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(poolMaxIdleTimeMs, TimeUnit.MILLISECONDS);
    }
}
