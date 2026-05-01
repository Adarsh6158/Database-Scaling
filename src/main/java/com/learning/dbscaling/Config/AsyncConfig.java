package com.learning.dbscaling.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Processing Config (Kafka)

 * WHY ASYNC PROCESSING?

 * Some operations are too heavy to run in the HTTP request thread
 * Running these synchronously blocks the HTTP thread pool,
 * increasing response latency and reducing throughput.
 * -> So Offload heavy work to a message broker (Apache Kafka).

 */

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String ORDER_TOPIC = "order-processing";
    public static final String ORDER_CONSUMER_GROUP = "order-processing-group";

    /**
     * Auto-create the Kafka topic on startup.
     * Partitions: 3, allows up to 3 parallel consumers in the group.
     * Each partition is replicated across N brokers. If one broker dies,
     * another takes over as leader with zero data loss.
     */
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(ORDER_TOPIC)
                .partitions(3)
                .replicas(1) // set to 3 in production
                .build();
    }

    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-worker-");
        executor.initialize();
        return executor;
    }
}
