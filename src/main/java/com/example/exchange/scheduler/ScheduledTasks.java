package com.example.exchange.scheduler;

import com.example.exchange.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ScheduledTasks {

    private final CacheService cacheService;

    public ScheduledTasks(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "0 0 1 * * *") // Runs every day at 1 AM
    public void clearCache() {
        log.info("Clearing cache");
        try {
            cacheService.evictAllCacheEntries();
        } catch (Exception ex) {
            log.error("Cache clearing failed {}", ex.getMessage());
        }
        log.info("Cache cleared successfully");
    }
}
