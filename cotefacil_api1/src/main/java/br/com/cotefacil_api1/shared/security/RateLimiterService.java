package br.com.cotefacil_api1.shared.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate.limit.login.capacity-per-minute:10}")
    private int loginCapacityPerMinute;

    public boolean allowLoginForKey(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder().addLimit(
                Bandwidth.classic(loginCapacityPerMinute, Refill.greedy(loginCapacityPerMinute, Duration.ofMinutes(1)))
        ).build());
        return bucket.tryConsume(1);
    }
}
