package com.example.cache.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	@GetMapping("/health")
    public Map<String, String> health(@RequestParam String nam) {
       return Map.of("status", "OK");
    }
}
