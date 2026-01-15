package com.example.cache.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheLockManager {
	private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

	public Object getLock(String key) {
		return locks.computeIfAbsent(key, k -> new Object());
	}
}
