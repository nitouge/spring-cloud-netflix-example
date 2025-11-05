package com.lsj.cloud.order.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRetryer implements Retryer {

    private final int maxAttempts;
    private final long backoff;
    private int attempt;

    public CustomRetryer() {
        this(1000, 3);
    }

    public CustomRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.info("Feign重试第 {} 次, 原因: {}", attempt, e.getMessage());

        if (attempt++ >= maxAttempts) {
            throw e;
        }

        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Retryer clone() {
        return new CustomRetryer(backoff, maxAttempts);
    }
}