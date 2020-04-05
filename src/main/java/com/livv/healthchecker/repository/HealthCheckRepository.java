package com.livv.healthchecker.repository;

import com.livv.healthchecker.model.HealthCheck;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface HealthCheckRepository extends ReactiveMongoRepository<HealthCheck, String> {
    Mono<HealthCheck> findAllByName(String name);
}
