package com.livv.healthchecker.web;

import com.livv.healthchecker.model.HealthCheck;
import com.livv.healthchecker.repository.HealthCheckRepository;
import com.livv.healthchecker.service.HealthCheckService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import reactor.core.publisher.Mono;

@Controller
@Validated
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @PostMapping("/healthcheck")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Mono<HealthCheck> createHealthCheck(@RequestBody HealthCheck healthCheck) {
        return healthCheckService.createHealthCheck(healthCheck);
    }

    @PutMapping("/healthcheck/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Mono<HealthCheck> updateHealthCheck(@PathVariable String id,
                                               @RequestBody HealthCheck updatedHealthCheck) {
        return healthCheckService.updateHealthCheck(id, updatedHealthCheck);
    }

    @GetMapping("/healthcheck/{id}")
    @ResponseBody
    public Mono<HealthCheck> getHealthCheckById(@PathVariable String id) {
        return healthCheckService.getHealthCheckById(id);
    }

    @DeleteMapping("/healthcheck/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteHealthCheck(@PathVariable String id) {
        return healthCheckService.deleteHealthCheck(id);
    }
}
