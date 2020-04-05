package com.livv.healthchecker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.StringJoiner;

public class HealthCheck {

    @Id
    private String id;
    private String name;
    private String endpoint;
    private int interval;
    private int threshold;
    private String expression;

    @JsonCreator
    public HealthCheck(@JsonProperty("name") String name,
                       @JsonProperty("endpoint") String endpoint,
                       @JsonProperty("interval") int interval,
                       @JsonProperty("threshold") int threshold,
                       @JsonProperty("expression") String expression) {
        this.name = name;
        this.interval = interval;
        this.threshold = threshold;
        this.endpoint = endpoint;
        this.expression = expression;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getInterval() {
        return interval;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getExpression() {
        return expression;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HealthCheck that = (HealthCheck) o;
        return interval == that.interval &&
               threshold == that.threshold &&
               Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(endpoint, that.endpoint) &&
               Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, endpoint, interval, threshold, expression);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HealthCheck.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("endpoint='" + endpoint + "'")
                .add("interval=" + interval)
                .add("threshold=" + threshold)
                .add("expression='" + expression + "'")
                .toString();
    }
}
