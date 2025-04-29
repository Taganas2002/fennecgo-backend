package com.fennec.fennecgo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")   // optional common prefix
public class HealthController {

    // record the moment this bean was created
    private final Instant startTime = Instant.now();

    @Value("${spring.application.name:fennecgo}")
    private String appName;

    @Value("${spring.application.version:1.0.0}")
    private String appVersion;

    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
            "name", appName,
            "version", appVersion
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        var runtime = ManagementFactory.getRuntimeMXBean();
        return ResponseEntity.ok(Map.of(
            "jvmName", runtime.getVmName(),
            "jvmVersion", runtime.getVmVersion(),
            "os", System.getProperty("os.name"),
            "osVersion", System.getProperty("os.version")
        ));
    }

    @GetMapping("/uptime")
    public ResponseEntity<String> uptime() {
        Duration up = Duration.between(startTime, Instant.now());
        long hours = up.toHours();
        long minutes = up.toMinutesPart();
        long seconds = up.toSecondsPart();
        return ResponseEntity.ok(
            String.format("%02dh %02dm %02ds", hours, minutes, seconds)
        );
    }

    @GetMapping("/games")
    public ResponseEntity<List<String>> listGames() {
        // replace with your DB/service lookup
        return ResponseEntity.ok(List.of(
            "Xbox",
            "PlayStation",
            "Amazon Gift Card",
            "Garena Shells"
        ));
    }

    @GetMapping("/providers")
    public ResponseEntity<List<Map<String,String>>> listProviders() {
        // stub data; in a real app query your database
        return ResponseEntity.ok(List.of(
            Map.of("code","SONELGAZ","name","Sonelgaz","category","Electricity"),
            Map.of("code","ADE","name","ADE","category","Water")
        ));
    }
}
