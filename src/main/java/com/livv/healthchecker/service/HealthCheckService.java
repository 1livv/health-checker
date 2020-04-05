package com.livv.healthchecker.service;

import com.livv.healthchecker.exception.HealthCheckExecutionException;
import com.livv.healthchecker.exception.NotUpToDateHealthCheckException;
import com.livv.healthchecker.exception.UnhealthyHealthCheckException;
import com.livv.healthchecker.model.HealthCheck;
import com.livv.healthchecker.model.HealthCheckState;
import com.livv.healthchecker.model.Response;
import com.livv.healthchecker.repository.HealthCheckRepository;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HealthCheckService {

    private final HealthCheckEvaluator healthCheckEvaluator;
    private final WebClient webClient;
    private final HealthCheckRepository healthCheckRepository;


    public HealthCheckService(HealthCheckEvaluator healthCheckEvaluator,
                              WebClient webClient,
                              HealthCheckRepository healthCheckRepository) {
        this.healthCheckEvaluator = healthCheckEvaluator;
        this.webClient = webClient;
        this.healthCheckRepository = healthCheckRepository;
    }

    public Mono<HealthCheck> createHealthCheck(HealthCheck healthCheck) {
        return check(healthCheck)
                .onErrorMap(HealthCheckExecutionException.class,
                            e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                             e.getMessage()))
                .then(healthCheckRepository.save(healthCheck))
                .doOnSuccess(this::start);
    }

    public Mono<HealthCheck> updateHealthCheck(String id, HealthCheck updatedHealthCheck) {
        return check(updatedHealthCheck)
                .onErrorMap(HealthCheckExecutionException.class,
                            e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                             e.getMessage()))
                .then(getHealthCheckById(id)
                              .flatMap(found -> {
                                  found.setThreshold(updatedHealthCheck.getThreshold());
                                  found.setEndpoint(updatedHealthCheck.getEndpoint());
                                  found.setInterval(updatedHealthCheck.getInterval());
                                  found.setExpression(updatedHealthCheck.getExpression());
                                  found.setName(updatedHealthCheck.getName());
                                  return healthCheckRepository.save(found);
                              }))
                .doOnSuccess(this::start);
    }

    public Mono<HealthCheck> getHealthCheckById(String id) {
        return healthCheckRepository
                .findById(id)
                .switchIfEmpty(
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                               "notFound")));
    }

    public Mono<Void> deleteHealthCheck(String id) {
        return getHealthCheckById(id)
                .then(healthCheckRepository.deleteById(id));
    }

    private Mono<Void> check(HealthCheck healthCheck) {
        return webClient
                .get()
                .uri(healthCheck.getEndpoint())
                .retrieve()
                .toEntity(Object.class)
                .map(responseEntity -> new Response(responseEntity.getStatusCode().value(),
                                                    responseEntity.getBody()))
                .filter(response -> healthCheckEvaluator
                        .check(healthCheck.getExpression(), response))
                .switchIfEmpty(Mono.error(
                        new HealthCheckExecutionException("Expression evaluated to false")))
                .onErrorMap(WebClientResponseException.class,
                            e -> new HealthCheckExecutionException(
                                    "Status from the endpoint was=" + e.getRawStatusCode()))
                .onErrorMap(ParseException.class, e -> new HealthCheckExecutionException(
                        "Could not parse expression " + e.getMessage()))
                .onErrorMap(EvaluationException.class, e -> new HealthCheckExecutionException(
                        "Could not evaluate expression " + e.getMessage()))
                .then();
    }

    private void start(HealthCheck healthCheck) {
        HealthCheckState healthCheckState = new HealthCheckState(healthCheck);
        Flux.range(0, Integer.MAX_VALUE)
                .delayElements(Duration.ofSeconds(healthCheck.getInterval()))
                .concatMap(unused -> checkStillRelevant(healthCheck)
                        .then(check(healthCheck)
                                      .onErrorResume(HealthCheckExecutionException.class, e -> {
                                          healthCheckState.onFailure();
                                          return Mono.empty();
                                      })
                                      .doOnSuccess(ignored -> healthCheckState.onSuccess())
                                      .onErrorResume(UnhealthyHealthCheckException.class,
                                                     e -> sendEmail())
                        ))
                .doOnError(NotUpToDateHealthCheckException.class, e -> System.err
                        .println("Stopping " + healthCheck + " cause it's not up to date anymore"))
                .subscribe();

    }

    private Mono<Void> sendEmail() {
        return Mono.just("sendEmail")
                .doOnSubscribe(s -> System.err.println("[EMAIL] Sending it"))
                .then();
    }

    private Mono<Void> checkStillRelevant(HealthCheck healthCheck) {
        return healthCheckRepository.findById(healthCheck.getId())
                .filter(found -> found.equals(healthCheck))
                .switchIfEmpty(Mono.error(new NotUpToDateHealthCheckException()))
                .then();
    }
}
