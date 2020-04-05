package com.livv.healthchecker.model;

import com.livv.healthchecker.exception.UnhealthyHealthCheckException;

public class HealthCheckState {
    private STATUS status;
    private int failureCounter;
    private final HealthCheck healthCheck;

    public HealthCheckState(HealthCheck healthCheck) {
        this.status = STATUS.HEALTHY;
        this.failureCounter = 0;
        this.healthCheck = healthCheck;
    }

    public void onFailure() {
        System.err.println("[FAILURE] " + healthCheck);
        failureCounter++;
        if (failureCounter > healthCheck.getThreshold()) {
            failureCounter = 0;
            status = STATUS.UNHEALTHY;
            throw new UnhealthyHealthCheckException();
        }
    }

    public void onSuccess() {
        System.err.println("[SUCCESS] " + healthCheck);
        failureCounter = 0;
        if (status == STATUS.UNHEALTHY) {
            status = STATUS.HEALTHY;
        }
    }

    enum STATUS {
        HEALTHY,
        UNHEALTHY
    }

}
