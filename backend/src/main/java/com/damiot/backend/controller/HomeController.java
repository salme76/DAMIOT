package com.damiot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "application", "DAMIOT Backend API",
                "version", "1.0.0",
                "status", "running",
                "timestamp", LocalDateTime.now(),
                "author", "Emilio José Salmerón Arjona",
                "endpoints", Map.of(
                        "sensors", "/api/sensors",
                        "actuators", "/api/actuators",
                        "device", "/api/device"
                )
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}