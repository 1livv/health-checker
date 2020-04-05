package com.livv.healthchecker.service;

import com.livv.healthchecker.model.Response;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckEvaluator {

    public Boolean check(String healthCheckExpression, Response response) {
        EvaluationContext evaluationContext = new StandardEvaluationContext(response);
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(healthCheckExpression);
        return exp.getValue(evaluationContext, Boolean.class);
    }
}
