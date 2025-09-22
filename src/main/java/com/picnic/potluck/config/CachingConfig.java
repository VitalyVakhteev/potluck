package com.picnic.potluck.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@Configuration
@EnableCaching
@EnableScheduling
@RequiredArgsConstructor
public class CachingConfig {
	private final CacheManager cacheManager;

	@Scheduled(fixedRateString = "PT5M")
	public void sweep() {
		cacheManager.getCacheNames()
				.forEach(n -> Objects.requireNonNull(cacheManager.getCache(n)).invalidate());
	}
}