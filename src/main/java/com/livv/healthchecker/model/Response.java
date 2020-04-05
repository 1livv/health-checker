package com.livv.healthchecker.model;

import java.util.Map;
import java.util.Objects;

public class Response {
    private final int statusCode;
    private final Object body;


    public Response(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int status() {
        return statusCode;
    }

    public Object body() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Response response = (Response) o;
        return statusCode == response.statusCode &&
               Objects.equals(body, response.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, body);
    }
}
